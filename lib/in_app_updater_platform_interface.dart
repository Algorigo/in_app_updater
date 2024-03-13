import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'in_app_updater_method_channel.dart';

abstract class InAppUpdaterPlatform extends PlatformInterface {
  /// Constructs a InAppUpdaterPlatform.
  InAppUpdaterPlatform() : super(token: _token);

  static final Object _token = Object();

  static InAppUpdaterPlatform _instance = MethodChannelInAppUpdater();

  /// The default instance of [InAppUpdaterPlatform] to use.
  ///
  /// Defaults to [MethodChannelInAppUpdater].
  static InAppUpdaterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [InAppUpdaterPlatform] when
  /// they register themselves.
  static set instance(InAppUpdaterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<Map<String, dynamic>> checkForUpdate() {
    throw UnimplementedError('checkForUpdate() has not been implemented.');
  }

  Future<bool> checkUpdateAvailable() {
    throw UnimplementedError('checkUpdateAvailable() has not been implemented.');
  }

  Future<void> startUpdateImmediate() {
    throw UnimplementedError('startUpdateImmediate() has not been implemented.');
  }

  Future<void> startUpdateFlexible() {
    throw UnimplementedError('startUpdateFlexible() has not been implemented.');
  }

  Future<void> completeFlexibleUpdate() {
    throw UnimplementedError('completeFlexibleUpdate() has not been implemented.');
  }

  Stream<dynamic> observeInAppUpdateInstallState() {
    throw UnimplementedError('observeInAppUpdateInstallState() has not been implemented.');
  }

  Future<Map<String, dynamic>> fakeCheckForUpdate() {
    throw UnimplementedError('fakeCheckForUpdate() has not been implemented.');
  }

  Future<bool> fakeCheckUpdateAvailable() {
    throw UnimplementedError('fakeCheckUpdateAvailable() has not been implemented.');
  }

  Future<void> fakeStartUpdateImmediate() {
    throw UnimplementedError('fakeStartUpdateImmediate() has not been implemented.');
  }

  Future<void> fakeStartUpdateFlexible() {
    throw UnimplementedError('fakeStartUpdateFlexible() has not been implemented.');
  }

  Future<void> fakeCompleteFlexibleUpdate() {
    throw UnimplementedError('fakeCompleteFlexibleUpdate() has not been implemented.');
  }

  Future<void> fakeSetUpdateAvailable(int isAvailable) {
    throw UnimplementedError('fakeSetUpdateAvailable() has not been implemented.');
  }

  Future<void> fakeSetUpdateNotAvailable() {
    throw UnimplementedError('fakeSetUpdateNotAvailable() has not been implemented.');
  }

  Future<void> fakeUserAcceptsUpdate() {
    throw UnimplementedError('fakeUserAcceptsUpdate() has not been implemented.');
  }

  Future<void> fakeUserRejectsUpdate() {
    throw UnimplementedError('fakeUserRejectsUpdate() has not been implemented.');
  }

  Future<void> fakeSetUpdatePriority(int priority) {
    throw UnimplementedError('fakeSetUpdatePriority() has not been implemented.');
  }

  Future<void> fakeSetClientVersionStalenessDays(int days) {
    throw UnimplementedError('fakeSetClientVersionStalenessDays() has not been implemented.');
  }

  Future<int> fakeGetTypeForUpdate() {
    throw UnimplementedError('fakeGetTypeForUpdate() has not been implemented.');
  }

  Future<void> fakeSetTotalBytesToDownload(int bytes) {
    throw UnimplementedError('fakeSetTotalBytesToDownload() has not been implemented.');
  }

  Future<void> fakeSetBytesDownloaded(int bytes) {
    throw UnimplementedError('fakeSetBytesDownloaded() has not been implemented.');
  }

  Future<void> fakeDownloadStarts() {
    throw UnimplementedError('fakeDownloadStarts() has not been implemented.');
  }

  Future<void> fakeDownloadCompletes() {
    throw UnimplementedError('fakeDownloadCompletes() has not been implemented.');
  }

  Future<void> fakeDownloadFails() {
    throw UnimplementedError('fakeDownloadFails() has not been implemented.');
  }

  Future<void> fakeInstallCompletes() {
    throw UnimplementedError('fakeInstallCompletes() has not been implemented.');
  }

  Future<void> fakeInstallFails() {
    throw UnimplementedError('fakeInstallFails() has not been implemented.');
  }

  Future<void> fakeSetInstallErrorCode(int errorCode) {
    throw UnimplementedError('fakeSetInstallErrorCode() has not been implemented.');
  }

  Future<bool> fakeIsConfirmationDialogVisible() {
    throw UnimplementedError('fakeIsConfirmationDialogVisible() has not been implemented.');
  }

  Future<bool> fakeIsImmediateFlowVisible() {
    throw UnimplementedError('fakeIsImmediateFlowVisible() has not been implemented.');
  }

  Future<bool> fakeIsInstallSplashScreenVisible() {
    throw UnimplementedError('fakeIsInstallSplashScreenVisible() has not been implemented.');
  }

  Future<int> fakeTypeForUpdateInProgress() {
    throw UnimplementedError('fakeTypeForUpdateInProgress() has not been implemented.');
  }

  Stream<dynamic> fakeObserveInAppUpdateInstallState() {
    throw UnimplementedError('fakeObserveInAppUpdateInstallState() has not been implemented.');
  }
}
