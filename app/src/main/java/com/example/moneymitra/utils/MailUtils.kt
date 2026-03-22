package com.example.moneymitra.utils

import android.util.Log
import com.example.moneymitra.data.model.Member
import com.example.moneymitra.data.api.RetrofitClient
import com.example.moneymitra.data.api.MemberRequest
import com.example.moneymitra.data.api.ReminderRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.*

object MailUtils {

    fun sendReminder(members: List<Member>) {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val db = FirebaseFirestore.getInstance()

                val requestList = members.mapNotNull { member ->

                    val doc = db.collection("users")
                        .document(member.managerId)
                        .get()
                        .await()

                    // 🔥 SAFE NULL HANDLING
                    val firstName = doc.getString("firstName") ?: ""
                    val lastName = doc.getString("lastName") ?: ""
                    val managerName = "$firstName $lastName".trim()

                    val managerUpi = doc.getString("upiid") ?: ""

                    // ❗ SKIP if invalid
                    if (managerName.isEmpty() || managerUpi.isEmpty()) {
                        Log.e("MAIL", "Invalid manager data for ${member.id}")
                        return@mapNotNull null
                    }

                    MemberRequest(
                        id = member.id,
                        name = member.name,
                        email = member.email,
                        amount = member.due,
                        managerName = managerName,
                        managerUpi = managerUpi
                    )
                }

                val request = ReminderRequest(requestList)

                Log.d("MAIL_DEBUG", request.toString()) // 🔥 DEBUG

                val response = RetrofitClient.apiService.sendReminder(request)

                Log.d("MAIL", "Success: ${response.isSuccessful}")

            } catch (e: Exception) {
                Log.e("MAIL", "Error", e)
            }
        }
    }
}