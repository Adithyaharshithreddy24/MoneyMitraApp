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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.modifier.modifierLocalConsumer
import com.example.moneymitra.ui.theme.LightGradientEnd
import com.example.moneymitra.ui.theme.LightGradientStart
import com.example.moneymitra.ui.theme.DarkLinkBlue
import java.security.KeyStore
import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.Switch
import androidx.compose.ui.platform.LocalContext
import com.example.moneymitra.utils.SettingsManager
import androidx.core.app.NotificationManagerCompat
import androidx.compose.runtime.LaunchedEffect
import android.content.Context
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {

    val isDark = isSystemInDarkTheme()
    val buttonGradient = if (isDark) {
        Brush.horizontalGradient(
            listOf(Color(0xFF283593), Color(0xFF5C6BC0))
        )
    } else {
        Brush.horizontalGradient(
            listOf(LightGradientStart, LightGradientEnd)
        )
    }
    val colors = MaterialTheme.colorScheme
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
            .background(colors.background)
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

        Spacer(Modifier.height(8.dp))

        /* -------- Add Account Button -------- */

        val isDark = isSystemInDarkTheme()

        Button(
            onClick = { showAddAccount = true },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(buttonGradient, RoundedCornerShape(26.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ Add Account",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        SettingsCard(
            onEditProfile = onEditProfile,
            onChangePassword = {},
            onHelpClick = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = Color.Red
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Logout",
                color = Color.Red
            )
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

    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            val curveStart = size.height * 0.25f
            val curveDepth = size.height * 0.75f


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
                    listOf(Color.Black, colors.primary)
                )
            )
        }

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
                    contentDescription = null,
                    tint = colors.onPrimary
                )
            }

            Text(
                text = "Profile",
                color = colors.onPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Transparent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = toCamelCase(name.ifEmpty { "User" }),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            if (username.isNotEmpty()) {
                Text(
                    text = "@$username",
                    color = colors.onBackground.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
    }
}
@Composable
fun AccountCard(account: Account) {

    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .width(220.dp)
            .height(145.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(colors.primary, colors.secondary)
                    )
                )
                .padding(16.dp)
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = account.bankName.uppercase(),
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

                Text(
                    text = toCamelCase(account.accType),
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "**** **** **** ${account.accNo.takeLast(4)}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
@Composable
fun SettingsCard(
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onHelpClick: () -> Unit
) {

    val context = LocalContext.current

    var autoRead by remember {
        mutableStateOf(
            SettingsManager.isAutoReadEnabled(context)
        )
    }


    val colors = MaterialTheme.colorScheme
    var showPermissionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        val enabled = isNotificationAccessEnabled(context)

        autoRead = enabled

        SettingsManager.saveAutoRead(context, enabled)
    }
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        )
    ) {

        Column {

            SettingRow(Icons.Default.Person, "Edit profile information", onClick = onEditProfile)
            SettingSwitchRow(
                icon = Icons.Default.Notifications,
                title = "Auto Read Notifications",
                checked = autoRead,
                onCheckedChange = {

                    if (it) {

                        val enabled = isNotificationAccessEnabled(context)

                        if (enabled) {
                            // Permission already granted
                            autoRead = true
                            SettingsManager.saveAutoRead(context, true)

                        } else {
                            // Permission not granted → ask user
                            showPermissionDialog = true
                        }

                    } else {

                        autoRead = false
                        SettingsManager.saveAutoRead(context, false)
                    }
                }
            )
            SettingRow(Icons.Default.Language, "Language", value = "English")

            SettingRow(Icons.Default.Lock, "Change password", onClick = onChangePassword)

            SettingRow(Icons.Default.Help, "Help & Support", onClick = onHelpClick)

            SettingRow(Icons.Default.Email, "Contact us")

            SettingRow(Icons.Default.Security, "Privacy policy")

            if (showPermissionDialog) {

                AlertDialog(
                    onDismissRequest = { showPermissionDialog = false },

                    title = {
                        Text("Allow Notification Access")
                    },

                    text = {
                        Text(
                            "MoneyMitra needs access to notifications to automatically detect your bank transactions."
                        )
                    },

                    confirmButton = {

                        TextButton(onClick = {

                            showPermissionDialog = false

                            val intent =
                                Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)

                            context.startActivity(intent)

                        }) {
                            Text("open settings")
                        }
                    },

                    dismissButton = {

                        TextButton(onClick = {
                            showPermissionDialog = false
                        }) {
                            Text("Cancel")
                        }
                    }
                )
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

    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.onBackground
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = colors.onSurface,
            fontSize = 15.sp
        )

        value?.let {
            Text(
                text = it,
                color = DarkLinkBlue,
                fontSize = 14.sp
            )
        }

        if (onClick != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colors.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
@Composable
fun SettingSwitchRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.onBackground
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = colors.onSurface,
            fontSize = 15.sp
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
fun toCamelCase(text: String): String {
    return text
        .lowercase()
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.replaceFirstChar { char ->
                char.uppercase()
            }
        }
}

fun isNotificationAccessEnabled(context: Context): Boolean {

    val enabledPackages =
        NotificationManagerCompat.getEnabledListenerPackages(context)

    return enabledPackages.contains(context.packageName)
}