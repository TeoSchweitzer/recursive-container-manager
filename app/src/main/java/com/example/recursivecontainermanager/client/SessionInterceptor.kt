package com.example.recursivecontainermanager.client

import android.content.res.Resources
import com.example.recursivecontainermanager.exceptions.InvalidCredentialsException
import com.example.recursivecontainermanager.exceptions.ResourceNotFoundException
import com.example.recursivecontainermanager.exceptions.ServerError
import com.example.recursivecontainermanager.exceptions.ServerErrorException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.asResponseBody
import okio.Buffer
import java.io.IOException


class SessionInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        /** Alter the request here */

        val sessionRequestBuild = request.newBuilder()
        sessionRequestBuild.url(MainApi.getServerAddress()+request.url.toString().substringAfter(BASE_URL))
        if (MainApi.getSessionCookie() != "")
            sessionRequestBuild.header("Cookie", "$SESSION_COOKIE=${MainApi.getSessionCookie()}")
        val finalRequest = sessionRequestBuild.build()

        println(displayRequest(finalRequest))
        /** Send request */
        val response = chain.proceed(finalRequest)
        /** Analyse response here */
        println(displayResponse(response))

        if (response.code == 403)
            throw InvalidCredentialsException()
        if (response.isRedirect && response.headers["Location"] == null)
            throw ServerErrorException(ServerError.MALFORMED_RESPONSE, response.headers("Location").toString())
        if (response.code == 404)
            throw ResourceNotFoundException()

        /** Return response if no exception is to be thrown */
        return response
    }

    private fun displayRequest(request: Request): String {
        var result = "\nRequest Message :"
        result+="\nurl : ${request.url}"
        result+="\nmethod : ${request.method}"
        val headers = mutableListOf<String>()
        for (header in request.headers) headers.add("${header.first}: ${header.second}")
        result+="\nheaders : $headers"
        result+="\ncacheControl : ${request.cacheControl}"
        val buffer = Buffer()
        request.body?.writeTo(buffer)
        result+="\nbody : ${buffer.readUtf8()}\n"
        return result
    }

    private fun displayResponse(response: Response): String {
        var result = "\nResponse Message :"
        result+="\nprotocol : ${response.protocol}"
        result+="\ncode : ${response.code}"
        result+="\nmessage : ${response.message}"
        val headers = mutableListOf<String>()
        for (header in response.headers) headers.add("${header.first}: ${header.second}")
        result+="\nheaders : $headers"
        result+="\ncacheControl : ${response.cacheControl}"
        if (response.body != null && false) {
            result+="\nbody : ${response.body!!.string()}"
        }
        result+="\nrequest : ${displayRequest(response.request)}\n"
        return result
    }
}