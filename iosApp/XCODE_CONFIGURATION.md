# Xcode Configuration for Firebase Crashlytics

## Overview

This guide shows the exact Xcode settings needed for Firebase Crashlytics to work properly on iOS.

## Part 1: Build Settings

### Location
**iosApp Target → Build Settings → All → Search: "debug information format"**

### Required Setting
```
Debug Information Format = DWARF with dSYM File
```

### For Both Configurations
- ✅ Debug: DWARF with dSYM File
- ✅ Release: DWARF with dSYM File

### Why This Matters
Without dSYM files, crash reports look like this:
```
0x0000000100abc123
0x0000000100def456
0x0000000100ghi789
```

With dSYM files, crash reports look like this:
```
LoginViewModel.login() line 45
AuthRepository.authenticate() line 123
NetworkClient.post() line 89
```

---

## Part 2: Build Phases - Crashlytics Upload Script

### Location
**iosApp Target → Build Phases → + → New Run Script Phase**

### Script Name
```
Upload Crashlytics dSYMs
```

### Script Position
Place AFTER "Compile Sources" but BEFORE "Copy Bundle Resources"

Order should be:
1. Dependencies
2. Compile Sources
3. **Upload Crashlytics dSYMs** ← Your new script
4. Copy Bundle Resources
5. Embed Frameworks

### Script Content
```bash
"${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run"
```

### Input Files (CRITICAL for Xcode 15+)
Click the arrow next to "Input Files" to expand, then add these 5 paths:

```
${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}
${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}/Contents/Resources/DWARF/${PRODUCT_NAME}
${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}/Contents/Info.plist
$(TARGET_BUILD_DIR)/$(UNLOCALIZED_RESOURCES_FOLDER_PATH)/GoogleService-Info.plist
$(TARGET_BUILD_DIR)/$(EXECUTABLE_PATH)
```

### Why Input Files Are Required

1. **Xcode 15+ Requirement**: Xcode needs to know which files the script will access
2. **User Script Sandboxing**: If enabled, scripts can only access declared files
3. **Build Optimization**: Xcode can determine when to re-run the script

Without Input Files, you'll see:
```
error: Info.plist Error Unable to process Info.plist at path.
```

---

## Part 3: Verify Configuration

### Build the Project
```
Product → Clean Build Folder (Cmd+Shift+K)
Product → Build (Cmd+B)
```

### Check Build Log
Look for this message in the build output:
```
✅ Successfully uploaded symbols for <your-app-bundle-id>
```

### If You See Errors

**Error: "run: No such file or directory"**

The Firebase SDK path is incorrect. Replace the script with:

```bash
# Find and run Crashlytics upload script
CRASHLYTICS_SCRIPT=$(find "${BUILD_DIR%/Build/*}/SourcePackages/checkouts" -name "run" -path "*/Crashlytics/run" | head -n 1)
if [ -f "$CRASHLYTICS_SCRIPT" ]; then
    "$CRASHLYTICS_SCRIPT"
else
    echo "warning: Crashlytics upload script not found"
fi
```

**Error: "Info.plist Error"**

Input Files are missing or incorrect. Double-check all 5 paths.

**Warning: "dSYM not found"**

Debug Information Format is not set to "DWARF with dSYM File". Check Part 1.

---

## Part 4: Test the Setup

### 1. Add Test Crash Code

Already available in `FirebaseBridge.swift`:
```swift
FirebaseBridge.shared.testCrash()
```

### 2. Trigger Crash
- Run app on device or simulator
- Navigate to settings or add a test button
- Call `FirebaseBridge.shared.testCrash()`
- App will crash immediately

### 3. Relaunch App
**IMPORTANT**: Crashes are sent on next launch, not immediately

### 4. Check Firebase Console
- Wait 5 minutes
- Go to: https://console.firebase.google.com/project/gma-tsunjo-stage/crashlytics
- You should see the crash with readable stack trace

### 5. Verify Stack Trace
The crash should show:
```
FirebaseBridge.testCrash() line 42
SettingsView.body line 123
...
```

NOT:
```
0x0000000100abc123
0x0000000100def456
...
```

---

## Part 5: Additional Configuration (Optional)

### Enable Crashlytics Debug Logging

Add to your scheme's environment variables:
1. Product → Scheme → Edit Scheme
2. Run → Arguments → Environment Variables
3. Add: `FIRDebugEnabled` = `YES`

This will show detailed Firebase logs in Xcode console.

### Disable Crashlytics for Debug Builds

If you only want Crashlytics in Release builds:

```bash
# Only upload dSYMs for Release builds
if [ "${CONFIGURATION}" == "Release" ]; then
    "${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run"
else
    echo "Skipping Crashlytics upload for Debug build"
fi
```

---

## Summary Checklist

Before considering setup complete:

- [ ] Debug Information Format = "DWARF with dSYM File" (Debug & Release)
- [ ] Crashlytics upload script added to Build Phases
- [ ] Script positioned AFTER "Compile Sources"
- [ ] All 5 Input Files added to script
- [ ] Build succeeds with "Successfully uploaded symbols" message
- [ ] Test crash appears in Firebase Console
- [ ] Stack trace shows function names (not memory addresses)

---

## Need Help?

- **Detailed steps**: See `CRASHLYTICS_SETUP_STEPS.md`
- **Quick reference**: See `FIREBASE_QUICK_START.md`
- **Full status**: See `FIREBASE_STATUS.md`
- **Firebase docs**: https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=ios
