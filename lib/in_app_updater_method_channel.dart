import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'in_app_updater_platform_interface.dart';

/// An implementation of [InAppUpdaterPlatform] that uses method channels.
class MethodChannelInAppUpdater extends InAppUpdaterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('in_app_updater');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<Map<String, dynamic>?> checkForUpdate() async {
    final updateInfo = Map<String, dynamic>.from(await methodChannel.invokeMethod('checkForUpdate') as Map);
    return updateInfo;
  }

  @override
  Future<bool?> checkUpdateAvailable() async {
    final updateAvailable = await methodChannel.invokeMethod<bool>('checkUpdateAvailable');
    return updateAvailable;
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
}
