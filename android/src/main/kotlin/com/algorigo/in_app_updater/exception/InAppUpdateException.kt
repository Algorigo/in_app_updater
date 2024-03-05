package com.algorigo.in_app_updater.exception

sealed class InAppUpdateException(
  override val message: String? = null,
) : Exception(message) {

  data class UnExpectedException(override val message: String?) : InAppUpdateException(message)

  data class FetchAppUpdateInfoFailedException(override val message: String?) : InAppUpdateException(message)



  override fun toString(): String {
    return "${this.javaClass.simpleName} message=$message"
  }
}
