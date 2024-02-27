
import 'in_app_updater_platform_interface.dart';

class InAppUpdater {
  Future<String?> getPlatformVersion() {
    return InAppUpdaterPlatform.instance.getPlatformVersion();
  }
}
