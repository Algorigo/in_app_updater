package com.algorigo.in_app_updater

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.algorigo.in_app_updater.InAppUpdateManager.Companion.REQUEST_CODE_UPDATE
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.updatePriority
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** InAppUpdaterPlugin */
class InAppUpdaterPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener,
  Application.ActivityLifecycleCallbacks {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel: MethodChannel

  private var applicationContext: Context? = null
  private var activity: Activity? = null

  private var appUpdateManager: AppUpdateManager? = null
  private var inAppUpdateManager: InAppUpdateManager? = null

  private val mainScope = CoroutineScope(Dispatchers.Main)

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "in_app_updater")
    channel.setMethodCallHandler(this)
    applicationContext = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "getPlatformVersion" -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }
      "checkForUpdate" -> checkForUpdate(result)
      "checkUpdateAvailable" -> checkUpdateAvailable(result)
      "startUpdate" -> {
        val updateType = call.argument<Int>("updateType") ?: AppUpdateType.IMMEDIATE
        inAppUpdateManager?.startUpdate(updateType)
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    applicationContext = null
    appUpdateManager = null
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    inAppUpdateManager = InAppUpdateManager(activity!!)
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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    return false
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
          "updateAvailability" to appUpdateInfo?.updateAvailability(),
          "availableVersionCode" to appUpdateInfo?.availableVersionCode(),
          "updatePriority" to appUpdateInfo?.updatePriority,
          "packageName" to appUpdateInfo?.packageName(),
          "clientVersionStalenessDays" to appUpdateInfo?.clientVersionStalenessDays(),
          "installStatus" to appUpdateInfo?.installStatus,
          "isFlexibleUpdateAllowed" to appUpdateInfo?.isFlexibleUpdateAllowed,
          "isFlexibleUpdateFailedPreconditions" to appUpdateInfo
            ?.getFailedUpdatePreconditions(AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE))
            ?.map { it.toInt() }
            ?.toList(),
          "isImmediateUpdateAllowed" to appUpdateInfo?.isImmediateUpdateAllowed,
          "isImmediateUpdateFailedPreconditions" to appUpdateInfo
            ?.getFailedUpdatePreconditions(AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE))
            ?.map { it.toInt() }
            ?.toList(),
          "bytesDownloaded" to appUpdateInfo?.bytesDownloaded(),
          "totalBytesToDownload" to appUpdateInfo?.totalBytesToDownload(),
        )
        result.success(infoMap)
      } catch (e: Exception) {
        result.error("fetch app update info failed", e.message, null)
      }
    }
  }

  fun showImmediateUpdate(appUpdateInfo: AppUpdateInfo, appUpdateType: AppUpdateType) {

    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
      appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS ||
      appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED
    ) {

      appUpdateManager?.startUpdateFlowForResult(
        appUpdateInfo,
        activity!!,
        AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE),
        REQUEST_CODE_UPDATE
      )
    }
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
  override fun onActivityStarted(activity: Activity) {}
  override fun onActivityPaused(activity: Activity) {}
  override fun onActivityStopped(activity: Activity) {}
  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
  override fun onActivityDestroyed(activity: Activity) {}

  override fun onActivityResumed(activity: Activity) {}
}
