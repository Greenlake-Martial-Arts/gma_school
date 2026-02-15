package com.gma.tsunjo.school.firebase

expect object FirebaseManager {
    fun initialize()
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
    fun setUserId(userId: String?)
    fun setUserProperty(name: String, value: String)
    fun recordException(throwable: Throwable)
    fun testCrash()
    fun getString(key: String): String
    fun getBoolean(key: String): Boolean
    fun getLong(key: String): Long
    fun getDouble(key: String): Double
    suspend fun fetchAndActivate(): Boolean
}
