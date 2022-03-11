package com.example.recursivecontainermanager.client

import com.example.recursivecontainermanager.exceptions.InvalidCredentialsException
import com.example.recursivecontainermanager.exceptions.ServerError
import com.example.recursivecontainermanager.exceptions.ServerErrorException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class SessionInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        /** Alter the request here */

        val sessionRequest = request.newBuilder()
            .url(MainApi.getServerAddress()+request.url.toString().substringAfter(BASE_URL))
            .header("Cookie", "session=${MainApi.getSessionCookie()}")
            .build()

        /** Send request */
        val response = chain.proceed(sessionRequest)
        /** Analyse response here */

        if (response.code == 403)
            throw InvalidCredentialsException()
        if (response.isRedirect && response.headers["Location"] == null)
            throw ServerErrorException(response.request.url.toString(), ServerError.MALFORMED_RESPONSE, response.headers.toString())

        /** Return response if no exception is to be thrown */
        return response
    }
}