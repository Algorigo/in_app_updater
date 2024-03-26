
import 'package:flutter/services.dart';
import 'package:in_app_updater/data/in_app_update_info.dart';
import 'package:in_app_updater/data/in_app_update_install_state.dart';
import 'package:in_app_updater/result/in_app_update_result.dart';

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
    return InAppUpdaterPlatform.instance.startUpdateImmediate().onError((error, stackTrace) {
      if (error is PlatformException) {
        if (InAppUpdateResult.of(error.code, error.message, error.details) != null) {
          throw InAppUpdateResult.of(error.code, error.message, error.details)!;
        }
      }
    });
  }

  Future<void> startUpdateFlexible() {
    return InAppUpdaterPlatform.instance.startUpdateFlexible().onError((error, stackTrace) {
      if (error is PlatformException) {
        if (InAppUpdateResult.of(error.code, error.message, error.details) != null) {
          throw InAppUpdateResult.of(error.code, error.message, error.details)!;
        }
      }
    });
  }

  Future<void> completeFlexibleUpdate() {
    return InAppUpdaterPlatform.instance.completeFlexibleUpdate();
  }

  Stream<InAppUpdateInstallState> observeInAppUpdateInstallState() {
    return InAppUpdaterPlatform.instance.observeInAppUpdateInstallState();
  }
}
