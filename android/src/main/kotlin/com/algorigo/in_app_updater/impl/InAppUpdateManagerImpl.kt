package com.algorigo.in_app_updater.impl

import android.app.Activity
import com.algorigo.in_app_updater.InAppActivityResult
import com.algorigo.in_app_updater.InAppUpdateInfo
import com.algorigo.in_app_updater.InAppUpdateInstallState
import com.algorigo.in_app_updater.InAppUpdateManager
import com.algorigo.in_app_updater.InAppUpdateType
import com.algorigo.in_app_updater.InAppUpdateType.Companion.REQUEST_CODE_FLEXIBLE_UPDATE
import com.algorigo.in_app_updater.InAppUpdateType.Companion.REQUEST_CODE_IMMEDIATE_UPDATE
import com.algorigo.in_app_updater.callbacks.OnActivityResultListener
import com.algorigo.in_app_updater.exceptions.InAppUpdateException
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.ktx.requestCompleteUpdate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class InAppUpdateManagerImpl(
    private val activity: Activity,
    private val appUpdateManager: AppUpdateManager
) : InAppUpdateManager() {

    override suspend fun checkForUpdate(): InAppUpdateInfo = suspendCoroutine { continuation ->
        if (isPlayStoreInstalled().not()) {
            continuation.resumeWithException(InAppUpdateException.PlayStoreNotInstalledException(message = "Play Store not installed"))
            return@suspendCoroutine
        }

        if (isPlayServicesAvailable().not()) {
            continuation.resumeWithException(InAppUpdateException.PlayStoreNotAvailableException(message = "Play Services not available"))
            return@suspendCoroutine
        }

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

    override suspend fun checkUpdateAvailable(): Boolean {
        val inAppUpdateInfo = checkForUpdate()
        return inAppUpdateInfo.isUpdateAvailable()
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
                return@suspendCoroutine
            }

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

    override suspend fun requestCompleteUpdate() {
        try {
            appUpdateManager.requestCompleteUpdate()
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

        appUpdateManager.registerListener(installStateUpdatedListener)

        awaitClose {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }
}
