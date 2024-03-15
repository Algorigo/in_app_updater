package com.algorigo.in_app_updater

import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.hasTerminalStatus
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.packageName
import com.google.android.play.core.ktx.totalBytesToDownload

class InAppUpdateInstallState(
  val installState: InstallState
) {

  fun toMap() = mapOf(
    "packageName" to installState.packageName,
    "installStatus" to installState.installStatus,
    "isDownloaded" to isDownloaded(),
    "bytesDownloaded" to installState.bytesDownloaded,
    "totalBytesToDownload" to installState.totalBytesToDownload,
    "hasTerminalStatus" to installState.hasTerminalStatus,
    "installErrorCode" to installState.installErrorCode()
  )

  fun isDownloaded() = installState.installStatus() == InstallStatus.DOWNLOADED
}