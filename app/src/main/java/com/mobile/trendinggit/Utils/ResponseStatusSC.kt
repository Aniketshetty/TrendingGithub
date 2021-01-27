package com.mobile.trendinggit.Utils

sealed class ResponseStatusSC {
    data class Success(val data: String) : ResponseStatusSC()
    data class Error(val error: Throwable, val data: String) : ResponseStatusSC()
    object Start : ResponseStatusSC()
}