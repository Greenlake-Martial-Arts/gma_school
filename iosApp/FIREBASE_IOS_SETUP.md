# Firebase iOS Setup Guide

Complete guide for Firebase Analytics, Crashlytics, and Remote Config on iOS.

---

## Quick Start

For experienced developers who need the essentials:

### 1. Enable dSYM Generation (2 min)
1. Open `iosApp.xcodeproj` in Xcode
2. Select **iosApp** target → **Build Settings**
3. Search: `debug information format`
4. Set to: **DWARF with dSYM File** (Debug & Release)

### 2. Add Crashlytics Upload Script (5 min)
1. Select **iosApp** target → **Build Phases**
2. Click **+** → **New Run Script Phase**
3. Name: `Upload Crashlytics dSYMs`
4. Place AFTER "Compile Sources"
5. Script:
   ```bash
   "${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run"
   ```
6. Add Input Files:
   ```
   ${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}
   ${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}/Contents/Resources/DWARF/${PRODUCT_NAME}
   ${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}/Contents/Info.plist
   $(TARGET_BUILD_DIR)/$(UNLOCALIZED_RESOURCES_FOLDER_PATH)/GoogleService-Info.plist
   $(TARGET_BUILD_DIR)/$(EXECUTABLE_PATH)
   ```

### 3. Build & Verify (2 min)
1. Clean: **Product → Clean Build Folder** (Cmd+Shift+K)
2. Build: **Product → Build** (Cmd+B)
3. Check logs for: `✅ Successfully uploaded symbols`

---

## Complete Setup Guide

### Step 1: Install Firebase SDK

1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Go to **File → Add Package Dependencies**
3. Enter URL: `https://github.com/firebase/firebase-ios-sdk`
4. Select version: **Latest** (or 11.x.x)
5. Select packages:
   - ✅ FirebaseAnalytics
   - ✅ FirebaseCrashlytics
   - ✅ FirebaseRemoteConfig
6. Click **Add Package**

### Step 2: Configure Build Settings

#### A. Set Configuration Files
1. Select **iosApp** project (blue icon)
2. Select **iosApp** target
3. Go to **Info** tab
4. Under **Configurations**:
   - **Debug**: Set to `Debug.xcconfig`
   - **Release**: Set to `Release.xcconfig`

#### B. Enable dSYM Generation
1. Select **iosApp** target
2. Go to **Build Settings** tab
3. Click **All**
4. Search: `debug information format`
5. Set **Debug Information Format** to **DWARF with dSYM File** for:
   - Debug
   - Release

#### C. Add Firebase Plist Selection Script
1. Select **iosApp** target
2. Go to **Build Phases** tab
3. Click **+** → **New Run Script Phase**
4. Drag it BEFORE "Compile Sources"
5. Name: `Copy Firebase Config`
6. Script:
   ```bash
   # Copy the appropriate GoogleService-Info.plist based on configuration
   PLIST_NAME="${FIREBASE_PLIST_NAME:-GoogleService-Info}"
   PLIST_SOURCE="${SRCROOT}/iosApp/${PLIST_NAME}.plist"
   PLIST_DESTINATION="${BUILT_PRODUCTS_DIR}/${PRODUCT_NAME}.app/GoogleService-Info.plist"

   if [ -f "$PLIST_SOURCE" ]; then
       cp "$PLIST_SOURCE" "$PLIST_DESTINATION"
       echo "Copied ${PLIST_NAME}.plist to app bundle"
   else
       echo "error: ${PLIST_NAME}.plist not found at ${PLIST_SOURCE}"
       exit 1
   fi
   ```

#### D. Add Crashlytics dSYM Upload Script

**CRITICAL: Required for readable crash reports**

1. Select **iosApp** target
2. Go to **Build Phases** tab
3. Click **+** → **New Run Script Phase**
4. Drag it AFTER "Compile Sources"
5. Name: `Upload Crashlytics dSYMs`
6. Script:
   ```bash
   "${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run"
   ```
7. Add Input Files (click **+** five times):
   ```
   ${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}
   ${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}/Contents/Resources/DWARF/${PRODUCT_NAME}
   ${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}/Contents/Info.plist
   $(TARGET_BUILD_DIR)/$(UNLOCALIZED_RESOURCES_FOLDER_PATH)/GoogleService-Info.plist
   $(TARGET_BUILD_DIR)/$(EXECUTABLE_PATH)
   ```

### Step 3: Add Firebase Initialization

Initialization code is in `iosApp/iosApp/iOSApp.swift`:
```swift
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

### Step 4: Add GoogleService-Info.plist Files

- ✅ Debug: `iosApp/iosApp/GoogleService-Info-Debug.plist`
- ✅ Release: `iosApp/iosApp/GoogleService-Info.plist`

### Step 5: Build and Test

#### Debug Build
- Bundle ID: `com.gma.tsunjo.school.debug`
- App Name: "Tsun Jo (Debug)"
- Firebase Project: gma-tsunjo-stage

#### Release Build
- Bundle ID: `com.gma.tsunjo.school`
- App Name: "Tsun Jo"
- Firebase Project: gma-tsunjo (production)

---

## Usage Reference

### Analytics

```swift
// Log event
FirebaseBridge.shared.logEvent("screen_view", parameters: [
    "screen_name": "LoginScreen",
    "screen_class": "LoginViewController"
])

// Set user ID
FirebaseBridge.shared.setUserId("user_12345")

// Set user property
FirebaseBridge.shared.setUserProperty("user_type", value: "student")
```

**Console:** Firebase Console → Analytics → Events (1 hour delay)

### Crashlytics

```swift
// Record non-fatal exception
FirebaseBridge.shared.recordException("API timeout: /auth/login")

// Add custom log (appears in crash reports)
FirebaseBridge.shared.log("User clicked login button")

// Add custom keys (appears in crash reports)
FirebaseBridge.shared.setCustomKey("last_action", value: "login_attempt")
FirebaseBridge.shared.setCustomKeyInt("retry_count", value: 3)
FirebaseBridge.shared.setCustomKeyBool("is_premium", value: true)

// Test crash
FirebaseBridge.shared.testCrash()
```

**Console:** Firebase Console → Crashlytics (5 min delay after relaunch)

### Remote Config

```swift
// Fetch and activate
FirebaseBridge.shared.fetchAndActivate { success in
    if success {
        let stringValue = FirebaseBridge.shared.getString("feature_flag")
        let boolValue = FirebaseBridge.shared.getBoolean("enable_feature")
        let intValue = FirebaseBridge.shared.getLong("max_retries")
        let doubleValue = FirebaseBridge.shared.getDouble("timeout_seconds")
    }
}
```

**Console:** Firebase Console → Remote Config

---

## Verification

### Build Verification
1. Clean: **Product → Clean Build Folder** (Cmd+Shift+K)
2. Build: **Product → Build** (Cmd+B)
3. Check build logs for: `✅ Successfully uploaded symbols`

### Analytics Verification
1. Run app and trigger events
2. Wait up to 1 hour
3. Check: Firebase Console → Analytics → Events

### Crashlytics Verification
1. Run app
2. Trigger test crash: `FirebaseBridge.shared.testCrash()`
3. Relaunch app (crashes sent on next launch)
4. Wait 5 minutes
5. Check: Firebase Console → Crashlytics
6. Verify stack trace shows function names (not memory addresses)

### Checklist
- [ ] dSYM generation enabled
- [ ] Upload script added with Input Files
- [ ] Build shows "Successfully uploaded symbols"
- [ ] Test crash appears in Console
- [ ] Stack trace is readable (not `0x00000001234567`)
- [ ] Analytics events appear
- [ ] Non-fatal exceptions logged

---

## Troubleshooting

### "Missing dSYM" in Firebase Console

**Cause:** dSYM upload script not configured or not running

**Solution:**
1. Verify dSYM generation: Build Settings → Debug Information Format = "DWARF with dSYM File"
2. Check upload script exists in Build Phases
3. Verify Input Files are added to script
4. Clean and rebuild
5. Check build logs for "Successfully uploaded symbols"

**Manual upload (if automatic fails):**
```bash
# Find dSYM location
find ~/Library/Developer/Xcode/DerivedData -name "*.dSYM"

# Upload manually
/path/to/firebase-ios-sdk/Crashlytics/upload-symbols \
  -gsp /path/to/GoogleService-Info.plist \
  -p ios \
  /path/to/YourApp.dSYM
```

### Crashes Show Memory Addresses

**Cause:** dSYMs not uploaded or not generated

**Solution:**
1. Enable dSYM generation (see Step 2B)
2. Add upload script (see Step 2D)
3. Clean and rebuild
4. Trigger new crash to test

### "run: No such file or directory" Error

**Cause:** Firebase SDK path incorrect

**Solution:**
Use alternative script:
```bash
# Find and run Crashlytics upload script
CRASHLYTICS_SCRIPT=$(find "${BUILD_DIR%/Build/*}/SourcePackages/checkouts" -name "run" -path "*/Crashlytics/run" | head -n 1)
if [ -f "$CRASHLYTICS_SCRIPT" ]; then
    "$CRASHLYTICS_SCRIPT"
else
    echo "warning: Crashlytics upload script not found"
fi
```

### Analytics Events Not Appearing

**Possible causes:**
- Up to 1 hour delay for first events
- Wrong GoogleService-Info.plist
- Analytics not enabled in Firebase Console

**Solution:**
1. Wait up to 1 hour
2. Verify correct plist file copied to app bundle
3. Check: Firebase Console → Analytics → Settings

### Build Script Not Running

**Solution:**
1. Verify script is in Build Phases
2. Check script is AFTER "Compile Sources"
3. Ensure script has no syntax errors
4. Clean build folder and rebuild

---

## Important Notes

- Both debug and release can be installed on same device
- Debug builds → Stage Firebase project
- Release builds → Production Firebase project
- Build script auto-selects correct GoogleService-Info.plist
- dSYMs uploaded automatically on every build
- Crashes sent on next app launch (not immediately)
- All Firebase logs use `<< ` prefix for easy searching

---

## Resources

- [Firebase iOS Documentation](https://firebase.google.com/docs/ios/setup)
- [Crashlytics Get Started](https://firebase.google.com/docs/crashlytics/get-started?platform=ios)
- [Get Readable Crash Reports](https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=ios)
- [Firebase Console - Stage](https://console.firebase.google.com/project/gma-tsunjo-stage)
