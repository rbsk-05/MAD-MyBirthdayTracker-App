package com.example.mybirthdaytracker.network

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface TwilioApi {
    @FormUrlEncoded
    @POST("Accounts/{accountSid}/Messages.json")
    suspend fun sendMessage(
        @Path("accountSid") accountSid: String,
        @Field("To") to: String,
        @Field("From") from: String,
        @Field("Body") body: String
    ): Response<TwilioResponse>

    @FormUrlEncoded
    @POST("Accounts/{accountSid}/Calls.json")
    suspend fun makeCall(
        @Path("accountSid") accountSid: String,
        @Field("To") to: String,
        @Field("From") from: String,
        @Field("Twiml") twiml: String
    ): Response<TwilioResponse>
}

data class TwilioResponse(
    val sid: String,
    val status: String
)
