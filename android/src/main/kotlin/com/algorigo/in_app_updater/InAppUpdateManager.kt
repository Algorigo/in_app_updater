package com.algorigo.in_app_updater

import android.app.Activity
import com.algorigo.in_app_updater.exceptions.InAppUpdateException
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.installStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class InAppUpdateManager(
  private val activity: Activity
) {

  private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

  private var currentInAppUpdateInstallState = InAppUpdateInstallState()

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

  fun observeInAppUpdateStatus() = callbackFlow {
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

  fun startUpdate(updateType: Int = AppUpdateType.IMMEDIATE) {
    // re-fetch app update info
    appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
      currentInAppUpdateInfo = currentInAppUpdateInfo.copy(appUpdateInfo = appUpdateInfo)

      val updateOptions = AppUpdateOptions.newBuilder(updateType).build()
      appUpdateManager.startUpdateFlowForResult(
        appUpdateInfo, activity, updateOptions, REQUEST_CODE_UPDATE
      )
    }
  }
  companion object {
    const val REQUEST_CODE_UPDATE = 5793
  }
}
