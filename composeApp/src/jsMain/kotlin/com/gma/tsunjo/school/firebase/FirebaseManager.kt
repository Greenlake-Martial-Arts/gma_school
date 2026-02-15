package com.gma.tsunjo.school.firebase

actual object FirebaseManager {
    actual fun initialize() {}
    actual fun logEvent(name: String, params: Map<String, Any>) {}
    actual fun setUserId(userId: String?) {}
    actual fun setUserProperty(name: String, value: String) {}
    actual fun recordException(throwable: Throwable) {}
    actual fun testCrash() {
        error("Test crash from FirebaseManager Web JS")
    }
    actual fun getString(key: String): String = ""
    actual fun getBoolean(key: String): Boolean = false
    actual fun getLong(key: String): Long = 0L
    actual fun getDouble(key: String): Double = 0.0
    actual suspend fun fetchAndActivate(): Boolean = false
}
