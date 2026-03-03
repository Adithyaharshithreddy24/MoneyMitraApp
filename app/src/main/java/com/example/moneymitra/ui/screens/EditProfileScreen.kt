package com.example.moneymitra.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymitra.R
import com.example.moneymitra.auth.ProfileRepository
import com.example.moneymitra.auth.UserRepository
import com.example.moneymitra.ui.components.DobPickerField
import com.example.moneymitra.ui.components.GenderDropdownField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onProfileSaved: () -> Unit
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val user = FirebaseAuth.getInstance().currentUser ?: return
    val db = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf("") }
    var originalUsername by remember { mutableStateOf("") }

    var checkingUsername by remember { mutableStateOf(false) }
    var isUsernameValid by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var upiid by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    /* ---------- LOAD USER DATA ---------- */
    LaunchedEffect(Unit) {
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener {
                val fetchedUsername = it.getString("username") ?: ""
                username = fetchedUsername
                originalUsername = fetchedUsername

                firstName = it.getString("firstName") ?: ""
                lastName = it.getString("lastName") ?: ""
                phone = it.getString("phone") ?: ""
                dob = it.getString("dob") ?: ""
                gender = it.getString("gender") ?: ""
                upiid=it.getString("upiid") ?: ""
                isUsernameValid = true
            }
    }

    /* ---------- USERNAME VALIDATION ---------- */
    LaunchedEffect(username) {
        usernameError = null
        isUsernameValid = false

        if (username.length < 8) {
            checkingUsername = false
            usernameError = "Username must be at least 8 characters"
            return@LaunchedEffect
        }

        if (username == originalUsername) {
            isUsernameValid = true
            return@LaunchedEffect
        }

        checkingUsername = true
        delay(500)

        UserRepository.isUsernameAvailable(username) { available ->
            checkingUsername = false
            isUsernameValid = available
            usernameError = if (!available) "Username already used" else null
        }
    }

    val buttonGradient = if (isDark) {
        Brush.horizontalGradient(
            listOf(Color(0xFF283593), Color(0xFF5C6BC0))
        )
    } else {
        Brush.horizontalGradient(
            listOf(Color.Black, Color(0xFF282B8C))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .imePadding() // 🔥 KEYBOARD SAFE
            .padding(16.dp)
    ) {

        /* ---------- HEADER ---------- */
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = colors.onBackground
                )
            }
            Text(
                "Edit Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )
        }

        Spacer(Modifier.height(24.dp))

        /* ---------- PROFILE IMAGE (Responsive) ---------- */
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = null,
                modifier = Modifier
                    .size((screenHeight * 0.15f).dp) // 🔥 Responsive
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }

        Spacer(Modifier.height(24.dp))

        /* ---------- USERNAME ---------- */
        OutlinedTextField(
            value = username,
            onValueChange = { username = it.trim().lowercase() },
            label = {
                Text(
                    buildAnnotatedString {
                        append("Username ")
                        withStyle(
                            style = SpanStyle(color = MaterialTheme.colorScheme.error)
                        ) {
                            append("*")
                        }
                    }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = usernameError != null,
            supportingText = {
                when {
                    checkingUsername ->
                        Text("Checking availability...")

                    usernameError != null ->
                        Text(usernameError!!, color = colors.error)

                    isUsernameValid && username != originalUsername ->
                        Text("Username available", color = Color(0xFF2E7D32))
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = {
                Text(
                    buildAnnotatedString {
                        append("First Name ")
                        withStyle(
                            style = SpanStyle(color = MaterialTheme.colorScheme.error)
                        ) {
                            append("*")
                        }
                    }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.outline
            ),
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = upiid,
            onValueChange = { upiid = it },
            label = {
                Text(
                    buildAnnotatedString {
                        append("UPI ID ")
                        withStyle(
                            style = SpanStyle(color = MaterialTheme.colorScheme.error)
                        ) {
                            append("*")
                        }
                    }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {},
            enabled = false,
            label = { Text("Email") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Row {
            DobPickerField(
                dob = dob,
                onDateSelected = { dob = it },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            GenderDropdownField(
                gender,
                { gender = it },
                Modifier.weight(.8f)
            )
        }

        Spacer(Modifier.height(32.dp))
        val isValid =
            !loading &&
                    !checkingUsername &&
                    isUsernameValid &&
                    username.isNotBlank() &&
                    upiid.isNotBlank() &&
                    firstName.isNotBlank()

        Button(
            onClick = {

                if (!isValid) {
                    Toast.makeText(
                        context,
                        "Please fill all required details correctly",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                loading = true

                ProfileRepository.saveProfile(
                    uid = user.uid,
                    data = mapOf(
                        "username" to username,
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "upiid" to upiid,
                        "email" to email,
                        "phone" to phone,
                        "dob" to dob,
                        "gender" to gender,
                        "profileCompleted" to true
                    ),
                    onSuccess = {
                        loading = false
                        onProfileSaved()
                    },
                    onError = {
                        loading = false
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp)
        ) {

            val disabledGradient = Brush.horizontalGradient(
                listOf(colors.surfaceVariant, colors.surface)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = if (isValid) buttonGradient else disabledGradient,
                        shape = RoundedCornerShape(26.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "SAVE CHANGES",
                        color = if (isValid)
                            Color.White
                        else
                            colors.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
