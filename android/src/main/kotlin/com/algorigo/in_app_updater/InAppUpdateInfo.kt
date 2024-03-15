package com.algorigo.in_app_updater

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.updatePriority

class InAppUpdateInfo(
  val appUpdateInfo: AppUpdateInfo
) {

  fun toMap() = mapOf(
    "updateAvailability" to appUpdateInfo.updateAvailability(),
    "availableVersionCode" to appUpdateInfo.availableVersionCode(),
    "updatePriority" to appUpdateInfo.updatePriority,
    "packageName" to appUpdateInfo.packageName(),
    "clientVersionStalenessDays" to appUpdateInfo.clientVersionStalenessDays(),
    "installStatus" to appUpdateInfo.installStatus,
    "isFlexibleUpdateAllowed" to isFlexibleUpdateAllowed,
    "isFlexibleUpdateFailedPreconditions" to appUpdateInfo
      .getFailedUpdatePreconditions(AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE))
      ?.map { it.toInt() }
      ?.toList(),
    "isImmediateUpdateAllowed" to isImmediateUpdateAllowed,
    "isImmediateUpdateFailedPreconditions" to appUpdateInfo
      .getFailedUpdatePreconditions(AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE))
      ?.map { it.toInt() }
      ?.toList(),
    "bytesDownloaded" to appUpdateInfo.bytesDownloaded(),
    "totalBytesToDownload" to appUpdateInfo.totalBytesToDownload(),
  )

  val isImmediateUpdateAllowed: Boolean
    get() = appUpdateInfo.isImmediateUpdateAllowed

  val isFlexibleUpdateAllowed: Boolean
    get() = appUpdateInfo.isFlexibleUpdateAllowed

  fun isUpdateAvailable(): Boolean = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

  fun isUpdateInProgress(): Boolean = appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
}
