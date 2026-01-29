package com.example.sipaddy.data.api

import com.example.sipaddy.data.local.TokenPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenPreferences: TokenPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val url = originalRequest.url.toString()
        if (url.contains("/login") || url.contains("/register")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            tokenPreferences.getAccessToken().first()
        }


        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)

    }

}