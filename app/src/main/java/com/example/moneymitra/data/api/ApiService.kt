package com.example.moneymitra.data.api

import com.example.moneymitra.data.model.ReceiptResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("scan-receipt")
    suspend fun scanReceipt(
        @Part file: MultipartBody.Part
    ): ReceiptResponse
}