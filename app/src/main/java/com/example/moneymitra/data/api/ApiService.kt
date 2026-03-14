package com.example.moneymitra.data.api

import com.example.moneymitra.data.model.Response
import okhttp3.MultipartBody
import retrofit2.http.*

data class NotificationRequest(
    val text: String
)

interface ApiService {

    // Receipt scanner
    @Multipart
    @POST("scan-receipt")
    suspend fun scanReceipt(
        @Part file: MultipartBody.Part
    ): Response


    // Notification analyzer
    @POST("analyze-notification")
    suspend fun analyzeNotification(
        @Body request: NotificationRequest
    ): Response
}