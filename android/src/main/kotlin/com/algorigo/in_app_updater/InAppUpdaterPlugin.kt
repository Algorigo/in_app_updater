package com.algorigo.in_app_updater

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.algorigo.in_app_updater.exceptions.InAppUpdateException
import com.algorigo.in_app_updater.exceptions.OnActivityResultException
import com.algorigo.in_app_updater.InAppUpdateType.Companion.REQUEST_CODE_IMMEDIATE_UPDATE
import com.algorigo.in_app_updater.InAppUpdateType.Companion.REQUEST_CODE_FLEXIBLE_UPDATE
import com.algorigo.in_app_updater.fake.FakeInAppUpdateManager
import com.algorigo.in_app_updater.impl.InAppUpdateManagerImpl
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.hasTerminalStatus
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.packageName
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.android.play.core.ktx.updatePriority
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** InAppUpdaterPlugin */
class InAppUpdaterPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener,
  Application.ActivityLifecycleCallbacks {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var methodChannel: MethodChannel
  private lateinit var eventChannel: EventChannel
  private lateinit var fakeEventChannel: EventChannel

  private var activity: Activity? = null

  private var inAppUpdateManager: InAppUpdateManager? = null
  private var fakeInAppUpdateManager: FakeInAppUpdateManager? = null

  private val mainScope = CoroutineScope(Dispatchers.Main)
  private val eventScope = CoroutineScope(Dispatchers.Main)
  private val fakeEventScope = CoroutineScope(Dispatchers.Main)

  private var lastResult: Result? = null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "in_app_updater")
    methodChannel.setMethodCallHandler(this)

    eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "in_app_updater_event")
    eventChannel.setStreamHandler(object : EventChannel.StreamHandler {
      override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventScope.launch {
          inAppUpdateManager?.observeInAppUpdateInstallState()
            ?.collectLatest {
              events?.success(it.toMap())
            }
        }
      }

      override fun onCancel(arguments: Any?) {
        eventScope.cancel()
      }
    })

    fakeEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "in_app_updater_fake_event")
    fakeEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
      override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        fakeEventScope.launch {
          fakeInAppUpdateManager?.observeInAppUpdateInstallState()
            ?.collectLatest {
              events?.success(it.toMap())
            }
        }
      }

      override fun onCancel(arguments: Any?) {
        fakeEventScope.cancel()
      }
    })
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    inAppUpdateManager = InAppUpdateManagerImpl(binding.activity, AppUpdateManagerFactory.create(binding.activity))
    fakeInAppUpdateManager = FakeInAppUpdateManager(binding.activity, FakeAppUpdateManager(binding.activity))
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivity() {
    activity = null
    inAppUpdateManager = null
    fakeInAppUpdateManager = null
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "getPlatformVersion" -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }

      "checkForUpdate" -> checkForUpdate(inAppUpdateManager, result)
      "checkUpdateAvailable" -> checkUpdateAvailable(inAppUpdateManager, result)
      "startUpdateImmediate" -> startUpdateImmediate(inAppUpdateManager, result)
      "startUpdateFlexible" -> startUpdateFlexible(inAppUpdateManager, result)
      "completeFlexibleUpdate" -> completeFlexibleUpdate(inAppUpdateManager, result)

      "fakeCheckForUpdate" -> checkForUpdate(fakeInAppUpdateManager, result)
      "fakeCheckUpdateAvailable" -> checkUpdateAvailable(fakeInAppUpdateManager, result)
      "fakeStartUpdateImmediate" -> startUpdateImmediate(fakeInAppUpdateManager, result)
      "fakeStartUpdateFlexible" -> startUpdateFlexible(fakeInAppUpdateManager, result)
      "fakeCompleteFlexibleUpdate" -> completeFlexibleUpdate(fakeInAppUpdateManager, result)
      "fakeSetUpdateAvailable" -> fakeSetUpdateAvailable(call, result)
      "fakeSetUpdateNotAvailable" -> fakeSetUpdateNotAvailable(result)
      "fakeUserAcceptsUpdate" -> fakeUserAcceptsUpdate(result)
      "fakeSetUserRejectsUpdate" -> fakeSetUserRejectsUpdate(result)
      "fakeSetUpdatePriority" -> fakeSetUpdatePriority(call, result)
      "fakeSetClientVersionStalenessDays" -> fakeSetClientVersionStalenessDays(call, result)
      "fakeGetTypeForUpdate" -> fakeGetTypeForUpdate(result)
      "fakeSetTotalBytesToDownload" -> fakeSetTotalBytesToDownload(call, result)
      "fakeSetBytesDownloaded" -> fakeSetBytesDownloaded(call, result)
      "fakeDownloadStarts" -> fakeDownloadStarts(result)
      "fakeDownloadCompletes" -> fakeDownloadCompletes(result)
      "fakeDownloadFails" -> fakeDownloadFails(result)
      "fakeInstallCompletes" -> fakeInstallCompletes(result)
      "fakeInstallFails" -> fakeInstallFails(result)
      "fakeSetInstallErrorCode" -> fakeSetInstallErrorCode(call, result)
      "fakeIsConfirmationDialogVisible" -> fakeIsConfirmationDialogVisible(result)
      "fakeIsImmediateFlowVisible" -> fakeIsImmediateFlowVisible(result)
      "fakeIsInstallSplashScreenVisible" -> isInstallSplashScreenVisible(result)
      "fakeTypeForUpdateInProgress" -> typeForUpdateInProgress(result)
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    inAppUpdateManager?.onActivityResultListener?.onActivityResult(InAppActivityResult(requestCode, resultCode, data))
    return true
  }

  private fun onActivityResult(result: Result?, inAppActivityResult: InAppActivityResult) {
    when (inAppActivityResult.requestCode) {
      REQUEST_CODE_IMMEDIATE_UPDATE -> {
        when (inAppActivityResult.resultCode) {
          RESULT_CANCELED -> {
            result?.error(
              OnActivityResultException.IMMEDIATE_UPDATE_CANCELED_EXCEPTION.code.toString(),
              OnActivityResultException.IMMEDIATE_UPDATE_CANCELED_EXCEPTION.message,
              null
            )
          }

          ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
            result?.error(
              OnActivityResultException.IMMEDIATE_UPDATE_FAILED_EXCEPTION.code.toString(),
              OnActivityResultException.IMMEDIATE_UPDATE_FAILED_EXCEPTION.message,
              null
            )
          }
        }
        lastResult = null
      }

      REQUEST_CODE_FLEXIBLE_UPDATE -> {
        when (inAppActivityResult.resultCode) {
          RESULT_OK -> {
            result?.success(Unit)
          }

          RESULT_CANCELED -> {
            result?.error(
              OnActivityResultException.FLEXIBLE_UPDATE_CANCELED_EXCEPTION.code.toString(),
              OnActivityResultException.FLEXIBLE_UPDATE_CANCELED_EXCEPTION.message,
              null
            )
            lastResult = null
          }

          ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
            result?.error(
              OnActivityResultException.FLEXIBLE_UPDATE_FAILED_EXCEPTION.code.toString(),
              OnActivityResultException.FLEXIBLE_UPDATE_CANCELED_EXCEPTION.message,
              null
            )
            lastResult = null
          }
        }
      }
    }
  }

  private fun checkForUpdate(inAppUpdateManager: InAppUpdateManager?, result: Result) {
    mainScope.launch(Dispatchers.Main) {
      try {
        val appUpdateInfo = inAppUpdateManager?.checkForUpdate()
        result.success(appUpdateInfo?.toMap())
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, null)
      } catch (e: Exception) {
        result.error("fetch app update info failed", e.message, null)
      }
    }
  }

  private fun checkUpdateAvailable(inAppUpdateManager: InAppUpdateManager?, result: Result) {
    mainScope.launch(Dispatchers.Main) {
      val available = inAppUpdateManager?.checkUpdateAvailable()
      result.success(available)
    }
  }

  private fun startUpdateImmediate(inAppUpdateManager: InAppUpdateManager?, result: Result?) {
    mainScope.launch {
      try {
        lastResult = result
        inAppUpdateManager?.startUpdate(InAppUpdateType.IMMEDIATE)?.also {
          onActivityResult(result, it)
        }
      } catch (e: InAppUpdateException) {
        result?.error(e.code.toString(), e.message, null)
      } catch (e: Exception) {
        result?.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun startUpdateFlexible(inAppUpdateManager: InAppUpdateManager?, result: Result) {
    mainScope.launch {
      try {
        lastResult = result
        inAppUpdateManager?.startUpdate(InAppUpdateType.FLEXIBLE)?.also {
          onActivityResult(result, it)
        }
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, null)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun completeFlexibleUpdate(inAppUpdateManager: InAppUpdateManager?, result: Result) {
    mainScope.launch {
      try {
        inAppUpdateManager?.requestCompleteUpdate()
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeSetUpdateAvailable(call: MethodCall, result: Result) {
    mainScope.launch {
      try {
        val availability = call.argument<Int>("availability")
        requireNotNull(availability) { "availability is required" }
        fakeInAppUpdateManager?.setUpdateAvailable(availability)
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeSetUpdateNotAvailable(result: Result) {
    mainScope.launch {
      try {
        fakeInAppUpdateManager?.setUpdateNotAvailable()
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeUserAcceptsUpdate(result: Result) {
    mainScope.launch {
      try {
        fakeInAppUpdateManager?.userAcceptsUpdate()
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeSetUserRejectsUpdate(result: Result) {
    mainScope.launch {
      try {
        fakeInAppUpdateManager?.userRejectsUpdate()
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeSetUpdatePriority(call: MethodCall, result: Result) {
    mainScope.launch {
      try {
        val priority = call.argument<Int>("priority")
        requireNotNull(priority) { "priority is required" }
        fakeInAppUpdateManager?.setUpdatePriority(priority)
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeSetClientVersionStalenessDays(call: MethodCall, result: Result) {
    mainScope.launch {
      try {
        val days = call.argument<Int>("stalenessDays")
        requireNotNull(days) { "stalenessDays is required" }
        fakeInAppUpdateManager?.setClientVersionStalenessDays(days)
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeGetTypeForUpdate(result: Result) {
    mainScope.launch {
      try {
        val type = fakeInAppUpdateManager?.getTypeForUpdate()
        result.success(type?.value)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeSetTotalBytesToDownload(methodCall: MethodCall, result: Result) {
    mainScope.launch {
      try {
        val bytes = methodCall.argument<Long>("totalBytesToDownload")
        requireNotNull(bytes) { "totalBytesToDownload is required" }
        fakeInAppUpdateManager?.setTotalBytesToDownload(bytes)
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeSetBytesDownloaded(methodCall: MethodCall, result: Result) {
    mainScope.launch {
      try {
        val bytes = methodCall.argument<Long>("bytesDownloaded")
        requireNotNull(bytes) { "bytesDownloaded is required" }
        fakeInAppUpdateManager?.setBytesDownloaded(bytes)
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeDownloadStarts(result: Result) {
    mainScope.launch {
      try {
        fakeInAppUpdateManager?.downloadStarts()
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeDownloadCompletes(result: Result) {
    mainScope.launch {
      try {
        fakeInAppUpdateManager?.downloadCompletes()
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeDownloadFails(result: Result) {
    mainScope.launch {
      try {
        fakeInAppUpdateManager?.downloadFails()
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeInstallCompletes(result: Result) {
    mainScope.launch {
      try {
        fakeInAppUpdateManager?.installCompletes()
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeInstallFails(result: Result) {
    mainScope.launch {
      try {
        fakeInAppUpdateManager?.installFails()
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeSetInstallErrorCode(call: MethodCall, result: Result) {
    mainScope.launch {
      try {
        val errorCode = call.argument<Int>("errorCode")
        requireNotNull(errorCode) { "errorCode is required" }
        fakeInAppUpdateManager?.setInstallErrorCode(errorCode)
        result.success(Unit)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, e.stackTrace)
      } catch (e: Exception) {
        result.error(e.message.toString(), e.cause.toString(), null)
      }
    }
  }

  private fun fakeIsConfirmationDialogVisible(result: Result) {
    result.success(fakeInAppUpdateManager?.isConfirmationDialogVisible)
  }

  private fun fakeIsImmediateFlowVisible(result: Result) {
    result.success(fakeInAppUpdateManager?.isImmediateFlowVisible)
  }

  private fun isInstallSplashScreenVisible(result: Result) {
    result.success(fakeInAppUpdateManager?.isInstallSplashScreenVisible)
  }

  private fun typeForUpdateInProgress(result: Result) {
    result.success(fakeInAppUpdateManager?.typeForUpdateInProgress)
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
  override fun onActivityStarted(activity: Activity) {}
  override fun onActivityPaused(activity: Activity) {}
  override fun onActivityStopped(activity: Activity) {}
  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
  override fun onActivityDestroyed(activity: Activity) {}

  override fun onActivityResumed(activity: Activity) {
    mainScope.launch {
      inAppUpdateManager?.checkForUpdate()?.also { inAppUpdateInfo ->
        if (inAppUpdateInfo.isUpdateInProgress()) {
          startUpdateImmediate(inAppUpdateManager, lastResult)
        }
      }
    }

    mainScope.launch {
      fakeInAppUpdateManager?.checkForUpdate()?.also { inAppUpdateInfo ->
        if (inAppUpdateInfo.isUpdateInProgress()) {
          startUpdateImmediate(fakeInAppUpdateManager, lastResult)
        }
      }
    }
  }
}
