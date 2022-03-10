package com.example.recursivecontainermanager.exceptions

import okhttp3.HttpUrl
import java.lang.Exception

enum class ServerError {
    NO_SESSION_COOKIE,
    IMPOSSIBLE_STATUS_RESPONSE,
    MALFORMED_RESPONSE
}

class ServerErrorException(url: HttpUrl, error: ServerError, message: String?) :
    Exception("$url: $error"+(if (!message.isNullOrBlank()) ": $message" else ""))