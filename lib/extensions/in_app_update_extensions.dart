import 'package:in_app_updater/data/in_app_update_info.dart';

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
}

extension InstallStatusExtension on InstallStatus {
  int toValue() {
    switch (this) {
      case InstallStatus.unknown:
        return 0;
      case InstallStatus.pending:
        return 1;
      case InstallStatus.installing:
        return 3;
      case InstallStatus.installed:
        return 4;
      case InstallStatus.failed:
        return 5;
      case InstallStatus.downloading:
        return 6;
      case InstallStatus.downloaded:
        return 7;
      case InstallStatus.canceled:
        return 8;
      default:
        throw Exception('Unknown InstallStatus value');
    }
  }
}
