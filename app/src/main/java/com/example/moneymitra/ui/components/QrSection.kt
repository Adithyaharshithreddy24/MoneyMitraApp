package com.example.moneymitra.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun QrSection(upiId: String) {

    val bitmap = remember {
        generateQrCode("upi://pay?pa=$upiId")
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp,40.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(220.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "UPI ID: $upiId",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedButton(onClick = { }) {
                        Text(
                            text = "Download",
                            color = Color.White
                        )
                    }

                    OutlinedButton(onClick = { }) {
                        Text(
                            text = "Share",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

fun generateQrCode(text: String): Bitmap {

    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)

    val width = bitMatrix.width
    val height = bitMatrix.height
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

    for (x in 0 until width) {
        for (y in 0 until height) {

            bmp.setPixel(
                x,
                y,
                if (bitMatrix[x, y]) android.graphics.Color.BLACK
                else android.graphics.Color.WHITE
            )
        }
    }

    return bmp
}