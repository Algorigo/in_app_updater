package com.algorigo.in_app_updater.fake

import android.app.Activity
import androidx.annotation.IntRange
import com.algorigo.in_app_updater.InAppActivityResult
import com.algorigo.in_app_updater.InAppUpdateInfo
import com.algorigo.in_app_updater.InAppUpdateInstallState
import com.algorigo.in_app_updater.InAppUpdateManager
import com.algorigo.in_app_updater.InAppUpdateType
import com.algorigo.in_app_updater.callbacks.OnActivityResultListener
import com.algorigo.in_app_updater.exceptions.InAppUpdateException
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallErrorCode
import com.google.android.play.core.ktx.requestCompleteUpdate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FakeInAppUpdateManager(
  private val activity: Activity,
  private val fakeAppUpdateManager: FakeAppUpdateManager
) : InAppUpdateManager() {

  val isConfirmationDialogVisible
    get() = fakeAppUpdateManager.isConfirmationDialogVisible

  val isImmediateFlowVisible
    get() = fakeAppUpdateManager.isImmediateFlowVisible

  val isInstallSplashScreenVisible
    get() = fakeAppUpdateManager.isInstallSplashScreenVisible

  val typeForUpdateInProgress
    get() = fakeAppUpdateManager.typeForUpdateInProgress

  override suspend fun checkUpdateAvailable() = suspendCoroutine {
    // re-fetch app update info
    fakeAppUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
      it.resume(InAppUpdateInfo(appUpdateInfo).isUpdateAvailable())
    }
  }

  override suspend fun checkForUpdate(): InAppUpdateInfo = suspendCoroutine { continuation ->
    try {
      fakeAppUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
        continuation.resume(InAppUpdateInfo(appUpdateInfo))
      }.addOnFailureListener {
        continuation.resumeWithException(InAppUpdateException.CheckForUpdateFailedException(message = it.message))
      }
    } catch (e: Exception) {
      continuation.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  override suspend fun startUpdate(inAppUpdateType: InAppUpdateType): InAppActivityResult {
    val inAppUpdateInfo = checkForUpdate()

    return suspendCoroutine { continuation ->
      val listener = OnActivityResultListener { activityResult ->
        continuation.resume(activityResult)
      }

      setOnActivityResultListener(listener)

      if (inAppUpdateInfo.isUpdateAvailable().not()) {
        continuation.resumeWithException(InAppUpdateException.UpdateNotAvailableException(message = "Update not available"))
      }

      when (inAppUpdateType) {
        InAppUpdateType.IMMEDIATE -> {
          if (inAppUpdateInfo.isImmediateUpdateAllowed) {
            val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
            fakeAppUpdateManager.startUpdateFlowForResult(
              inAppUpdateInfo.appUpdateInfo, activity, updateOptions, InAppUpdateType.REQUEST_CODE_IMMEDIATE_UPDATE
            )
          } else {
            continuation.resumeWithException(InAppUpdateException.ImmediateUpdateNotAllowedException(message = "Immediate update not allowed"))
          }
        }

        InAppUpdateType.FLEXIBLE -> {
          if (inAppUpdateInfo.isFlexibleUpdateAllowed) {
            val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
            fakeAppUpdateManager.startUpdateFlowForResult(
              inAppUpdateInfo.appUpdateInfo, activity, updateOptions, InAppUpdateType.REQUEST_CODE_FLEXIBLE_UPDATE
            )
          } else {
            continuation.resumeWithException(InAppUpdateException.FlexibleUpdateNotAllowedException(message = "Flexible update not allowed"))
          }
        }
      }
    }
  }

  override suspend fun requestCompleteUpdate() {
    try {
      fakeAppUpdateManager.requestCompleteUpdate()
    } catch (e: Exception) {
      throw InAppUpdateException.CompleteFlexibleUpdateException(message = e.message)
    }
  }

  override fun observeInAppUpdateInstallState(): Flow<InAppUpdateInstallState> = callbackFlow {
    var currentInAppUpdateInstallState: InAppUpdateInstallState? = null
    val installStateUpdatedListener = InstallStateUpdatedListener { installState ->
      if (currentInAppUpdateInstallState?.installState?.installStatus() != installState.installStatus()) {
        InAppUpdateInstallState(installState).also {
          currentInAppUpdateInstallState = it
          trySend(it)
        }
      }
    }

    fakeAppUpdateManager.registerListener(installStateUpdatedListener)

    awaitClose {
      fakeAppUpdateManager.unregisterListener(installStateUpdatedListener)
    }
  }

  suspend fun setUpdateAvailable(availableVersionCode: Int, @AppUpdateType appUpdateType: Int = AppUpdateType.IMMEDIATE) = suspendCoroutine {
    try {
      fakeAppUpdateManager.setUpdateAvailable(availableVersionCode, appUpdateType)
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun setUpdateNotAvailable() = suspendCoroutine {
    try {
      fakeAppUpdateManager.setUpdateNotAvailable()
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun userAcceptsUpdate() = suspendCoroutine {
    try {
      fakeAppUpdateManager.userAcceptsUpdate()
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun userRejectsUpdate() = suspendCoroutine {
    try {
      fakeAppUpdateManager.userRejectsUpdate()
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun setUpdatePriority(@IntRange(from = 1, to = 5) priority: Int) = suspendCoroutine {
    try {
      fakeAppUpdateManager.setUpdatePriority(priority)
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun setClientVersionStalenessDays(stalenessDays: Int) = suspendCoroutine {
    try {
      fakeAppUpdateManager.setClientVersionStalenessDays(stalenessDays)
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun getTypeForUpdate(): InAppUpdateType = suspendCoroutine {
    try {
      it.resume(InAppUpdateType.fromValue(fakeAppUpdateManager.typeForUpdateInProgress))
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun setTotalBytesToDownload(totalBytesToDownload: Long) = suspendCoroutine {
    try {
      fakeAppUpdateManager.setTotalBytesToDownload(totalBytesToDownload)
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun setBytesDownloaded(bytesDownloaded: Long) = suspendCoroutine {
    try {
      fakeAppUpdateManager.setBytesDownloaded(bytesDownloaded)
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun downloadStarts() = suspendCoroutine {
    try {
      fakeAppUpdateManager.downloadStarts()
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun userCancelsDownload() = suspendCoroutine {
    try {
      fakeAppUpdateManager.userCancelsDownload()
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun downloadCompletes() = suspendCoroutine {
    try {
      fakeAppUpdateManager.downloadCompletes()
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun downloadFails() = suspendCoroutine {
    try {
      fakeAppUpdateManager.downloadFails()
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun installCompletes() = suspendCoroutine {
    try {
      fakeAppUpdateManager.installCompletes()
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun installFails() = suspendCoroutine {
    try {
      fakeAppUpdateManager.installFails()
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }

  suspend fun setInstallErrorCode(@InstallErrorCode errorCode: Int) = suspendCoroutine {
    try {
      fakeAppUpdateManager.setInstallErrorCode(errorCode)
      it.resume(Unit)
    } catch (e: Exception) {
      it.resumeWithException(InAppUpdateException.UnExpectedException(message = e.message))
    }
  }
}
