# GMA School Project Guidelines

*Project configuration for Greenlake Martial Arts School App*

## Project Overview

- **Purpose**: Internal use application for martial arts school management
- **Focus**: Student progress tracking, attendance, and belt requirements
- **Platforms**: Android, iOS, Web (Wasm/JS), Desktop (JVM)
- **Stack**: Kotlin Multiplatform, Compose Multiplatform, Ktor Server, MySQL
- **Architecture**: Clean Architecture, MVVM (client), Repository pattern
- **Database**: MySQL with audit logging requirements

---

## Theme System - Source of Truth

All visual styling must come from the theme system. **No hardcoded values.**

### Theme Components

1. **Colors** - `theme/Color.kt` + `theme/ColorScheme.kt`
   - Use: `MaterialTheme.colorScheme.*`
   - Never: `Color(0xFF...)`, `Color.White`, `Color.Gray`

2. **Typography** - `theme/Type.kt`
   - Use: `MaterialTheme.typography.*`
   - Never: Hardcoded font sizes or weights

3. **Shapes** - `theme/Shape.kt`
   - Use: `MaterialTheme.shapes.*` (extraSmall, small, medium, large, extraLarge)
   - Never: `RoundedCornerShape(8.dp)` directly in composables

### Available Shapes
- `MaterialTheme.shapes.extraSmall` - 4.dp corners
- `MaterialTheme.shapes.small` - 8.dp corners (buttons)
- `MaterialTheme.shapes.medium` - 12.dp corners
- `MaterialTheme.shapes.large` - 16.dp corners (cards, images)
- `MaterialTheme.shapes.extraLarge` - 28.dp corners

## Composable Structure

Every screen composable must follow this three-layer structure:

### 1. Container Function (Screen)
- **Naming**: `[Feature]Screen` (e.g., `LoginScreen`, `DashboardScreen`)
- **Purpose**: Handles state management, ViewModels, and business logic
- **Parameters**: ViewModel (with default), callbacks for navigation
- **Example**:
```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LoginView(
        uiState = uiState,
        onLoginClick = { email, password ->
            viewModel.login(email, password)
        }
    )
}
```

### 2. View Function
- **Naming**: `[Feature]View` (e.g., `LoginView`, `DashboardView`)
- **Purpose**: Pure UI rendering, no business logic
- **Parameters**: UI state, event callbacks only
- **Example**:
```kotlin
@Composable
fun LoginView(
    uiState: LoginUiState,
    onLoginClick: (email: String, password: String) -> Unit,
    onJoinNowClick: () -> Unit = {}
) {
    // UI code only - uses MaterialTheme.* and Strings.*
}
```

### 3. Preview Functions
- **Naming**: `[Feature]Preview`, `[Feature]PreviewLight`
- **Purpose**: Preview the View with sample data in both themes
- **Annotation**: `@Preview`
- **Example**:
```kotlin
@Preview
@Composable
fun LoginPreview() {
    GMATheme {  // Dark theme (default)
        LoginView(
            uiState = LoginUiState.Idle,
            onLoginClick = { _, _ -> }
        )
    }
}

@Preview
@Composable
fun LoginPreviewLight() {
    GMATheme(darkTheme = false) {  // Light theme
        LoginView(
            uiState = LoginUiState.Idle,
            onLoginClick = { _, _ -> }
        )
    }
}
```

### File Structure
```kotlin
// 1. Imports
import ...

// 2. Container (Screen)
@Composable
fun [Feature]Screen(...) { }

// 3. View
@Composable
fun [Feature]View(...) { }

// 4. Previews (at bottom)
@Preview
@Composable
fun [Feature]Preview() { }

@Preview
@Composable
fun [Feature]PreviewLight() { }
```

### Benefits
- **Separation of concerns**: Logic vs UI
- **Testability**: View can be tested independently
- **Reusability**: View can be used in different contexts
- **Preview**: Easy to preview UI without ViewModels
- **Consistency**: All screens follow the same pattern

## Error Handling Architecture

### Layer Responsibilities

**1. API Layer** (`shared/data/remote/*Api.kt`)
- Makes HTTP calls and returns response objects
- Throws raw exceptions (no mapping):
  - `HttpRequestTimeoutException` - Request timeout
  - `IOException` - Network errors
  - `ResponseException` - HTTP error responses (4xx, 5xx)
  - Generic `Exception` - Other errors
- Example:
```kotlin
class AuthApi {
    suspend fun login(request: LoginRequest): LoginResponse {
        val response = client.post("$endpoint/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("HTTP ${response.status.value}")
        }
    }
}
```

**2. Repository Layer** (`shared/data/repository/*Repository.kt`)
- Catches all exceptions from API layer
- Maps exceptions to specific `AppException` types:
  - `HttpRequestTimeoutException` → `AppException.Timeout()`
  - `IOException` → `AppException.NetworkError(e)`
  - `ResponseException` with 401 → `AppException.InvalidCredentials()`
  - `ResponseException` with 403 → `AppException.Unauthorized()`
  - `ResponseException` with 5xx → `AppException.ServerError()`
  - Other → `AppException.Unknown(message, e)`
- Wraps results in `Result<Success, Failure>`
- Example:
```kotlin
class LoginRepository {
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            Result.success(authApi.login(LoginRequest(username, password)))
        } catch (e: HttpRequestTimeoutException) {
            Result.failure(AppException.Timeout())
        } catch (e: ResponseException) {
            val appException = when (e.response.status) {
                HttpStatusCode.Unauthorized -> AppException.InvalidCredentials()
                // ...
            }
            Result.failure(appException)
        } catch (e: Exception) {
            Result.failure(AppException.Unknown(e.message ?: "Unknown error", e))
        }
    }
}
```

**3. ViewModel Layer** (`composeApp/ui/viewmodel/*ViewModel.kt`)
- Unwraps `Result` from repository
- Uses `ErrorMapper.toMessage(error)` to convert `AppException` to user-friendly strings
- Updates UI state with error message
- Example:
```kotlin
class LoginViewModel {
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            
            loginRepository.login(username, password)
                .onSuccess { response ->
                    _uiState.value = LoginUiState.Success(response.user, response.token)
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Error(ErrorMapper.toMessage(error))
                }
        }
    }
}
```

**4. UI Layer** (`composeApp/ui/screens/*Screen.kt`)
- Displays error from state
- Login: Inline error above button
- Dashboard/Others: Snackbar
- Example:
```kotlin
if (uiState is LoginUiState.Error) {
    Text(
        text = uiState.message,
        color = MaterialTheme.colorScheme.error
    )
}
```

### Summary Flow
```
API → Throws raw exceptions
Repository → Maps to AppException, wraps in Result
ViewModel → Uses ErrorMapper, updates UI state
UI → Displays error message
```

## Text Resources

- **NEVER** hardcode text strings in composables
- **ALWAYS** use `Strings.*` object from `com.gma.tsunjo.school.resources.Strings`
- All user-facing text must be externalized for maintainability
- Use `ErrorMapper.toMessage()` for consistent error messages across the app

## State Management

- Use sealed classes for UI state
- Include: `Idle`, `Loading`, `Success`, `Error(message: String)`
- ViewModels expose `StateFlow<UiState>`
- UI collects state with `collectAsState()`

## Dependency Injection

- Use Koin for DI
- ViewModels: `koinViewModel()` in Screen composables
- Repositories/APIs: Constructor injection

## Network Configuration

- Default timeout: 30 seconds (connect, socket, request)
- Configured in `HttpClientFactory`

## Naming Conventions

- Screens: `[Feature]Screen` (e.g., `LoginScreen`)
- Views: `[Feature]View` (e.g., `LoginView`)
- ViewModels: `[Feature]ViewModel`
- Previews: `[Feature]Preview`, `[Feature]PreviewLight`

## Before Making Changes

1. Follow Screen → View → Preview pattern
2. Use theme system - no hardcoded colors/shapes/text
3. Externalize all text to `Strings` object
4. Follow error handling flow (API → Repository → ViewModel → UI)
5. Add both dark and light theme previews
6. Use `ErrorMapper.toMessage()` for error messages
