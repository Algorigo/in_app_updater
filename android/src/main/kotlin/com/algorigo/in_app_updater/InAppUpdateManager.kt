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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class InAppUpdateManager {

  var onActivityResultListener: OnActivityResultListener? = null
    private set

  abstract suspend fun checkUpdateAvailable(): Boolean

  abstract suspend fun checkForUpdate(): InAppUpdateInfo

  abstract suspend fun startUpdate(inAppUpdateType: InAppUpdateType = InAppUpdateType.IMMEDIATE): InAppActivityResult

  abstract suspend fun requestCompleteUpdate()

  abstract fun observeInAppUpdateInstallState(): Flow<InAppUpdateInstallState>

  protected fun setOnActivityResultListener(onActivityResultListener: OnActivityResultListener?) {
    this.onActivityResultListener = onActivityResultListener
  }
}
