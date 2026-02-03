package com.example.moneymitra.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.moneymitra.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

@Composable
fun EditProfileScreen(
    onProfileSaved: () -> Unit
) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser ?: return
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    /* ---------------- STATE ---------------- */
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    /* ---------------- LOAD EXISTING PROFILE ---------------- */
    LaunchedEffect(user.uid) {
        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    username = doc.getString("username") ?: ""
                    firstName = doc.getString("firstName") ?: ""
                    lastName = doc.getString("lastName") ?: ""
                    phone = doc.getString("phone") ?: ""
                    dob = doc.getString("dob") ?: ""
                    gender = doc.getString("gender") ?: ""
                    profileImageUrl = doc.getString("photoUrl")
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
    }

    /* ---------------- IMAGE PICKER ---------------- */
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
        }

    /* ---------------- UI ---------------- */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp)
    ) {

        Text(
            text = "Edit Profile",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        /* ---------- PROFILE IMAGE ---------- */
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box {
                when {
                    imageUri != null -> {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    }

                    profileImageUrl != null -> {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    }

                    else -> {
                        Image(
                            painter = painterResource(R.drawable.profile),
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Edit",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        /* ---------- INPUT FIELDS ---------- */
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {},
            enabled = false,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(Modifier.height(10.dp))
        Row {
            Row(
                modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.weight(1f)
                )
            }

        }

        Spacer(Modifier.height(20.dp))

        /* ---------- SAVE BUTTON ---------- */
        Button(
            onClick = {
                if (username.isBlank() || firstName.isBlank()) {
                    Toast.makeText(context, "Fill required fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                loading = true

                fun saveProfile(photoUrl: String?) {
                    val data = mutableMapOf<String, Any>(
                        "username" to username,
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "phone" to phone,
                        "dob" to dob,
                        "gender" to gender,
                        "profileCompleted" to true
                    )

                    photoUrl?.let { data["photoUrl"] = it }

                    db.collection("users")
                        .document(user.uid)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener {
                            loading = false
                            onProfileSaved()
                        }
                        .addOnFailureListener {
                            loading = false
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                }

                if (imageUri != null) {
                    val ref = storage.reference
                        .child("profile_images/${user.uid}/profile.jpg")

                    ref.putFile(imageUri!!)
                        .continueWithTask { task ->
                            if (!task.isSuccessful) {
                                throw task.exception ?: Exception("Upload failed")
                            }
                            ref.downloadUrl
                        }
                        .addOnSuccessListener { uri ->
                            saveProfile(uri.toString())
                        }
                        .addOnFailureListener {
                            loading = false
                            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    saveProfile(null)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !loading,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (loading) "Saving..." else "SAVE CHANGES")
        }

        Spacer(Modifier.height(40.dp))
    }
}

/* ---------- REUSABLE FIELD ---------- */
@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Column(modifier.padding(vertical = 6.dp)) {
        Text(label, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Composable
fun DobPickerField(
    dob: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = remember { java.util.Calendar.getInstance() }

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, day ->
                val formattedDate =
                    "%02d/%02d/%04d".format(day, month + 1, year)
                onDateSelected(formattedDate)
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        ).apply {
            // ❌ Disable future dates (optional but recommended)
            datePicker.maxDate = System.currentTimeMillis()
        }
    }

    OutlinedTextField(
        value = dob,
        onValueChange = {},
        readOnly = true,
        label = { Text("Date of Birth") },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.calendar),
                contentDescription = "Pick date",
                modifier = Modifier.size(30.dp).clickable {
                    datePickerDialog.show()
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdownField(
    gender: String,
    onGenderSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Male", "Female", "Other")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = gender,
            onValueChange = {},
            readOnly = true,
            label = { Text("Gender") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onGenderSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
