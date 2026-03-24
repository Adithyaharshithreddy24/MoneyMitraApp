package com.example.moneymitra.repository

import com.example.moneymitra.data.api.RetrofitClient
import com.example.moneymitra.data.model.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ScanRepository {

    suspend fun scanReceipt(file: File): Response {

        val requestFile =
            file.asRequestBody("image/*".toMediaTypeOrNull())

        val body =
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestFile
            )

        return RetrofitClient.apiService.scanReceipt(body)
    }
}