# Token Storage - Quick Reference

## Platform Storage Table

| Platform              | Storage Type      | Location                                     | Encrypted           | Persists | Cleared On         |
|-----------------------|-------------------|----------------------------------------------|---------------------|----------|--------------------|
| **Android**           | SharedPreferences | `/data/data/.../shared_prefs/`               | Optional            | ✅ Yes    | App uninstall      |
| **iOS**               | NSUserDefaults    | `~/Library/Preferences/`                     | Optional (Keychain) | ✅ Yes    | App uninstall      |
| **macOS**             | NSUserDefaults    | `~/Library/Preferences/`                     | Optional (Keychain) | ✅ Yes    | App uninstall      |
| **Desktop (Windows)** | Registry          | `HKEY_CURRENT_USER\Software\JavaSoft\Prefs\` | ❌ No                | ✅ Yes    | May persist*       |
| **Desktop (macOS)**   | Preferences       | `~/Library/Preferences/`                     | ❌ No                | ✅ Yes    | May persist*       |
| **Desktop (Linux)**   | Preferences       | `~/.java/.userPrefs/`                        | ❌ No                | ✅ Yes    | May persist*       |
| **Web (JS)**          | localStorage      | Browser storage                              | ❌ No                | ✅ Yes    | Clear browser data |
| **Web (WasmJS)**      | localStorage      | Browser storage                              | ❌ No                | ✅ Yes    | Clear browser data |

*Desktop: Preferences may persist after app uninstall (OS-dependent, not guaranteed)

---

## Implementation Classes

| Platform        | Class                       | Library                |
|-----------------|-----------------------------|------------------------|
| Android         | `SharedPreferencesSettings` | multiplatform-settings |
| iOS/macOS       | `NSUserDefaultsSettings`    | multiplatform-settings |
| Desktop (JVM)   | `PreferencesSettings`       | multiplatform-settings |
| Web (JS/WasmJS) | `StorageSettings`           | multiplatform-settings |

---

## Security Levels

| Platform  | Default Security       | Enhanced Option              |
|-----------|------------------------|------------------------------|
| Android   | App-private storage    | EncryptedSharedPreferences   |
| iOS/macOS | App-sandboxed          | Keychain (hardware-backed)   |
| Desktop   | User-level permissions | OS-specific secure storage   |
| Web       | Same-origin policy     | HTTPS + short token lifetime |

---

## Token Manager API

```kotlin
interface TokenManager {
    suspend fun saveToken(token: String)      // Save after login
    fun getToken(): String?                   // Get for API calls
    suspend fun clearToken()                  // Clear on logout
    fun isAuthenticated(): Boolean            // Check if logged in
}
```

---

## Quick Usage

### Save Token

```kotlin
tokenManager.saveToken("eyJhbGci...")
```

### Get Token

```kotlin
val token = tokenManager.getToken()
```

### Clear Token

```kotlin
tokenManager.clearToken()
```

### Check Auth

```kotlin
if (tokenManager.isAuthenticated()) {
    // User is logged in
}
```

---

## HTTP Integration

**Automatic:** Token is automatically added to all API requests via `HttpClient` interceptor.

```
Authorization: Bearer {token}
```

No manual header management needed.

---

## Key Files

| File                                                         | Purpose              |
|--------------------------------------------------------------|----------------------|
| `shared/src/commonMain/.../auth/TokenManager.kt`             | Interface definition |
| `shared/src/commonMain/.../auth/TokenManagerImpl.kt`         | Implementation       |
| `shared/src/androidMain/.../auth/SettingsFactory.android.kt` | Android storage      |
| `shared/src/iosMain/.../auth/SettingsFactory.ios.kt`         | iOS storage          |
| `shared/src/jvmMain/.../auth/SettingsFactory.jvm.kt`         | Desktop storage      |
| `shared/src/jsMain/.../auth/SettingsFactory.js.kt`           | Web JS storage       |
| `shared/src/wasmJsMain/.../auth/SettingsFactory.wasmJs.kt`   | Web Wasm storage     |
| `shared/src/commonMain/.../data/remote/HttpClientFactory.kt` | HTTP interceptor     |

---

## Documentation

- **Full Details**: See `TOKEN_STORAGE_PLATFORMS.md`
- **Auth Flow**: See `server/docs/AUTHENTICATION_FLOW.md`
- **Implementation**: See `IMPLEMENTATION_COMPLETE.md`

