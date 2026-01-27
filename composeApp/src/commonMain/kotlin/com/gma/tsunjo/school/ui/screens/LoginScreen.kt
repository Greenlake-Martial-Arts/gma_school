// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gma.tsunjo.school.resources.Strings
import com.gma.tsunjo.school.theme.GMATheme
import com.gma.tsunjo.school.ui.viewmodel.LoginUiState
import com.gma.tsunjo.school.ui.viewmodel.LoginViewModel
import gma_school.composeapp.generated.resources.Res
import gma_school.composeapp.generated.resources.gma_logo_600_square
import gma_school.composeapp.generated.resources.gma_logo_translucent_v2
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinInject(),
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    LoginView(
        uiState = uiState,
        onLoginClick = { email, password ->
            viewModel.login(email, password)
        }
    )
}

@Composable
fun LoginView(
    uiState: LoginUiState,
    onLoginClick: (email: String, password: String) -> Unit,
    onJoinNowClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo
            Image(
                painter = painterResource(Res.drawable.gma_logo_600_square),
                contentDescription = "GMA Logo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Member Login title
            Text(
                text = Strings.LOGIN_TITLE,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.sp
            )

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email field
            Text(
                text = Strings.LOGIN_USERNAME_LABEL,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        "your@email.com",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = uiState !is LoginUiState.Loading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Password field
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.LOGIN_PASSWORD_LABEL,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = onForgotPasswordClick) {
                    Text(
                        text = "Forgot?",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        "••••••••",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "👁" else "👁‍🗨",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = uiState !is LoginUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            if (uiState is LoginUiState.Error) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enter Dojo button
            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 2.dp,
                        color = if (uiState !is LoginUiState.Loading && email.isNotBlank() && password.isNotBlank()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        },
                        shape = MaterialTheme.shapes.small
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.small,
                enabled = uiState !is LoginUiState.Loading && email.isNotBlank() && password.isNotBlank()
            ) {
                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = Strings.LOGIN_BUTTON,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Join now
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New to the evolution?  ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onJoinNowClick) {
                    Text(
                        text = "JOIN NOW",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    GMATheme {
        LoginView(
            uiState = LoginUiState.Idle,
            onLoginClick = { _, _ -> }
        )
    }
}

@Preview
@Composable
fun LoginPreviewLight() {
    GMATheme(darkTheme = true) {
        LoginView(
            uiState = LoginUiState.Idle,
            onLoginClick = { _, _ -> }
        )
    }
}
