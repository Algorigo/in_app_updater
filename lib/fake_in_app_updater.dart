import 'package:flutter/services.dart';
import 'package:in_app_updater/data/app_update_type.dart';
import 'package:in_app_updater/data/in_app_update_info.dart';
import 'package:in_app_updater/data/in_app_update_install_state.dart';
import 'package:in_app_updater/result/in_app_update_result.dart';

import 'in_app_updater_platform_interface.dart';

class FakeInAppUpdater {
  Future<InAppUpdateInfo> checkForUpdate() {
    return InAppUpdaterPlatform.instance.fakeCheckForUpdate();
  }

  Future<bool> checkUpdateAvailable() {
    return InAppUpdaterPlatform.instance.fakeCheckUpdateAvailable();
  }

  Future<void> startUpdateImmediate() {
    return InAppUpdaterPlatform.instance.fakeStartUpdateImmediate().onError((error, stackTrace) {
      if (error is PlatformException) {
        if (InAppUpdateResult.of(error.code, error.message, error.details) != null) {
          throw InAppUpdateResult.of(error.code, error.message, error.details)!;
        }
      }
    });
  }

  Future<void> startUpdateFlexible() {
    return InAppUpdaterPlatform.instance.fakeStartUpdateFlexible().onError((error, stackTrace) {
      if (error is PlatformException) {
        if (InAppUpdateResult.of(error.code, error.message, error.details) != null) {
          throw InAppUpdateResult.of(error.code, error.message, error.details)!;
        }
      }
    });
  }

  Future<void> completeFlexibleUpdate() {
    return InAppUpdaterPlatform.instance.fakeCompleteFlexibleUpdate();
  }

  Stream<InAppUpdateInstallState> observeInAppUpdateInstallState() {
    return InAppUpdaterPlatform.instance.fakeObserveInAppUpdateInstallState();
  }

  Future<void> setUpdateAvailable(int availableVersionCode, AppUpdateType appUpdateType) {
    return InAppUpdaterPlatform.instance.fakeSetUpdateAvailable(availableVersionCode, appUpdateType);
  }

  Future<void> setUpdateNotAvailable() {
    return InAppUpdaterPlatform.instance.fakeSetUpdateNotAvailable();
  }

  Future<void> userAcceptsUpdate() {
    return InAppUpdaterPlatform.instance.fakeUserAcceptsUpdate();
  }

  Future<void> userRejectsUpdate() {
    return InAppUpdaterPlatform.instance.fakeUserRejectsUpdate();
  }

  Future<void> setClientVersionStalenessDays(int days) {
    return InAppUpdaterPlatform.instance.fakeSetClientVersionStalenessDays(days);
  }

  Future<int> getTypeForUpdate() {
    return InAppUpdaterPlatform.instance.fakeGetTypeForUpdate();
  }

  Future<void> setTotalBytesToDownload(int bytes) {
    return InAppUpdaterPlatform.instance.fakeSetTotalBytesToDownload(bytes);
  }

  Future<void> setBytesDownloaded(int bytes) {
    return InAppUpdaterPlatform.instance.fakeSetBytesDownloaded(bytes);
  }

  Future<void> setDownloadStarts() {
    return InAppUpdaterPlatform.instance.fakeDownloadStarts();
  }

  Future<void> setDownloadCompletes() {
    return InAppUpdaterPlatform.instance.fakeDownloadCompletes();
  }

  Future<void> setFakeUserCancelsDownload() {
    return InAppUpdaterPlatform.instance.fakeUserCancelsDownload();
  }

  Future<void> setDownloadFails() {
    return InAppUpdaterPlatform.instance.fakeDownloadFails();
  }

  Future<void> setInstallCompletes() {
    return InAppUpdaterPlatform.instance.fakeInstallCompletes();
  }

  Future<void> setInstallFails() {
    return InAppUpdaterPlatform.instance.fakeInstallFails();
  }

  Future<void> setInstallErrorCde(int priority) {
    return InAppUpdaterPlatform.instance.fakeSetUpdatePriority(priority);
  }

  Future<bool> isConfirmationDialogVisible() {
    return InAppUpdaterPlatform.instance.fakeIsConfirmationDialogVisible();
  }

  Future<bool> isImmediateFlowVisible() {
    return InAppUpdaterPlatform.instance.fakeIsImmediateFlowVisible();
  }

  Future<bool> isInstallSplashScreenVisible() {
    return InAppUpdaterPlatform.instance.fakeIsInstallSplashScreenVisible();
  }

  Future<int> typeForUpdateInProgress() {
    return InAppUpdaterPlatform.instance.fakeTypeForUpdateInProgress();
  }
}
