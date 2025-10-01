package com.skripsi.posyandudigital.ui.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (userRole: String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val username = viewModel.username.value
    val password = viewModel.password.value
    val passwordVisible = viewModel.passwordVisible.value
    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value
    val loggedInUser = viewModel.loggedInUser.value

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loggedInUser, errorMessage) {
        loggedInUser?.let { user ->
            onLoginSuccess(user.role)
            viewModel.onNavigationDone()
        }
        errorMessage?.let {
            snackbarHostState.showSnackbar(message = it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFE1BEE7)
                        )
                    )
                )
        ) {
            TopRightArt(modifier = Modifier.align(Alignment.TopEnd))
            BottomLeftArt(modifier = Modifier.align(Alignment.BottomStart))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = "Logo Posyandu",
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Posyandu Digital",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Selamat datang kembali",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(48.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { viewModel.onUsernameChange(it) },
                            label = { Text("Username atau Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image =
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                    Icon(
                                        imageVector = image,
                                        contentDescription = "Toggle Password Visibility"
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            "Login",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopRightArt(modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = Color(0xFFCE93D8)

    Canvas(
        modifier = modifier
            .size(200.dp)
            .clip(RoundedCornerShape(bottomStart = 100.dp))
    ) {
        val path = Path().apply {
            moveTo(size.width, 0f)
            lineTo(size.width, size.height)
            quadraticBezierTo(size.width * 0.4f, size.height * 0.8f, 0f, size.height)
            lineTo(0f, 0f)
            close()
        }
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(primaryColor.copy(alpha = 0.7f), secondaryColor.copy(alpha = 0.7f)),
                start = Offset(size.width, 0f),
                end = Offset(0f, size.height)
            )
        )
    }
}

@Composable
fun BottomLeftArt(modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = Color(0xFFCE93D8)

    Canvas(
        modifier = modifier
            .size(200.dp)
            .clip(RoundedCornerShape(topEnd = 100.dp))
    ) {
        val path = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, 0f)
            quadraticBezierTo(size.width * 0.6f, size.height * 0.2f, size.width, 0f)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(secondaryColor.copy(alpha = 0.7f), primaryColor.copy(alpha = 0.7f)),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
        )
    }
}
