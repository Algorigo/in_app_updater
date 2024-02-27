import 'package:flutter_test/flutter_test.dart';
import 'package:in_app_updater/in_app_updater.dart';
import 'package:in_app_updater/in_app_updater_platform_interface.dart';
import 'package:in_app_updater/in_app_updater_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockInAppUpdaterPlatform
    with MockPlatformInterfaceMixin
    implements InAppUpdaterPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final InAppUpdaterPlatform initialPlatform = InAppUpdaterPlatform.instance;

  test('$MethodChannelInAppUpdater is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelInAppUpdater>());
  });

  test('getPlatformVersion', () async {
    InAppUpdater inAppUpdaterPlugin = InAppUpdater();
    MockInAppUpdaterPlatform fakePlatform = MockInAppUpdaterPlatform();
    InAppUpdaterPlatform.instance = fakePlatform;

    expect(await inAppUpdaterPlugin.getPlatformVersion(), '42');
  });
}
