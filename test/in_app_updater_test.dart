import 'package:flutter_test/flutter_test.dart';
import 'package:in_app_updater/data/app_update_type.dart';
import 'package:in_app_updater/data/in_app_update_info.dart';
import 'package:in_app_updater/data/in_app_update_install_state.dart';
import 'package:in_app_updater/in_app_updater.dart';
import 'package:in_app_updater/in_app_updater_platform_interface.dart';
import 'package:in_app_updater/in_app_updater_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockInAppUpdaterPlatform
    with MockPlatformInterfaceMixin
    implements InAppUpdaterPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<InAppUpdateInfo> checkForUpdate() {
    // TODO: implement checkForUpdate
    throw UnimplementedError();
  }

  @override
  Future<bool> checkUpdateAvailable() {
    // TODO: implement checkUpdateAvailable
    throw UnimplementedError();
  }

  @override
  Future<void> completeFlexibleUpdate() {
    // TODO: implement completeFlexibleUpdate
    throw UnimplementedError();
  }

  @override
  Future<InAppUpdateInfo> fakeCheckForUpdate() {
    // TODO: implement fakeCheckForUpdate
    throw UnimplementedError();
  }

  @override
  Future<bool> fakeCheckUpdateAvailable() {
    // TODO: implement fakeCheckUpdateAvailable
    throw UnimplementedError();
  }

  @override
  Future<void> fakeCompleteFlexibleUpdate() {
    // TODO: implement fakeCompleteFlexibleUpdate
    throw UnimplementedError();
  }

  @override
  Future<void> fakeDownloadCompletes() {
    // TODO: implement fakeDownloadCompletes
    throw UnimplementedError();
  }

  @override
  Future<void> fakeUserCancelsDownload() {
    // TODO: implement fakeUserCancelsDownload
    throw UnimplementedError();
  }

  @override
  Future<void> fakeDownloadFails() {
    // TODO: implement fakeDownloadFails
    throw UnimplementedError();
  }

  @override
  Future<void> fakeDownloadStarts() {
    // TODO: implement fakeDownloadStarts
    throw UnimplementedError();
  }

  @override
  Future<int> fakeGetTypeForUpdate() {
    // TODO: implement fakeGetTypeForUpdate
    throw UnimplementedError();
  }

  @override
  Future<void> fakeInstallCompletes() {
    // TODO: implement fakeInstallCompletes
    throw UnimplementedError();
  }

  @override
  Future<void> fakeInstallFails() {
    // TODO: implement fakeInstallFails
    throw UnimplementedError();
  }

  @override
  Future<bool> fakeIsConfirmationDialogVisible() {
    // TODO: implement fakeIsConfirmationDialogVisible
    throw UnimplementedError();
  }

  @override
  Future<bool> fakeIsImmediateFlowVisible() {
    // TODO: implement fakeIsImmediateFlowVisible
    throw UnimplementedError();
  }

  @override
  Future<bool> fakeIsInstallSplashScreenVisible() {
    // TODO: implement fakeIsInstallSplashScreenVisible
    throw UnimplementedError();
  }

  @override
  Future<void> fakeSetBytesDownloaded(int bytes) {
    // TODO: implement fakeSetBytesDownloaded
    throw UnimplementedError();
  }

  @override
  Future<void> fakeSetClientVersionStalenessDays(int days) {
    // TODO: implement fakeSetClientVersionStalenessDays
    throw UnimplementedError();
  }

  @override
  Future<void> fakeSetInstallErrorCode(int errorCode) {
    // TODO: implement fakeSetInstallErrorCode
    throw UnimplementedError();
  }

  @override
  Future<void> fakeSetTotalBytesToDownload(int bytes) {
    // TODO: implement fakeSetTotalBytesToDownload
    throw UnimplementedError();
  }

  @override
  Future<void> fakeSetUpdateAvailable(int availableVersionCode, AppUpdateType appUpdateType) {
    // TODO: implement fakeSetUpdateAvailable
    throw UnimplementedError();
  }

  @override
  Future<void> fakeSetUpdateNotAvailable() {
    // TODO: implement fakeSetUpdateNotAvailable
    throw UnimplementedError();
  }

  @override
  Future<void> fakeSetUpdatePriority(int priority) {
    // TODO: implement fakeSetUpdatePriority
    throw UnimplementedError();
  }

  @override
  Future<void> fakeStartUpdateFlexible() {
    // TODO: implement fakeStartUpdateFlexible
    throw UnimplementedError();
  }

  @override
  Future<void> fakeStartUpdateImmediate() {
    // TODO: implement fakeStartUpdateImmediate
    throw UnimplementedError();
  }

  @override
  Future<int> fakeTypeForUpdateInProgress() {
    // TODO: implement fakeTypeForUpdateInProgress
    throw UnimplementedError();
  }

  @override
  Future<void> fakeUserAcceptsUpdate() {
    // TODO: implement fakeUserAcceptsUpdate
    throw UnimplementedError();
  }

  @override
  Future<void> fakeUserRejectsUpdate() {
    // TODO: implement fakeUserRejectsUpdate
    throw UnimplementedError();
  }

  @override
  Future<void> startUpdateFlexible() {
    // TODO: implement startUpdateFlexible
    throw UnimplementedError();
  }

  @override
  Future<void> startUpdateImmediate() {
    // TODO: implement startUpdateImmediate
    throw UnimplementedError();
  }

  @override
  Stream<InAppUpdateInstallState> fakeObserveInAppUpdateInstallState() {
    // TODO: implement fakeObserveInAppUpdateInstallState
    throw UnimplementedError();
  }

  @override
  Stream<InAppUpdateInstallState> observeInAppUpdateInstallState() {
    // TODO: implement observeInAppUpdateInstallState
    throw UnimplementedError();
  }
}

void main() {
  final InAppUpdaterPlatform initialPlatform = InAppUpdaterPlatform.instance;

  test('$MethodChannelInAppUpdater is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelInAppUpdater>());
  });

  test('getPlatformVersion', () async {
    InAppUpdater inAppUpdaterPlugin = InAppUpdater();
    MockInAppUpdaterPlatform fakePlatform = MockInAppUpdaterPlatform();
    InAppUpdaterPlatform.instance = fakePlatform;

    expect(await inAppUpdaterPlugin.getPlatformVersion(), '42');
  });
}
