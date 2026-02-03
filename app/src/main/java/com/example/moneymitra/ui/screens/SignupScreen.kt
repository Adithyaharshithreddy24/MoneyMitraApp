package com.example.moneymitra.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymitra.R

@Composable
fun SignupScreen(
    onGoogleClick: () -> Unit,
    onSignupClick: (String, String, String) -> Unit,
    onSignInClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {

        /* ---------------- TOP DARK HEADER ---------------- */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)

                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF000000),
                            Color(0xFF282B8C)
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Spacer(Modifier.height(40.dp))
                Text(
                    text = "Welcome!",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))

                Image(
                    painter = painterResource(R.drawable.logo_white),
                    contentDescription = null,
                    modifier = Modifier.size(190.dp)
                )
            }
        }

        /* ---------------- BOTTOM WHITE CARD ---------------- */

        Box(
            modifier = Modifier
                .fillMaxSize()
            ,
            contentAlignment = Alignment.BottomEnd
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(1f),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .height(6.dp)
                            .background(
                                color = Color.Black,
                                shape = RoundedCornerShape(50) // fully rounded pill
                            )

                    )

                    Spacer(Modifier.height(12.dp))


                    /* ---------- EMAIL ---------- */
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email or Username") },

                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.email),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Unspecified
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.padlock),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Unspecified
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    if (passwordVisible)
                                        R.drawable.eye
                                    else
                                        R.drawable.hidden
                                ),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        passwordVisible = !passwordVisible
                                    }
                            )
                        },
                        visualTransformation =
                            if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    /* ---------- CONFIRM PASSWORD ---------- */
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.padlock),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                        modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    if (confirmPasswordVisible) R.drawable.eye else R.drawable.hidden
                                ),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp).clickable {
                                    confirmPasswordVisible = !confirmPasswordVisible
                                }
                            )
                        },
                        visualTransformation =
                            if (confirmPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(Modifier.height(18.dp))


                    /* ---------- SIGN UP BUTTON (GRADIENT) ---------- */
                    Button(
                        onClick = {
                            onSignupClick(email, password, confirmPassword)
                        },
                        modifier = Modifier
                            .width(200.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF000000),
                                            Color(0xFF282B8C)
                                        )
                                    ),
                                    shape = RoundedCornerShape(30.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign up",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row {
                        Text("Have an account? ")
                        Text(
                            "Sign in here",
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onSignInClick() }
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(Modifier.weight(1f))
                        Text(
                            "or",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Divider(Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth(1f)
                            .clickable { onGoogleClick() },
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(8.dp)

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            Spacer(Modifier.width(20.dp))
                            Image(
                                painter = painterResource(R.drawable.google),
                                contentDescription = "Google",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(30.dp)
                                    .clickable { onGoogleClick() }
                            )
                            Spacer(Modifier.width(20.dp))
                            Text(
                                "Continue with Google",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
