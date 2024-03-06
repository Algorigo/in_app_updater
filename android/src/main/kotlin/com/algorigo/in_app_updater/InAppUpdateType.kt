package com.algorigo.in_app_updater

import com.google.android.play.core.install.model.AppUpdateType

enum class InAppUpdateType(
  @AppUpdateType val value: Int
) {
  FLEXIBLE(AppUpdateType.FLEXIBLE),
  IMMEDIATE(AppUpdateType.IMMEDIATE);

  fun requestCode(): Int = when (this) {
    FLEXIBLE -> REQUEST_CODE_FLEXIBLE_UPDATE
    IMMEDIATE -> REQUEST_CODE_IMMEDIATE_UPDATE
  }

  companion object {
    const val REQUEST_CODE_IMMEDIATE_UPDATE = 5801
    const val REQUEST_CODE_FLEXIBLE_UPDATE = 5802
  }
}
