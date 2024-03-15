
import 'package:in_app_updater/data/in_app_update_info.dart';
import 'package:in_app_updater/data/in_app_update_install_state.dart';

import 'in_app_updater_platform_interface.dart';

class InAppUpdater {
  Future<String?> getPlatformVersion() {
    return InAppUpdaterPlatform.instance.getPlatformVersion();
  }

  Future<InAppUpdateInfo> checkForUpdate() {
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

  Stream<InAppUpdateInstallState> observeInAppUpdateInstallState() {
    return InAppUpdaterPlatform.instance.observeInAppUpdateInstallState();
  }
}
