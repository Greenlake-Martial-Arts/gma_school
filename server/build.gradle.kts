plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "com.gma.tsunjo.school"
version = "1.0.0"
application {
    mainClass.set("com.gma.tsunjo.school.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)

    implementation(projects.shared)
    implementation(projects.database)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.serverCompression)
    implementation(libs.ktor.serializationKotlinxJson)
    implementation(libs.ktor.serverHostJvm)

    // Configuration
    implementation(libs.typesafe.config)

    // Koin for Ktor
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.mockk)
    testImplementation(libs.h2)
}