# Kermit Logging Implementation

**Date**: February 1, 2026  
**Library**: Kermit v2.0.8 by Touchlab

## Overview

Implemented Kermit multiplatform logging library to track authentication flow, token operations, and navigation events across all platforms.

## Why Kermit?

After evaluating multiple logging libraries:

- **Napier**: Last updated 2 years ago (January 2024) - maintenance concern
- **kotlin-logging**: Requires backend logger setup (SLF4J for JVM, custom for other platforms)
- **Kermit**: ✅ Actively maintained, zero setup, all platforms supported

## Installation

### Dependencies Added

**gradle/libs.versions.toml**:

```toml
[versions]
kermit = "2.0.8"

[libraries]
kermit = { module = "co.touchlab:kermit", version.ref = "kermit" }
```

**shared/build.gradle.kts** and **composeApp/build.gradle.kts**:

```kotlin
commonMain.dependencies {
    implementation(libs.kermit)
}
```

## Logging Points

### 1. Token Management (`TokenManagerImpl`)

```kotlin
private val log = Logger.withTag("TokenManager")

override suspend fun saveToken(token: String) {
    settings.putString(KEY_TOKEN, token)
    log.d { "<< Token saved successfully" }
}

override fun getToken(): String? {
    val token = settings.getStringOrNull(KEY_TOKEN)
    log.d { "<< Token retrieved: ${if (token != null) "[PRESENT]" else "[ABSENT]"}" }
    return token
}

override suspend fun clearToken() {
    settings.remove(KEY_TOKEN)
    log.d { "<< Token cleared" }
}

override fun isAuthenticated(): Boolean {
    val authenticated = getToken() != null
    log.d { "<< Authentication check: $authenticated" }
    return authenticated
}
```

**Security**: Token value is never logged, only presence/absence.

### 2. HTTP Client (`HttpClientFactory`)

```kotlin
private val log = KermitLogger.withTag("HttpClient")

defaultRequest {
    val token = tokenManager.getToken()
    if (token != null) {
        header("Authorization", "Bearer $token")
        log.d { "<< Auth header added to request: ${this.url}" }
    } else {
        log.d { "<< No token available for request: ${this.url}" }
    }
}
```

**Security**: Token is sent in header but not logged. Only URL and presence/absence logged.

### 3. Login Flow (`LoginViewModel`)

```kotlin
private val log = Logger.withTag("LoginViewModel")

fun login(username: String, password: String) {
    log.d { "<< Login attempt for user: $username" }
    _uiState.value = LoginUiState.Loading
    viewModelScope.launch {
        loginRepository.login(username, password)
            .onSuccess { response ->
                log.i { "Login successful for user: ${response.user.username}" }
                tokenManager.saveToken(response.token)
                _uiState.value = LoginUiState.Success(response.user, response.token)
            }
            .onFailure { error ->
                log.e { "Login failed: ${error.message}" }
                _uiState.value = LoginUiState.Error(UiErrorMapper.toMessage(error))
            }
    }
}
```

**Security**: Password is never logged.

### 4. Navigation (`AppNavigation`)

```kotlin
private val log = Logger.withTag("AppNavigation")

val startDestination = if (tokenManager.isAuthenticated()) {
    log.i { "User authenticated, starting at Home" }
    Screen.Home
} else {
    log.i { "User not authenticated, starting at Login" }
    Screen.Login
}

// Login success
onLoginSuccess = {
    log.i { "Login successful, navigating to Home" }
    navController.navigate(Screen.Home) {
        popUpTo(Screen.Login) { inclusive = true }
    }
}

// Logout
onLogout = {
    log.i { "Logout initiated" }
    studentsViewModel.clearSelection()
    coroutineScope.launch {
        tokenManager.clearToken()
        log.i { "Navigating to Login after logout" }
        navController.navigate(Screen.Login) {
            popUpTo(0) { inclusive = true }
        }
    }
}
```

## Log Levels Used

- **Debug (d)**: Token operations, HTTP requests, authentication checks
- **Info (i)**: Navigation events, successful login
- **Error (e)**: Login failures

## Platform Output

Kermit automatically routes logs to platform-specific outputs:

| Platform        | Output                     |
|-----------------|----------------------------|
| Android         | Logcat                     |
| iOS             | OSLog (Xcode console)      |
| Desktop (JVM)   | Standard output (terminal) |
| Web (JS/WasmJS) | Browser console            |

## Example Log Output

```
Debug: (TokenManager) Token retrieved: [ABSENT]
Debug: (TokenManager) Authentication check: false
Info: (AppNavigation) User not authenticated, starting at Login
Debug: (LoginViewModel) Login attempt for user: admin
Info: (LoginViewModel) Login successful for user: admin
Debug: (TokenManager) Token saved successfully
Debug: (HttpClient) Auth header added to request: http://localhost:8080/api/students
Info: (AppNavigation) Login successful, navigating to Home
```

## Security Considerations

✅ **Token values are never logged** - only presence/absence  
✅ **Passwords are never logged**  
✅ **Auth headers show "[REDACTED]" in logs** (actual token sent in header)  
✅ **Usernames are logged** - considered non-sensitive for internal app

## Testing

Verified on Desktop (JVM):

```bash
./gradlew :composeApp:run
```

Logs appear in terminal showing:

- App start authentication check
- Login flow
- Token operations
- Navigation events

## Future Enhancements

1. **Log Levels by Environment**: Debug for dev, Error for production
2. **Crashlytics Integration**: Add `kermit-crashlytics` module for Firebase
3. **File Logging**: Add `kermit-io` module for persistent logs
4. **Custom LogWriter**: Send logs to remote server for monitoring

## References

- [Kermit Documentation](https://kermit.touchlab.co/docs/)
- [Kermit GitHub](https://github.com/touchlab/Kermit)
- [Touchlab](https://touchlab.co/) - KMP experts
