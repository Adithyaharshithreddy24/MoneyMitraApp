package com.example.moneymitra.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.text.font.FontWeight
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
@Composable
fun QrSection(upiId: String) {

    val context = LocalContext.current

    val bitmap = remember(upiId) {
        generateQrCode("upi://pay?pa=$upiId&pn=MoneyMitra&cu=INR")
    }
    Card(
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF000000),
                            Color(0xFF0D133D),
                            Color(0xFF1A237E)
                        )
                    )
                )
                .padding(vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Scan to Pay",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .background(
                        Color.White,
                        RoundedCornerShape(24.dp)
                    )
                    .padding(8.dp)
            ) {

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "UPI QR",
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "UPI ID",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = upiId,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        copyUpi(context, upiId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(
                    onClick = { saveQrToGallery(context, bitmap)},
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {

                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color.Black
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = "Download",
                        color = Color.Black
                    )
                }

                OutlinedButton(
                    onClick = { shareQr(context, bitmap)},
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color.White)
                ) {

                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = "Share",
                        color = Color.White
                    )
                }
            }
        }
    }
}
fun copyUpi(context: Context, upi: String) {

    val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE)
                as ClipboardManager

    val clip = ClipData.newPlainText("UPI ID", upi)

    clipboard.setPrimaryClip(clip)

    Toast.makeText(
        context,
        "UPI ID Copied",
        Toast.LENGTH_SHORT
    ).show()
}
fun generateQrCode(text: String): Bitmap {

    val size = 512

    val hints = hashMapOf<com.google.zxing.EncodeHintType, Any>().apply {

        put(
            com.google.zxing.EncodeHintType.ERROR_CORRECTION,
            com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H
        )

        put(
            com.google.zxing.EncodeHintType.MARGIN,
            2
        )
    }

    val writer = QRCodeWriter()

    val bitMatrix =
        writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints)

    val bmp =
        Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

    for (x in 0 until size) {
        for (y in 0 until size) {

            bmp.setPixel(
                x,
                y,
                if (bitMatrix[x, y])
                    android.graphics.Color.BLACK
                else
                    android.graphics.Color.WHITE
            )
        }
    }

    return bmp
}
fun saveQrToGallery(context: Context, bitmap: Bitmap) {

    val filename = "MoneyMitra_QR_${System.currentTimeMillis()}.png"

    val resolver = context.contentResolver

    val contentValues = android.content.ContentValues().apply {

        put(
            android.provider.MediaStore.Images.Media.DISPLAY_NAME,
            filename
        )

        put(
            android.provider.MediaStore.Images.Media.MIME_TYPE,
            "image/png"
        )

        put(
            android.provider.MediaStore.Images.Media.RELATIVE_PATH,
            "Pictures/MoneyMitra"
        )
    }

    val uri = resolver.insert(
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )

    uri?.let {

        val stream = resolver.openOutputStream(it)

        stream?.use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }

        Toast.makeText(
            context,
            "QR Saved to Gallery",
            Toast.LENGTH_SHORT
        ).show()
    }
}
fun shareQr(context: Context, bitmap: Bitmap) {

    val file = java.io.File(
        context.cacheDir,
        "qr_code.png"
    )

    val stream = java.io.FileOutputStream(file)

    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

    stream.close()

    val uri = androidx.core.content.FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        file
    )

    val intent = android.content.Intent(
        android.content.Intent.ACTION_SEND
    )

    intent.type = "image/png"

    intent.putExtra(
        android.content.Intent.EXTRA_STREAM,
        uri
    )

    intent.addFlags(
        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
    )

    context.startActivity(
        android.content.Intent.createChooser(intent, "Share QR")
    )
}