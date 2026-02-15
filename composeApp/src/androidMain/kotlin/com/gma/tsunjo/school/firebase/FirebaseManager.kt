package com.gma.tsunjo.school.firebase

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.tasks.await

actual object FirebaseManager {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var crashlytics: FirebaseCrashlytics
    private lateinit var remoteConfig: FirebaseRemoteConfig

    actual fun initialize() {
        analytics = Firebase.analytics
        crashlytics = Firebase.crashlytics
        remoteConfig = Firebase.remoteConfig
    }

    actual fun logEvent(name: String, params: Map<String, Any>) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                }
            }
        }
        analytics.logEvent(name, bundle)
    }

    actual fun setUserId(userId: String?) {
        analytics.setUserId(userId)
        crashlytics.setUserId(userId ?: "")
    }

    actual fun setUserProperty(name: String, value: String) {
        analytics.setUserProperty(name, value)
    }

    actual fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    actual fun testCrash() {
        throw RuntimeException("Test crash from FirebaseManager")
    }

    actual fun getString(key: String): String {
        return remoteConfig.getString(key)
    }

    actual fun getBoolean(key: String): Boolean {
        return remoteConfig.getBoolean(key)
    }

    actual fun getLong(key: String): Long {
        return remoteConfig.getLong(key)
    }

    actual fun getDouble(key: String): Double {
        return remoteConfig.getDouble(key)
    }

    actual suspend fun fetchAndActivate(): Boolean {
        return remoteConfig.fetchAndActivate().await()
    }
}
