package com.example.recursivecontainermanager.client

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class SessionInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val sessionRequest = request.newBuilder()
        //  .header("Cookie", viewModel.getSessionCookie())
            .build()

        val response = chain.proceed(sessionRequest)

        if (response.header("Cookie",null) == null) {
            throw Exception()
        }

        return response
    }
}