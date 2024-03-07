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

  Future<Map<String, dynamic>?> checkForUpdate() {
    throw UnimplementedError('checkForUpdate() has not been implemented.');
  }

  Future<bool?> checkUpdateAvailable() {
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
}
