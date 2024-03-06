package com.algorigo.in_app_updater

import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.InstallStatus

class InAppUpdateInstallState(
  val installState: InstallState
) {

  fun isDownloaded() = installState.installStatus() == InstallStatus.DOWNLOADED
}