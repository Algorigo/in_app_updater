package com.algorigo.in_app_updater.callbacks

import com.algorigo.in_app_updater.InAppActivityResult

fun interface OnActivityResultListener {

  fun onActivityResult(result: InAppActivityResult)
}
