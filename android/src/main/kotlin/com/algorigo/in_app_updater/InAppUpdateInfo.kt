package com.algorigo.in_app_updater

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.UpdateAvailability

internal data class InAppUpdateInfo(
  val appUpdateInfo: AppUpdateInfo? = null
) {

  fun isUpdateAvailable(): Boolean = appUpdateInfo?.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

  fun isUpdateInProgress() = appUpdateInfo?.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
}
