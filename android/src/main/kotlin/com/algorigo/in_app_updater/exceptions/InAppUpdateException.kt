package com.algorigo.in_app_updater.exceptions

sealed class InAppUpdateException(
  open val code: Int? = null,
  override val message: String? = null,
) : Exception(message) {

  data class UnExpectedException(override val code: Int = UNEXPECTED_EXCEPTION_CODE, override val message: String?) : InAppUpdateException(code, message)

  data class UpdateNotAvailableException(override val code: Int = UPDATE_NOT_AVAILABLE_EXCEPTION_CODE, override val message: String?) : InAppUpdateException(code, message)

  data class CheckForUpdateFailedException(override val code: Int = CHECK_FOR_UPDATE_FAILED_EXCEPTION_CODE, override val message: String?) : InAppUpdateException(code, message)

  data class ImmediateUpdateNotAllowedException(override val code: Int = IMMEDIATE_UPDATE_NOT_ALLOWED_EXCEPTION_CODE, override val message: String?) : InAppUpdateException(code, message)

  data class FlexibleUpdateNotAllowedException(override val code: Int = FLEXIBLE_UPDATE_NOT_ALLOWED_EXCEPTION_CODE, override val message: String?) : InAppUpdateException(code, message)

  data class CompleteFlexibleUpdateException(override val code: Int = COMPLETE_FLEXIBLE_UPDATE_EXCEPTION_CODE, override val message: String?) : InAppUpdateException(code, message)

  override fun toString(): String {
    return "${this.javaClass.simpleName} message=$message"
  }

  companion object {
    const val UNEXPECTED_EXCEPTION_CODE = 5701
    const val UPDATE_NOT_AVAILABLE_EXCEPTION_CODE = 5702
    const val CHECK_FOR_UPDATE_FAILED_EXCEPTION_CODE = 5703
    const val IMMEDIATE_UPDATE_NOT_ALLOWED_EXCEPTION_CODE = 5704
    const val FLEXIBLE_UPDATE_NOT_ALLOWED_EXCEPTION_CODE = 5705
    const val COMPLETE_FLEXIBLE_UPDATE_EXCEPTION_CODE = 5706
  }
}
