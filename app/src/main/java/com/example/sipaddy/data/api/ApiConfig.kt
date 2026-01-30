package com.example.sipaddy.data.api

import android.content.Context
import com.example.sipaddy.BuildConfig
import com.example.sipaddy.data.local.TokenPreferences
import com.example.sipaddy.data.local.dataStore
import com.example.sipaddy.utils.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    fun getApiService(context: Context): ApiService {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val authInterceptor = Interceptor { chain ->
            val tokenPreferences = TokenPreferences.getInstance(context.dataStore)
            val token = runBlocking {
                tokenPreferences.getAccessToken().first()
            }

            val request = chain.request()
            val requestBuilder = request.newBuilder()

            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }


        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(Constants.TIMEOUT_DURATION, TimeUnit.SECONDS)
            .readTimeout(Constants.TIMEOUT_DURATION, TimeUnit.SECONDS)
            .writeTimeout(Constants.TIMEOUT_DURATION, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}