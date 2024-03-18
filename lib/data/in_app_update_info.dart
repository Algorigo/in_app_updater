import 'package:in_app_updater/data/update_availability.dart';
import 'package:in_app_updater/extensions/in_app_update_extensions.dart';

import 'install_status.dart';

class InAppUpdateInfo {
  final UpdateAvailability? updateAvailability;
  final int? availableVersionCode;
  final int? updatePriority;
  final String? packageName;
  final int? clientVersionStalenessDays;
  final InstallStatus? installStatus;
  final bool? isFlexibleUpdateAllowed;
  final List<int>? flexibleUpdateFailedPreconditions;
  final bool? isImmediateUpdateAllowed;
  final List<int>? immediateUpdateFailedPreconditions;
  final int? bytesDownloaded;
  final int? totalBytesToDownload;

  InAppUpdateInfo({
    required this.updateAvailability,
    required this.availableVersionCode,
    required this.updatePriority,
    required this.packageName,
    required this.clientVersionStalenessDays,
    required this.installStatus,
    required this.isFlexibleUpdateAllowed,
    required this.flexibleUpdateFailedPreconditions,
    required this.isImmediateUpdateAllowed,
    required this.immediateUpdateFailedPreconditions,
    required this.bytesDownloaded,
    required this.totalBytesToDownload,
  });

  factory InAppUpdateInfo.fromJson(Map<String, dynamic> json) {
    return InAppUpdateInfo(
      updateAvailability: (json['updateAvailability'] as int?)?.toUpdateAvailability(),
      availableVersionCode: json['availableVersionCode'],
      updatePriority: json['updatePriority'],
      packageName: json['packageName'],
      clientVersionStalenessDays: json['clientVersionStalenessDays'],
      installStatus: (json['installStatus'] as int?)?.toInstallStatus(),
      isFlexibleUpdateAllowed: json['isFlexibleUpdateAllowed'],
      flexibleUpdateFailedPreconditions: List<int>.from(json['isFlexibleUpdateFailedPreconditions']),
      isImmediateUpdateAllowed: json['isImmediateUpdateAllowed'],
      immediateUpdateFailedPreconditions: List<int>.from(json['isImmediateUpdateFailedPreconditions']),
      bytesDownloaded: json['bytesDownloaded'],
      totalBytesToDownload: json['totalBytesToDownload'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'updateAvailability': updateAvailability,
      'availableVersionCode': availableVersionCode,
      'updatePriority': updatePriority,
      'packageName': packageName,
      'clientVersionStalenessDays': clientVersionStalenessDays,
      'installStatus': installStatus,
      'isFlexibleUpdateAllowed': isFlexibleUpdateAllowed,
      'flexibleUpdateFailedPreconditions': flexibleUpdateFailedPreconditions,
      'isImmediateUpdateAllowed': isImmediateUpdateAllowed,
      'immediateUpdateFailedPreconditions': immediateUpdateFailedPreconditions,
      'bytesDownloaded': bytesDownloaded,
    };
  }
}
