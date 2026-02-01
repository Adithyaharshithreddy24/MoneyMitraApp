package com.example.moneymitra.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun sendMail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("manideepgangaraju369@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Test Mail")
        putExtra(Intent.EXTRA_TEXT, "hi")
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
    }
}
