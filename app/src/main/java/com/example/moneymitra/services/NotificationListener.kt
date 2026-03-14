package com.example.moneymitra.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.moneymitra.data.api.NotificationRequest
import com.example.moneymitra.data.api.RetrofitClient
import com.example.moneymitra.utils.SettingsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.UUID

class NotificationListener : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        try {

            if (!SettingsManager.isAutoReadEnabled(applicationContext)) return

            val pkg = sbn.packageName

            if (!isPaymentApp(pkg) && !isSmsApp(pkg)) return

            val extras = sbn.notification.extras

            val title = extras.getString("android.title") ?: ""
            val text = extras.getCharSequence("android.text")?.toString() ?: ""

            val message = "$title $text"

            if (!isTransactionMessage(message)) return

            processTransaction(message)

        } catch (e: Exception) {
            Log.e("MoneyMitraListener", "Error: ${e.message}")
        }
    }

    private fun isSmsApp(pkg: String): Boolean {

        val smsApps = setOf(
            "com.google.android.apps.messaging",   // Google Messages
            "com.android.mms",                     // Default Android SMS
            "com.samsung.android.messaging",       // Samsung
            "com.truecaller",                      // Truecaller SMS
            "com.miui.sms",                        // Xiaomi
            "com.microsoft.android.smsorganizer"   // Microsoft SMS Organizer
        )

        return pkg in smsApps
    }

    private fun isPaymentApp(pkg: String): Boolean {

        val apps = setOf(

            // UPI Apps
            "com.phonepe.app",
            "com.google.android.apps.nbu.paisa.user",
            "net.one97.paytm",
            "in.amazon.mShop.android.shopping",
            "com.whatsapp",
            "com.bhim.upi",

            // Banks
            "com.sbi.lotusintouch",
            "com.csam.icici.bank.imobile",
            "com.axis.mobile",
            "com.snapwork.hdfc",
            "com.msf.kbank.mobile",
            "com.indusind.indie",
            "com.federalbank.FedMobile"
        )

        return pkg in apps
    }

    private fun isTransactionMessage(message: String): Boolean {

        val keywords = listOf(
            "debited",
            "credited",
            "paid",
            "received",
            "spent",
            "upi",
            "withdrawn",
            "deposit"
        )

        val lower = message.lowercase()

        return keywords.any { lower.contains(it) }
    }

    private fun processTransaction(message: String) {

        serviceScope.launch {

            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Log.d("MoneyMitraListener", "User not logged in")
                return@launch
            }
            try {

                val response =
                    RetrofitClient.apiService.analyzeNotification(
                        NotificationRequest(message)
                    )

                Log.d("MoneyMitraGemini", response.toString())

                if (response.type == "NONE") return@launch

                val userId =
                    FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                val db = FirebaseFirestore.getInstance()

                val notification = hashMapOf(
                    "name" to response.name,
                    "amount" to response.amount,
                    "type" to response.type,
                    "category" to response.category,
                    "note" to response.note,
                    "createdAt" to response.createdAt
                )

                db.collection("users")
                    .document(userId)
                    .collection("notifications")
                    .add(notification)

            } catch (e: Exception) {

                Log.e("MoneyMitraError", e.message ?: "")
            }
        }
    }
}