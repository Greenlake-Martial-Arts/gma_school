plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    // Exposed ORM
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    
    // MySQL driver
    implementation(libs.mysql.connector)
    
    // Connection pooling
    implementation(libs.hikaricp)
    
    // Shared models
    implementation(projects.shared)
    
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.h2)
    testImplementation(libs.mockk)
}
