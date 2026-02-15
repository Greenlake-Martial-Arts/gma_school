// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.exceptions

import com.gma.tsunjo.school.firebase.FirebaseManager

/**
 * Logs error to Firebase Crashlytics as non-fatal exception
 */
fun Throwable.logToFirebase() {
    FirebaseManager.recordException(this)
}
