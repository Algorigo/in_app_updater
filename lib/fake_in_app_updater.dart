import 'package:in_app_updater/data/in_app_update_info.dart';

import 'in_app_updater_platform_interface.dart';

class FakeInAppUpdater {
  Future<InAppUpdateInfo> checkForUpdate() {
    return InAppUpdaterPlatform.instance.fakeCheckForUpdate();
  }

  Future<bool> checkUpdateAvailable() {
    return InAppUpdaterPlatform.instance.fakeCheckUpdateAvailable();
  }

  Future<void> startUpdateImmediate() {
    return InAppUpdaterPlatform.instance.fakeStartUpdateImmediate();
  }

  Future<void> startUpdateFlexible() {
    return InAppUpdaterPlatform.instance.fakeStartUpdateFlexible();
  }

  Future<void> completeFlexibleUpdate() {
    return InAppUpdaterPlatform.instance.fakeCompleteFlexibleUpdate();
  }

  Stream<dynamic> observeInAppUpdateInstallState() {
    return InAppUpdaterPlatform.instance.observeInAppUpdateInstallState();
  }

  Future<void> setUpdateAvailable(int isAvailable) {
    return InAppUpdaterPlatform.instance.fakeSetUpdateAvailable(isAvailable);
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
