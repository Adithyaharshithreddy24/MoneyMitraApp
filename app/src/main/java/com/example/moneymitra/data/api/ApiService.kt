package com.example.moneymitra.data.api

import com.example.moneymitra.data.model.Response
import okhttp3.MultipartBody
import retrofit2.Response as RetrofitResponse
import retrofit2.http.*

data class NotificationRequest(
    val text: String
)

data class MemberRequest(
    val id: String,
    val name: String,
    val email: String,
    val amount: Int,
    val managerName: String,
    val managerUpi: String
)

data class ReminderRequest(
    val members: List<MemberRequest>
)

interface ApiService {

    @Multipart
    @POST("scan-receipt")
    suspend fun scanReceipt(
        @Part file: MultipartBody.Part
    ): Response

    @POST("analyze-notification")
    suspend fun analyzeNotification(
        @Body request: NotificationRequest
    ): Response

    @POST("send-reminder")
    suspend fun sendReminder(
        @Body request: ReminderRequest
    ): RetrofitResponse<Unit>
}