package com.algorigo.in_app_updater

import com.algorigo.in_app_updater.callbacks.OnActivityResultListener
import kotlinx.coroutines.flow.Flow


abstract class InAppUpdateManager {

    var onActivityResultListener: OnActivityResultListener? = null
        private set

    abstract suspend fun checkForUpdate(): InAppUpdateInfo

    abstract suspend fun checkUpdateAvailable(): Boolean

    abstract suspend fun startUpdate(inAppUpdateType: InAppUpdateType = InAppUpdateType.IMMEDIATE): InAppActivityResult

    abstract suspend fun requestCompleteUpdate()

    abstract fun observeInAppUpdateInstallState(): Flow<InAppUpdateInstallState>

    protected fun setOnActivityResultListener(onActivityResultListener: OnActivityResultListener?) {
        this.onActivityResultListener = onActivityResultListener
    }

    protected fun isPlayStoreInstalled(): Boolean {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activity.packageManager.getPackageInfo("com.android.vending", PackageManager.PackageInfoFlags.of(0))
            } else {
                activity.packageManager.getPackageInfo("com.android.vending", 0)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.i(TAG, "Play Store not installed.")
            return false
        }
        return true
    }

    protected fun isPlayServicesAvailable(): Boolean {
        val availability = GoogleApiAvailability.getInstance()
        if (availability.isGooglePlayServicesAvailable(activity) != ConnectionResult.SUCCESS) {
            Log.i(TAG, "Google Play Services not available")
            return false
        }
        return true
    }
}
