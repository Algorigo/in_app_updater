package com.algorigo.in_app_updater

import android.content.Intent

data class InAppActivityResult(val requestCode: Int, val resultCode: Int, val data: Intent?)
