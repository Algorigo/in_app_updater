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
}
