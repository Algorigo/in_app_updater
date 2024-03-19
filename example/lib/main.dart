import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:in_app_updater/data/in_app_update_info.dart';
import 'package:in_app_updater/in_app_updater.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  InAppUpdateInfo? _appUpdateInfo;
  final _inAppUpdaterPlugin = InAppUpdater();

  @override
  void initState() {
    super.initState();
    initAppUpdateInfo();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initAppUpdateInfo() async {
    InAppUpdateInfo? appUpdateInfo;

    try {
      appUpdateInfo = await _inAppUpdaterPlugin.checkForUpdate();
      if (kDebugMode) {
        print('update info: $appUpdateInfo');
      }
    } catch(e) {
      if (kDebugMode) {
        print("Failed to get update info: $e");
      }
      appUpdateInfo = null;
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _appUpdateInfo = appUpdateInfo;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('update info: ${_appUpdateInfo?.toJson()}\n'),
            ],
          ),
        ),
      ),
    );
  }
}
