import Foundation
import FirebaseAnalytics
import FirebaseCrashlytics
import FirebaseRemoteConfig

@objc public class FirebaseBridge: NSObject {
    
    @objc public static let shared = FirebaseBridge()
    
    private override init() {
        super.init()
    }
    
    // MARK: - Analytics
    
    @objc public func logEvent(_ name: String, parameters: [String: Any]?) {
        Analytics.logEvent(name, parameters: parameters)
        print("<< Firebase Analytics: \(name)")
    }
    
    @objc public func setUserId(_ userId: String?) {
        Analytics.setUserID(userId)
        Crashlytics.crashlytics().setUserID(userId ?? "")
        print("<< Firebase User ID set: \(userId ?? "nil")")
    }
    
    @objc public func setUserProperty(_ name: String, value: String?) {
        Analytics.setUserProperty(value, forName: name)
        print("<< Firebase User Property: \(name) = \(value ?? "nil")")
    }
    
    // MARK: - Crashlytics
    
    @objc public func recordException(_ message: String) {
        let error = NSError(
            domain: "KotlinException",
            code: 0,
            userInfo: [NSLocalizedDescriptionKey: message]
        )
        Crashlytics.crashlytics().record(error: error)
        print("<< Firebase Non-fatal: \(message)")
    }
    
    @objc public func log(_ message: String) {
        Crashlytics.crashlytics().log(message)
        print("<< Firebase Log: \(message)")
    }
    
    @objc public func setCustomKey(_ key: String, value: String) {
        Crashlytics.crashlytics().setCustomValue(value, forKey: key)
    }
    
    @objc public func setCustomKeyInt(_ key: String, value: Int) {
        Crashlytics.crashlytics().setCustomValue(value, forKey: key)
    }
    
    @objc public func setCustomKeyBool(_ key: String, value: Bool) {
        Crashlytics.crashlytics().setCustomValue(value, forKey: key)
    }
    
    @objc public func testCrash() {
        fatalError("Test crash from Settings")
    }
    
    // MARK: - Remote Config
    
    @objc public func getString(_ key: String) -> String {
        return RemoteConfig.remoteConfig().configValue(forKey: key).stringValue ?? ""
    }
    
    @objc public func getBoolean(_ key: String) -> Bool {
        return RemoteConfig.remoteConfig().configValue(forKey: key).boolValue
    }
    
    @objc public func getLong(_ key: String) -> Int64 {
        return RemoteConfig.remoteConfig().configValue(forKey: key).numberValue.int64Value
    }
    
    @objc public func getDouble(_ key: String) -> Double {
        return RemoteConfig.remoteConfig().configValue(forKey: key).numberValue.doubleValue
    }
    
    @objc public func fetchAndActivate(_ completion: @escaping (Bool) -> Void) {
        RemoteConfig.remoteConfig().fetchAndActivate { status, error in
            if let error = error {
                print("<< Firebase Remote Config fetch failed: \(error.localizedDescription)")
                completion(false)
            } else {
                print("<< Firebase Remote Config fetched successfully")
                completion(true)
            }
        }
    }
}
