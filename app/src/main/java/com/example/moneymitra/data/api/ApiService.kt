package com.example.moneymitra.data.api

import com.example.moneymitra.data.model.GoldLoanResponse
import com.example.moneymitra.data.model.LoanRequest
import com.example.moneymitra.data.model.LoanResponse
import com.example.moneymitra.data.model.LoansResponse
import com.example.moneymitra.data.model.Response
import com.example.moneymitra.data.model.VehicleLoanResponse
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

    @POST("personalloanprediction")
    suspend fun predictPersonalLoan(
        @Body request: LoanRequest
    ): LoanResponse

    @FormUrlEncoded
    @POST("calculatehomeloan")
    suspend fun calculateHomeLoan(
        @Field("propertyvalue") propertyValue: Double,
        @Field("monthlyincome") monthlyIncome: Double,
        @Field("cibil") cibil: Int
    ): LoansResponse

    @FormUrlEncoded
    @POST("calculategoldloan")
    suspend fun calculateGoldLoan(
        @Field("weight") weight: Double
    ): GoldLoanResponse

    @FormUrlEncoded
    @POST("calculatevehicleloan")
    suspend fun calculateVehicleLoan(
        @Field("price") price: Double,
        @Field("income") income: Double,
        @Field("cibil") cibil: Int,
        @Field("vehicle_type") type: String
    ): VehicleLoanResponse
}