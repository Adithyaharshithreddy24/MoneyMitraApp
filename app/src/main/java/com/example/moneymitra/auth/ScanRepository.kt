package com.example.moneymitra.auth

import com.example.moneymitra.data.api.RetrofitClient
import com.example.moneymitra.data.model.ReceiptResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ScanRepository {

    suspend fun scanReceipt(file: File): ReceiptResponse {

        val requestFile =
            file.asRequestBody("image/*".toMediaTypeOrNull())

        val body =
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestFile
            )

        return RetrofitClient.api.scanReceipt(body)
    }
}