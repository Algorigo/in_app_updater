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
import com.algorigo.in_app_updater.impl.InAppUpdateManagerImpl
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.ktx.installStatus
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
class InAppUpdaterPlugin : FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, ActivityAware, PluginRegistry.ActivityResultListener,
  Application.ActivityLifecycleCallbacks {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var methodChannel: MethodChannel
  private lateinit var eventChannel: EventChannel

  private var activity: Activity? = null

  private var inAppUpdateManager: InAppUpdateManager? = null

  private val mainScope = CoroutineScope(Dispatchers.Main)
  private val eventScope = CoroutineScope(Dispatchers.Main)

  private var lastResult: Result? = null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "in_app_updater")
    methodChannel.setMethodCallHandler(this)

    eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "in_app_updater_event")
    eventChannel.setStreamHandler(this)
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    inAppUpdateManager = InAppUpdateManagerImpl(binding.activity, AppUpdateManagerFactory.create(binding.activity))
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivity() {
    activity = null
    inAppUpdateManager = null
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "getPlatformVersion" -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }

      "checkForUpdate" -> checkForUpdate(result)
      "checkUpdateAvailable" -> checkUpdateAvailable(result)
      "startUpdateImmediate" -> startUpdateImmediate(result)
      "startUpdateFlexible" -> startUpdateFlexible(result)
      "completeFlexibleUpdate" -> completeFlexibleUpdate(result)
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    inAppUpdateManager?.onActivityResultListener?.onActivityResult(InAppActivityResult(requestCode, resultCode, data))
    return true
  }

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    eventScope.launch {
      inAppUpdateManager?.observeInAppUpdateInstallState()
        ?.collectLatest {
          events?.success(it)
        }
    }
  }

  override fun onCancel(arguments: Any?) {
    eventScope.cancel()
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

  private fun checkUpdateAvailable(result: Result) {
    mainScope.launch(Dispatchers.Main) {
      val available = inAppUpdateManager?.checkUpdateAvailable()
      result.success(available)
    }
  }

  private fun checkForUpdate(result: Result) {
    mainScope.launch(Dispatchers.Main) {
      try {
        val appUpdateInfo = inAppUpdateManager?.checkForUpdate()

        val infoMap = mapOf(
          "updateAvailability" to appUpdateInfo?.appUpdateInfo?.updateAvailability(),
          "availableVersionCode" to appUpdateInfo?.appUpdateInfo?.availableVersionCode(),
          "updatePriority" to appUpdateInfo?.appUpdateInfo?.updatePriority,
          "packageName" to appUpdateInfo?.appUpdateInfo?.packageName(),
          "clientVersionStalenessDays" to appUpdateInfo?.appUpdateInfo?.clientVersionStalenessDays(),
          "installStatus" to appUpdateInfo?.appUpdateInfo?.installStatus,
          "isFlexibleUpdateAllowed" to appUpdateInfo?.isFlexibleUpdateAllowed,
          "isFlexibleUpdateFailedPreconditions" to appUpdateInfo
            ?.appUpdateInfo
            ?.getFailedUpdatePreconditions(AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE))
            ?.map { it.toInt() }
            ?.toList(),
          "isImmediateUpdateAllowed" to appUpdateInfo?.isImmediateUpdateAllowed,
          "isImmediateUpdateFailedPreconditions" to appUpdateInfo
            ?.appUpdateInfo
            ?.getFailedUpdatePreconditions(AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE))
            ?.map { it.toInt() }
            ?.toList(),
          "bytesDownloaded" to appUpdateInfo?.appUpdateInfo?.bytesDownloaded(),
          "totalBytesToDownload" to appUpdateInfo?.appUpdateInfo?.totalBytesToDownload(),
        )
        result.success(infoMap)
      } catch (e: InAppUpdateException) {
        result.error(e.code.toString(), e.message, null)
      } catch (e: Exception) {
        result.error("fetch app update info failed", e.message, null)
      }
    }
  }

  private fun startUpdateImmediate(result: Result?) {
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

  private fun startUpdateFlexible(result: Result) {
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

  private fun completeFlexibleUpdate(result: Result) {
    try {
      inAppUpdateManager?.completeFlexibleUpdate()
      result.success(Unit)
    } catch (e: InAppUpdateException) {
      result.error(e.code.toString(), e.message, e.stackTrace)
    } catch (e: Exception) {
      result.error(e.message.toString(), e.cause.toString(), null)
    }
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
          startUpdateImmediate(lastResult)
        }
      }
    }
  }
}
