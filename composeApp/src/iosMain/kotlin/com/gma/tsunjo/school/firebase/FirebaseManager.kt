package com.gma.tsunjo.school.firebase

actual object FirebaseManager {
    actual fun initialize() {
        // Firebase is initialized in Swift (iOSApp.swift)
    }
    
    actual fun logEvent(name: String, params: Map<String, Any>) {
        // TODO: Call FirebaseBridge.shared.logEvent(name, params)
        // Requires proper Kotlin/Native <-> Swift interop setup
    }
    
    actual fun setUserId(userId: String?) {
        // TODO: Call FirebaseBridge.shared.setUserId(userId)
    }
    
    actual fun setUserProperty(name: String, value: String) {
        // TODO: Call FirebaseBridge.shared.setUserProperty(name, value)
    }
    
    actual fun recordException(throwable: Throwable) {
        // TODO: Call FirebaseBridge.shared.recordException(message)
        // For now, just print to console
        val message = "${throwable::class.simpleName}: ${throwable.message}"
        println("iOS Crashlytics: $message")
    }
    
    actual fun testCrash() {
        // TODO: Call FirebaseBridge.shared.testCrash()
        // For now, throw Kotlin exception
        error("Test crash from FirebaseManager iOS")
    }
    
    actual fun getString(key: String): String = ""
    actual fun getBoolean(key: String): Boolean = false
    actual fun getLong(key: String): Long = 0L
    actual fun getDouble(key: String): Double = 0.0
    actual suspend fun fetchAndActivate(): Boolean = false
}

/*
 * TODO: Implement proper Kotlin/Native <-> Swift bridge
 * 
 * Option 1: Use @ObjCName and expect/actual declarations
 * Option 2: Use cinterop with Objective-C wrapper
 * Option 3: Use Kotlin/Native's experimental Swift interop
 * 
 * The Swift FirebaseBridge class is already implemented in:
 * iosApp/iosApp/FirebaseBridge.swift
 * 
 * It provides:
 * - FirebaseBridge.shared.logEvent(name, parameters)
 * - FirebaseBridge.shared.setUserId(userId)
 * - FirebaseBridge.shared.recordException(message)
 * - etc.
 */
