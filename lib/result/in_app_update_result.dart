sealed class InAppUpdateResult implements Exception {
  InAppUpdateResult({
    required this.code,
    this.message,
    this.details,
  });

  final String code;
  final String? message;
  final dynamic details;

  @override
  String toString() => 'PlatformException($code, $message, $details)';

  static InAppUpdateResult? of(String code, message, details) {
    switch (code) {
      case immediateUpdateCanceledExceptionCode:
        return ImmediateUpdateCanceledException(message: message, details: details);
      case immediateUpdateFailedExceptionCode:
        return ImmediateUpdateFailedException(message: message, details: details);
      case flexibleUpdateCanceledExceptionCode:
        return FlexibleUpdateCanceledException(message: message, details: details);
      case flexibleUpdateFailedExceptionCode:
        return FlexibleUpdateFailedException(message: message, details: details);
      default:
        return null;
    }
  }
}

class ImmediateUpdateCanceledException extends InAppUpdateResult {
  ImmediateUpdateCanceledException({
    String? message,
    dynamic details,
  }) : super(
          code: immediateUpdateCanceledExceptionCode,
          message: message,
          details: details,
        );
}

class ImmediateUpdateFailedException extends InAppUpdateResult {
  ImmediateUpdateFailedException({
    String? message,
    dynamic details,
  }) : super(
          code: immediateUpdateFailedExceptionCode,
          message: message,
          details: details,
        );
}

class FlexibleUpdateCanceledException extends InAppUpdateResult {
  FlexibleUpdateCanceledException({
    String? message,
    dynamic details,
  }) : super(
          code: flexibleUpdateCanceledExceptionCode,
          message: message,
          details: details,
        );
}

class FlexibleUpdateFailedException extends InAppUpdateResult {
  FlexibleUpdateFailedException({
    String? message,
    dynamic details,
  }) : super(
          code: flexibleUpdateFailedExceptionCode,
          message: message,
          details: details,
        );
}


const String immediateUpdateCanceledExceptionCode = "5901";
const String immediateUpdateFailedExceptionCode = "5902";
const String flexibleUpdateCanceledExceptionCode = "5903";
const String flexibleUpdateFailedExceptionCode = "5904";
