import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:in_app_updater/data/app_update_type.dart';
import 'package:in_app_updater/data/in_app_update_info.dart';
import 'package:in_app_updater/data/in_app_update_install_state.dart';
import 'package:in_app_updater/data/install_status.dart';
import 'package:in_app_updater/extensions/in_app_update_extensions.dart';
import 'package:in_app_updater/fake_in_app_updater.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _fakeInAppUpdater = FakeInAppUpdater();

  StreamSubscription<InAppUpdateInstallState>? installStateStreamSubscription;

  InAppUpdateInfo? _appUpdateInfo;
  int _availableVersionCode = 1;
  bool? _updateAvailable;
  InAppUpdateInstallState? _installState;

  @override
  void initState() {
    super.initState();
    checkAppUpdateInfo();
    observeInAppUpdateInstallState();
  }

  @override
  void dispose() {
    installStateStreamSubscription?.cancel();
    super.dispose();
  }

  String installStatusToString(InAppUpdateInstallState? inAppUpdateInstallState) {
    switch (inAppUpdateInstallState?.installStatus) {
      case null:
        return 'Install not started';
      case InstallStatus.unknown:
        return 'Install not started';
      case InstallStatus.pending:
        return 'Install pending';
      case InstallStatus.installing:
        return 'Installing';
      case InstallStatus.installed:
        return 'Installed';
      case InstallStatus.failed:
        return 'Install failed';
      case InstallStatus.downloading:
        return 'Downloading';
      case InstallStatus.downloaded:
        return 'Downloaded';
      case InstallStatus.canceled:
        return 'Install canceled';
    }
  }

  void observeInAppUpdateInstallState() {
    installStateStreamSubscription ??= _fakeInAppUpdater.observeInAppUpdateInstallState().listen((event) {
      setState(() {
        _installState = event;
      });
    });
  }

  Future<void> checkAppUpdateInfo() async {
    InAppUpdateInfo? appUpdateInfo;

    try {
      appUpdateInfo = await _fakeInAppUpdater.checkForUpdate();
      if (kDebugMode) {
        print('update info: ${appUpdateInfo.toJson()}');
      }
    } catch (e) {
      if (kDebugMode) {
        print("Failed to get update info: $e");
      }
      appUpdateInfo = null;
    }

    if (!mounted) return;

    setState(() {
      _appUpdateInfo = appUpdateInfo;
      _updateAvailable = appUpdateInfo?.updateAvailability?.checkUpdateAvailable() ?? false;
    });
  }

  Future<void> checkUpdateAvailable() async {
    bool updateAvailable;
    try {
      updateAvailable = await _fakeInAppUpdater.checkUpdateAvailable();
      if (kDebugMode) {
        print('update available: $updateAvailable');
      }
    } catch (e) {
      if (kDebugMode) {
        print("Failed to check update available: $e");
      }
      updateAvailable = false;
    }

    if (!mounted) return;

    setState(() {
      _updateAvailable = updateAvailable;
    });
  }

  Future<void> setFlexibleUpdateAvailable() async {
    try {
      await _fakeInAppUpdater.setUpdateAvailable(_availableVersionCode++, AppUpdateType.flexible);
      await checkAppUpdateInfo();
    } catch (e) {
      if (kDebugMode) {
        print("Failed to set update available: $e");
      }
    }
  }

  Future<void> startFakeFlexibleUpdateFlow() async {
    try {
      await _fakeInAppUpdater.startUpdateFlexible();
      await _fakeInAppUpdater.setTotalBytesToDownload(100);
      await _fakeInAppUpdater.userAcceptsUpdate();
      await Future.delayed(const Duration(seconds: 1)); // 1 second delay
      await _fakeInAppUpdater.setDownloadStarts();
      await Future.delayed(const Duration(seconds: 1)); // 1 second delay
      await _fakeInAppUpdater.setBytesDownloaded(50);
      await Future.delayed(const Duration(seconds: 1)); // 1 second delay
      await _fakeInAppUpdater.setBytesDownloaded(100);
      await Future.delayed(const Duration(seconds: 1)); // 1 second delay
      await _fakeInAppUpdater.setDownloadCompletes();
      await Future.delayed(const Duration(seconds: 1)); // 1 second delay
      await _fakeInAppUpdater.completeFlexibleUpdate();
      await Future.delayed(const Duration(seconds: 1)); // 1 second delay
      await _fakeInAppUpdater.setInstallCompletes();
      await checkAppUpdateInfo();
    } catch (e) {
      if (kDebugMode) {
        print("Failed to set download starts: $e");
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 8.0),
          child: SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                TextButton(onPressed: checkAppUpdateInfo, child: const Text('Check for update')),
                Text('update info: ${_appUpdateInfo?.toJson()}\n', textAlign: TextAlign.center),
                TextButton(onPressed: setFlexibleUpdateAvailable, child: const Text('Set update available')),
                TextButton(onPressed: startFakeFlexibleUpdateFlow, child: const Text('Start fake flexible update flow')),
                Text('update available: $_updateAvailable\n', style: const TextStyle(color: Colors.red), textAlign: TextAlign.center),
                Text('Install state: ${installStatusToString(_installState)}', style: const TextStyle(color: Colors.red), textAlign: TextAlign.center),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
