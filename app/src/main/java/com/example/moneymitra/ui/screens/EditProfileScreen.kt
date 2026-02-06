package com.example.moneymitra.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

                isUsernameValid = true
            }
    }

    /* ---------- USERNAME VALIDATION (ONLY PLACE FIRESTORE IS CALLED) ---------- */
    LaunchedEffect(username) {
        usernameError = null
        isUsernameValid = false

        if (username.length < 4) return@LaunchedEffect

        // allow unchanged username
        if (username == originalUsername) {
            isUsernameValid = true
            return@LaunchedEffect
        }

        checkingUsername = true
        delay(500) // debounce

        UserRepository.isUsernameAvailable(username) { available ->
            checkingUsername = false
            isUsernameValid = available
            usernameError = if (!available) "Username already used" else null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        /* ---------- HEADER ---------- */
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Edit Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))

        /* ---------- PROFILE IMAGE ---------- */
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile",
                modifier = Modifier.size(120.dp).clip(CircleShape)
            )
        }

        Spacer(Modifier.height(20.dp))

        /* ---------- USERNAME ---------- */
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it.trim().lowercase()
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            isError = usernameError != null,
            supportingText = {
                when {
                    checkingUsername -> Text("Checking availability...")
                    usernameError != null ->
                        Text(usernameError!!, color = Color.Red)
                    isUsernameValid && username != originalUsername ->
                        Text("Username available", color = Color(0xFF2E7D32))
                }
            }
        )

        Spacer(Modifier.height(10.dp))

        /* ---------- OTHER FIELDS ---------- */
        OutlinedTextField(firstName, { firstName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(lastName, { lastName = it }, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(email, {}, enabled = false, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            DobPickerField(dob, { dob = it }, Modifier.width(200.dp))
            Spacer(Modifier.width(12.dp))
            GenderDropdownField(gender, { gender = it }, Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(30.dp))

        /* ---------- SAVE BUTTON ---------- */
        Button(
            onClick = {
                loading = true
                ProfileRepository.saveProfile(
                    uid = user.uid,
                    data = mapOf(
                        "username" to username,
                        "firstName" to firstName,
                        "lastName" to lastName,
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
                .height(50.dp),
            enabled = !loading &&
                    isUsernameValid &&
                    username.isNotBlank() &&
                    firstName.isNotBlank()
        ) {
            Text(if (loading) "Saving..." else "SAVE CHANGES")
        }
    }
}
