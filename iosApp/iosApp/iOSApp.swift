import SwiftUI
import FirebaseCore
import FirebaseCrashlytics

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        
        // Enable Crashlytics collection
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(true)
        
        // Log custom event to verify Crashlytics is working
        Crashlytics.crashlytics().log("App launched - Crashlytics initialized")
        Crashlytics.crashlytics().setCustomValue("simulator", forKey: "device_type")
        
        #if DEBUG
        print("<< Firebase configured for Debug")
        print("<< Crashlytics enabled")
        print("<< Crashlytics data collection: \(Crashlytics.crashlytics().isCrashlyticsCollectionEnabled())")
        #endif
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

// Test function to trigger crash
func triggerTestCrash() {
    fatalError("Test crash from iOS")
}
