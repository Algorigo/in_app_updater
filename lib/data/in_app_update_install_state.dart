import 'package:in_app_updater/data/install_status.dart';
import 'package:in_app_updater/extensions/in_app_update_extensions.dart';

class InAppUpdateInstallState {
  final String? packageName;
  final InstallStatus? installStatus;
  final bool? isDownloaded;
  final int? bytesDownloaded;
  final int? totalBytesToDownload;
  final bool? hasTerminalStatus;
  final int? installErrorCode;

  InAppUpdateInstallState({
    required this.packageName,
    required this.installStatus,
    required this.isDownloaded,
    required this.bytesDownloaded,
    required this.totalBytesToDownload,
    required this.hasTerminalStatus,
    required this.installErrorCode,
  });

  factory InAppUpdateInstallState.fromJson(Map<String, dynamic> json) {
    return InAppUpdateInstallState(
      packageName: json['packageName'],
      installStatus: (json['installStatus'] as int?)?.toInstallStatus(),
      isDownloaded: json['isDownloaded'],
      bytesDownloaded: json['bytesDownloaded'],
      totalBytesToDownload: json['totalBytesToDownload'],
      hasTerminalStatus: json['hasTerminalStatus'],
      installErrorCode: json['installErrorCode'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'packageName': packageName,
      'installStatus': installStatus,
      'isDownloaded': isDownloaded,
      'bytesDownloaded': bytesDownloaded,
      'totalBytesToDownload': totalBytesToDownload,
      'hasTerminalStatus': hasTerminalStatus,
      'installErrorCode': installErrorCode,
    };
  }
}
