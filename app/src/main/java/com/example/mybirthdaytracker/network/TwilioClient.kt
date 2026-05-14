package com.example.mybirthdaytracker.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TwilioClient {

    private const val BASE_URL = "https://api.twilio.com/2010-04-01/"

    fun create(accountSid: String, authToken: String): TwilioApi {
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val credentials = Credentials.basic(accountSid, authToken)
            
            val request = original.newBuilder()
                .header("Authorization", credentials)
                .build()
            
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwilioApi::class.java)
    }
}
