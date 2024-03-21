
enum UpdateAvailability {
  unknown(0),
  updateNotAvailable(1),
  updateAvailable(2),
  developerTriggeredUpdateInProgress(3);

  const UpdateAvailability(this.value);
  final int value;
}
