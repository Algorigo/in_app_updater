
import 'in_app_updater_platform_interface.dart';

class InAppUpdater {
  Future<String?> getPlatformVersion() {
    return InAppUpdaterPlatform.instance.getPlatformVersion();
  }

  Future<Map<String, dynamic>> checkForUpdate() {
    return InAppUpdaterPlatform.instance.checkForUpdate();
  }

  Future<bool> checkUpdateAvailable() {
    return InAppUpdaterPlatform.instance.checkUpdateAvailable();
  }

  Future<void> startUpdateImmediate() {
    return InAppUpdaterPlatform.instance.startUpdateImmediate();
  }

  Future<void> startUpdateFlexible() {
    return InAppUpdaterPlatform.instance.startUpdateFlexible();
  }

  Future<void> completeFlexibleUpdate() {
    return InAppUpdaterPlatform.instance.completeFlexibleUpdate();
  }

  Stream<dynamic> observeInAppUpdateInstallState() {
    return InAppUpdaterPlatform.instance.observeInAppUpdateInstallState();
  }
}
