package com.algorigo.in_app_updater

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed

class InAppUpdateInfo(
  val appUpdateInfo: AppUpdateInfo
) {

  val isImmediateUpdateAllowed: Boolean
    get() = appUpdateInfo.isImmediateUpdateAllowed

  val isFlexibleUpdateAllowed: Boolean
    get() = appUpdateInfo.isFlexibleUpdateAllowed

  fun isUpdateAvailable(): Boolean = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

  fun isUpdateInProgress(): Boolean = appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
}
