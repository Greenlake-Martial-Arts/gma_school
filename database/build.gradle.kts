plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.45.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.45.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.45.0")
    
    // MySQL driver
    implementation("mysql:mysql-connector-java:8.0.33")
    
    // Connection pooling
    implementation("com.zaxxer:HikariCP:5.0.1")
    
    // Shared models
    implementation(projects.shared)
    
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.20")
}
