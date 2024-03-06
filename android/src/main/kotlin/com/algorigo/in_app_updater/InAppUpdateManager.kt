package com.algorigo.in_app_updater

import android.app.Activity
import com.algorigo.in_app_updater.InAppUpdateType.Companion.REQUEST_CODE_FLEXIBLE_UPDATE
import com.algorigo.in_app_updater.InAppUpdateType.Companion.REQUEST_CODE_IMMEDIATE_UPDATE
import com.algorigo.in_app_updater.callbacks.OnActivityResultListener
import com.algorigo.in_app_updater.exceptions.InAppUpdateException
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class InAppUpdateManager(
  private val activity: Activity
) {

  private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

  var onActivityResultListener: OnActivityResultListener? = null
    private set

  suspend fun checkUpdateAvailable() = suspendCoroutine {
    // re-fetch app update info
    appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
      it.resume(InAppUpdateInfo(appUpdateInfo).isUpdateAvailable())
    }
  }

  suspend fun checkForUpdate(): InAppUpdateInfo = suspendCoroutine { continuation ->
    try {
      appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
        continuation.resume(InAppUpdateInfo(appUpdateInfo))
      }.addOnFailureListener {
        continuation.resumeWithException(InAppUpdateException.CheckForUpdateFailedException(message = it.message))
      }
    } catch (e: Exception) {
      continuation.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun startUpdate(inAppUpdateType: InAppUpdateType = InAppUpdateType.IMMEDIATE): InAppActivityResult {

    val inAppUpdateInfo = checkForUpdate()

    return suspendCoroutine { continuation ->
      val listener = OnActivityResultListener { activityResult ->
        continuation.resume(activityResult)
      }

      setOnActivityResultListener(listener)

      when (inAppUpdateType) {
        InAppUpdateType.IMMEDIATE -> {
          if (inAppUpdateInfo.isImmediateUpdateAllowed) {
            val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
            appUpdateManager.startUpdateFlowForResult(
              inAppUpdateInfo.appUpdateInfo, activity, updateOptions, REQUEST_CODE_IMMEDIATE_UPDATE
            )
          } else {
            continuation.resumeWithException(InAppUpdateException.ImmediateUpdateNotAllowedException(message = "Immediate update not allowed"))
          }
        }

        InAppUpdateType.FLEXIBLE -> {
          if (inAppUpdateInfo.isFlexibleUpdateAllowed) {
            val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
            appUpdateManager.startUpdateFlowForResult(
              inAppUpdateInfo.appUpdateInfo, activity, updateOptions, REQUEST_CODE_FLEXIBLE_UPDATE
            )
          } else {
            continuation.resumeWithException(InAppUpdateException.FlexibleUpdateNotAllowedException(message = "Flexible update not allowed"))
          }
        }
      }
    }
  }
    val installStateUpdatedListener = InstallStateUpdatedListener { installState ->
      if (currentInAppUpdateInstallState.installState?.installStatus() != installState.installStatus()) {
        currentInAppUpdateInstallState = currentInAppUpdateInstallState.copy(installState = installState)
        trySend(currentInAppUpdateInfo)

        if (installState.installStatus == InstallStatus.DOWNLOADED) {
          close()
        }
      }
    }

    appUpdateManager.registerListener(installStateUpdatedListener)

    awaitClose {
      appUpdateManager.unregisterListener(installStateUpdatedListener)
    }
  }

  private fun setOnActivityResultListener(onActivityResultListener: OnActivityResultListener?) {
    this.onActivityResultListener = onActivityResultListener
  }
}
