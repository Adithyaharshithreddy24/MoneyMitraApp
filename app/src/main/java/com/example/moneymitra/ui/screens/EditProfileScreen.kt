package com.example.moneymitra.ui.screens

import android.app.DatePickerDialog
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
import com.example.moneymitra.auth.ProfileRepository
import com.example.moneymitra.ui.components.DobPickerField
import com.example.moneymitra.ui.components.GenderDropdownField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.moneymitra.R
import java.util.Calendar

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
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    /* ---------- LOAD DATA ---------- */
    LaunchedEffect(Unit) {
        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener {
                username = it.getString("username") ?: ""
                firstName = it.getString("firstName") ?: ""
                lastName = it.getString("lastName") ?: ""
                phone = it.getString("phone") ?: ""
                dob = it.getString("dob") ?: ""
                gender = it.getString("gender") ?: ""
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Edit Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )

                // Optional edit icon (UI only)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit",
                        modifier = Modifier.size(18.dp),
                        tint = Color.Gray
                    )
                }
            }
        }

        /* ---------- FIELDS ---------- */
        OutlinedTextField(
            username,
            { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            firstName,
            { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            lastName,
            { lastName = it },
            label = { Text("Last Name")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            email,
            {},
            enabled = false,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            phone,
            { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            DobPickerField(
                dob = dob,
                onDateSelected = { dob = it },
                modifier = Modifier.width(200.dp)
            )

            Spacer(Modifier.width(12.dp))

            GenderDropdownField(
                gender = gender,
                onGenderSelected = { gender = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(30.dp))

        /* ---------- SAVE ---------- */
        Button(
            onClick = {
                if (username.isBlank() || firstName.isBlank()) {
                    Toast.makeText(context, "Required fields missing", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                loading = true

                val data = mapOf(
                    "username" to username,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "phone" to phone,
                    "dob" to dob,
                    "gender" to gender,
                    "profileCompleted" to true
                )

                ProfileRepository.saveProfile(
                    uid = user.uid,
                    data = data,
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
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !loading
        ) {
            Text(if (loading) "Saving..." else "SAVE CHANGES")
        }
    }
}
