package com.example.moneymitra.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun UploadReceiptScreen(
    navController: NavController
) {

    val context = LocalContext.current
    var loading by remember { mutableStateOf(false) }

    val picker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            uri ?: return@rememberLauncherForActivityResult

            loading = true

            val file = uriToFile(uri, context)

            uploadReceipt(
                file,
                navController,
                context
            ) {
                loading = false
            }
        }

    LaunchedEffect(Unit) {
        picker.launch("image/*")
    }

    if (loading) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                CircularProgressIndicator()

                Spacer(modifier = Modifier.height(12.dp))

                Text("Uploading receipt...")

            }
        }
    }
}

fun uriToFile(uri: Uri, context: Context): File {

    val inputStream = context.contentResolver.openInputStream(uri)

    val file = File(context.cacheDir, "upload_receipt.jpg")

    val outputStream = FileOutputStream(file)

    inputStream?.copyTo(outputStream)

    inputStream?.close()
    outputStream.close()

    return file
}