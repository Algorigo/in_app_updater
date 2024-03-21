import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:in_app_updater/data/in_app_update_install_state.dart';
import 'package:in_app_updater/extensions/in_app_update_extensions.dart';

import 'data/app_update_type.dart';
import 'data/in_app_update_info.dart';
import 'in_app_updater_platform_interface.dart';

/// An implementation of [InAppUpdaterPlatform] that uses method channels.
class MethodChannelInAppUpdater extends InAppUpdaterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('in_app_updater');

  @visibleForTesting
  final eventChannel = const EventChannel('in_app_updater_event');

  @visibleForTesting
  final fakeEventChannel = const EventChannel('in_app_updater_fake_event');

  @override
  Future<InAppUpdateInfo> checkForUpdate() async {
    final map = Map<String, dynamic>.from(await methodChannel.invokeMethod('checkForUpdate') as Map);
    final updateInfo = InAppUpdateInfo.fromJson(map);
    return updateInfo;
  }

  @override
  Future<bool> checkUpdateAvailable() async {
    final updateAvailable = await methodChannel.invokeMethod<bool>('checkUpdateAvailable');
    return updateAvailable ?? false;
  }

  @override
  Future<void> startUpdateImmediate() async {
    return await methodChannel.invokeMethod('startUpdateImmediate');
  }

  @override
  Future<void> startUpdateFlexible() async {
    return await methodChannel.invokeMethod('startUpdateFlexible');
  }

  @override
  Future<void> completeFlexibleUpdate() async {
    return await methodChannel.invokeMethod('completeFlexibleUpdate');
  }

  @override
  Stream<InAppUpdateInstallState> observeInAppUpdateInstallState() {
    return eventChannel
        .receiveBroadcastStream()
        .map((event) => jsonDecode(event))
        .map((map) => InAppUpdateInstallState.fromJson(map));
  }

  @override
  Future<InAppUpdateInfo> fakeCheckForUpdate() async {
    final map = Map<String, dynamic>.from(await methodChannel.invokeMethod('fakeCheckForUpdate') as Map);
    final updateInfo = InAppUpdateInfo.fromJson(map);
    return updateInfo;
  }

  @override
  Future<bool> fakeCheckUpdateAvailable() async {
    final updateAvailable = await methodChannel.invokeMethod<bool>('fakeCheckUpdateAvailable');
    return updateAvailable ?? false;
  }

  @override
  Future<void> fakeStartUpdateImmediate() async {
    return await methodChannel.invokeMethod('fakeStartUpdateImmediate');
  }

  @override
  Future<void> fakeStartUpdateFlexible() async {
    return await methodChannel.invokeMethod('fakeStartUpdateFlexible');
  }

  @override
  Future<void> fakeCompleteFlexibleUpdate() async {
    return await methodChannel.invokeMethod('fakeCompleteFlexibleUpdate');
  }

  @override
  Future<void> fakeSetUpdateAvailable(int availableVersionCode, AppUpdateType appUpdateType) async {
    final map = {
      'availableVersionCode': availableVersionCode,
      'appUpdateType': appUpdateType.index,
    };
    return await methodChannel.invokeMethod('fakeSetUpdateAvailable', map);
  }

  @override
  Future<void> fakeSetUpdateNotAvailable() async {
    return await methodChannel.invokeMethod('fakeSetUpdateNotAvailable');
  }

  @override
  Future<void> fakeUserAcceptsUpdate() async {
    return await methodChannel.invokeMethod('fakeUserAcceptsUpdate');
  }

  @override
  Future<void> fakeUserRejectsUpdate() async {
    return await methodChannel.invokeMethod('fakeUserRejectsUpdate');
  }

  @override
  Future<void> fakeSetUpdatePriority(int priority) async {
    return await methodChannel.invokeMethod('fakeSetUpdatePriority', priority);
  }

  @override
  Future<void> fakeSetClientVersionStalenessDays(int days) async {
    return await methodChannel.invokeMethod('fakeSetClientVersionStalenessDays', days);
  }

  @override
  Future<int> fakeGetTypeForUpdate() async {
    return await methodChannel.invokeMethod('fakeGetTypeForUpdate');
  }

  @override
  Future<void> fakeSetTotalBytesToDownload(int bytes) async {
    return await methodChannel.invokeMethod('fakeSetTotalBytesToDownload', bytes);
  }

  @override
  Future<void> fakeSetBytesDownloaded(int bytes) async {
    return await methodChannel.invokeMethod('fakeSetBytesDownloaded', bytes);
  }

  @override
  Future<void> fakeDownloadStarts() async {
    return await methodChannel.invokeMethod('fakeDownloadStarts');
  }

  @override
  Future<void> fakeUserCancelsDownload() async {
    return await methodChannel.invokeMethod('fakeUserCancelsDownload');
  }

  @override
  Future<void> fakeDownloadCompletes() async {
    return await methodChannel.invokeMethod('fakeDownloadCompletes');
  }

  @override
  Future<void> fakeDownloadFails() async {
    return await methodChannel.invokeMethod('fakeDownloadFails');
  }

  @override
  Future<void> fakeInstallCompletes() async {
    return await methodChannel.invokeMethod('fakeInstallCompletes');
  }

  @override
  Future<void> fakeInstallFails() async {
    return await methodChannel.invokeMethod('fakeInstallFails');
  }

  @override
  Future<void> fakeSetInstallErrorCode(int errorCode) async {
    return await methodChannel.invokeMethod('fakeSetInstallErrorCode', errorCode);
  }

  @override
  Future<bool> fakeIsConfirmationDialogVisible() async {
    return await methodChannel.invokeMethod('fakeIsConfirmationDialogVisible');
  }

  @override
  Future<bool> fakeIsImmediateFlowVisible() async {
    return await methodChannel.invokeMethod('fakeIsImmediateFlowVisible');
  }

  @override
  Future<bool> fakeIsInstallSplashScreenVisible() async {
    return await methodChannel.invokeMethod('fakeIsInstallSplashScreenVisible');
  }

  @override
  Future<int> fakeTypeForUpdateInProgress() async {
    return await methodChannel.invokeMethod('fakeTypeForUpdateInProgress');
  }

  @override
  Stream<InAppUpdateInstallState> fakeObserveInAppUpdateInstallState() {
    return fakeEventChannel
        .receiveBroadcastStream()
        .map((map) => InAppUpdateInstallState.fromJson(Map<String, dynamic>.from(map)));
  }
}
