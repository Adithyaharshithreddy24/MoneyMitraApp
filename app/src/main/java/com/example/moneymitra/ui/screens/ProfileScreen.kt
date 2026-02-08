package com.example.moneymitra.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneymitra.R
import com.example.moneymitra.data.model.Account
import com.example.moneymitra.ui.viewmodel.ProfileViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.modifier.modifierLocalConsumer
import java.security.KeyStore

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEditProfile:()-> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    var showAddAccount by remember { mutableStateOf(false) }

    if (showAddAccount) {
        AddAccountBottomSheet(
            onDismiss = { showAddAccount = false },
            onAccountSaved = { showAddAccount = false }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .verticalScroll(rememberScrollState())
    ) {


        CurvedProfileHeader(
            name = viewModel.fullName,
            username = viewModel.user.username,
            onBack = onBack
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.offset(y = (-24).dp)
        ) {
            items(viewModel.accounts) { account ->
                AccountCard(account)
                Spacer(modifier = Modifier.width(12.dp))
            }
        }

        Button(
            onClick = { showAddAccount = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF000000), // start
                                Color(0xFF282B8C)  // end
                            )
                        ),
                        shape = RoundedCornerShape(26.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ Add Account",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsCard(
            onEditProfile = onEditProfile ,
            onChangePassword = { /* open change password */ },
            onHelpClick = { /* open help screen */ }
        )


        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = onLogout,   // ✅ FIXED
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = Color.Red
            )
            Spacer(Modifier.width(6.dp))
            Text("Logout", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
@Composable
fun CurvedProfileHeader(
    name: String,
    username: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {

        // 🔹 Background curve
        Canvas(modifier = Modifier.fillMaxSize()) {

            val curveStart = size.height - 550f
            val curveDepth = size.height - 200f

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, curveStart)
                quadraticBezierTo(
                    size.width / 2f,
                    curveDepth,
                    size.width,
                    curveStart
                )
                lineTo(size.width, 0f)
                close()
            }

            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF000000), // start
                        Color(0xFF282B8C)  // end
                    )
                )
            )
        }

        // 🔹 Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Profile",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 🔹 Profile image + name (INSIDE Box)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name.ifEmpty { "User" },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B1A3A)
            )

            if (username.isNotEmpty()) {
                Text(
                    text = "@$username",
                    color = Color(0xFF0B1A3A),
                    fontSize = 14.sp
                )
            }
        }
    }
}
fun toCamelCase(text: String): String =
    text.lowercase()
        .split(" ")
        .joinToString(" ") {
            it.replaceFirstChar { ch ->
                if (ch.isLowerCase()) ch.titlecase() else ch.toString()
            }
        }
@Composable
fun AccountCard(account: Account) {

    Card(
        modifier = Modifier
            .width(220.dp)
            .height(145.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF334BA8),Color(0xFF282B8C)
                        )
                    )
                )
                .padding(16.dp)
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                /* -------- TOP ROW -------- */
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = account.bankName.uppercase(), // 🔥 CAPS
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
                /* -------- ACCOUNT TYPE -------- */
                Text(
                    text = toCamelCase(account.accType), // 🔥 camel case
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.weight(1f))

                /* -------- ACCOUNT NUMBER -------- */
                Text(
                    text = "**** **** **** ${account.accNo.takeLast(4)}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))


            }
        }
    }
}


@Composable
fun SettingRow(
    icon: ImageVector,
    title: String,
    value: String? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f))
        value?.let {
            Text(it, color = Color(0xFF4F6BD8))
        }
    }
}


@Composable
fun SettingsCard(
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onHelpClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            SettingRow(
                icon = Icons.Default.Person,
                title = "Edit profile information",
                onClick = onEditProfile
            )

            SettingRow(
                icon = Icons.Default.Language,
                title = "Language",
                value = "English"
            )

            SettingRow(
                icon = Icons.Default.Lock,
                title = "Change password",
                onClick = onChangePassword
            )

            SettingRow(
                icon = Icons.Default.Help,
                title = "Help & Support",
                onClick = onHelpClick
            )

            SettingRow(
                icon = Icons.Default.Email,
                title = "Contact us"
            )

            SettingRow(
                icon = Icons.Default.Security,
                title = "Privacy policy"
            )
        }
    }
}
