import 'package:in_app_updater/data/app_update_type.dart';

import '../data/install_status.dart';
import '../data/update_availability.dart';

extension IntExtension on int {

  InstallStatus toInstallStatus() {
    switch (this) {
      case 0:
        return InstallStatus.unknown;
      case 1:
        return InstallStatus.pending;
      case 2:
        return InstallStatus.downloading;
      case 3:
        return InstallStatus.installing;
      case 4:
        return InstallStatus.installed;
      case 5:
        return InstallStatus.failed;
      case 6:
        return InstallStatus.canceled;
      case 11:
        return InstallStatus.downloaded;
      default:
        throw ArgumentError('Invalid InstallStatus value: $this');
    }
  }

  UpdateAvailability toUpdateAvailability() {
    switch (this) {
      case 0:
        return UpdateAvailability.unknown;
      case 1:
        return UpdateAvailability.updateNotAvailable;
      case 2:
        return UpdateAvailability.updateAvailable;
      case 3:
        return UpdateAvailability.developerTriggeredUpdateInProgress;
      default:
        throw ArgumentError('Invalid updateAvailability value: $this');
    }
  }

  AppUpdateType toAppUpdateType() {
    switch (this) {
      case 0:
        return AppUpdateType.flexible;
      case 1:
        return AppUpdateType.immediate;
      default:
        throw ArgumentError('Invalid AppUpdateType value: $this');
    }
  }
}

extension UpdateAvailabilityExtension on UpdateAvailability {
  int toValue() {
    switch (this) {
      case UpdateAvailability.unknown:
        return 0;
      case UpdateAvailability.updateNotAvailable:
        return 1;
      case UpdateAvailability.updateAvailable:
        return 2;
      case UpdateAvailability.developerTriggeredUpdateInProgress:
        return 3;
      default:
        throw Exception('Unknown UpdateAvailability value');
    }
  }

  bool checkUpdateAvailable() {
    return this == UpdateAvailability.updateAvailable;
  }
}

extension InstallStatusExtension on InstallStatus {
  int toValue() {
    switch (this) {
      case InstallStatus.unknown:
        return 0;
      case InstallStatus.pending:
        return 1;
      case InstallStatus.downloading:
        return 2;
      case InstallStatus.installing:
        return 3;
      case InstallStatus.installed:
        return 4;
      case InstallStatus.failed:
        return 5;
      case InstallStatus.canceled:
        return 6;
      case InstallStatus.downloaded:
        return 11;
      default:
        throw Exception('Unknown InstallStatus value');
    }
  }
}

extension AppUpdateTypeExtension on AppUpdateType {
  int toValue() {
    switch (this) {
      case AppUpdateType.flexible:
        return 0;
      case AppUpdateType.immediate:
        return 1;
      default:
        throw Exception('Unknown AppUpdateType value');
    }
  }
}