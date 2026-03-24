package com.example.moneymitra.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.moneymitra.repository.ScanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ScanReceiptScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val lifecycleOwner = context as ComponentActivity

    var hasCameraPermission by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.CAMERA)
    }

    if (!hasCameraPermission) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Waiting for camera permission...")
        }
        return
    }

    val previewView = remember { PreviewView(context) }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    LaunchedEffect(previewView) {

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {

            Button(
                enabled = !isLoading,
                onClick = {

                    isLoading = true

                    val photoFile = File(
                        context.cacheDir,
                        "receipt.jpg"
                    )

                    val outputOptions =
                        ImageCapture.OutputFileOptions.Builder(photoFile)
                            .build()

                    imageCapture?.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),

                        object : ImageCapture.OnImageSavedCallback {

                            override fun onImageSaved(
                                output: ImageCapture.OutputFileResults
                            ) {

                                uploadReceipt(
                                    photoFile,
                                    navController,
                                    context
                                ) {
                                    isLoading = false
                                }

                            }

                            override fun onError(
                                exception: ImageCaptureException
                            ) {

                                isLoading = false

                                Toast.makeText(
                                    context,
                                    "Capture Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )

                },
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
            ) {

                if (isLoading) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text("Scanning receipt...")

                    }

                } else {

                    Text("Scan Receipt")

                }
            }
        }
    }
}

/* ---------------- UPLOAD IMAGE TO BACKEND ---------------- */

fun uploadReceipt(
    file: File,
    navController: NavController,
    context: Context,
    onFinish: () -> Unit
) {

    val repo = ScanRepository()

    CoroutineScope(Dispatchers.IO).launch {

        try {

            val response = repo.scanReceipt(file)

            CoroutineScope(Dispatchers.Main).launch {

                onFinish()

                navController.navigate(
                    "addTransaction" +
                            "?name=${Uri.encode(response.name)}" +
                            "&amount=${response.amount}" +
                            "&category=${Uri.encode(response.category)}" +
                            "&note=${Uri.encode(response.note)}"
                )
            }

        } catch (e: Exception) {

            CoroutineScope(Dispatchers.Main).launch {

                onFinish()

                Toast.makeText(
                    context,
                    "Scan failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}