package com.example.recursivecontainermanager.exceptions

import java.io.IOException

enum class ServerError {
    NO_SESSION_COOKIE,
    IMPOSSIBLE_STATUS_RESPONSE,
    MALFORMED_RESPONSE
}

class ServerErrorException(error: ServerError, message: String?) :
    IOException("$error"+(if (!message.isNullOrBlank()) ": $message" else ""))