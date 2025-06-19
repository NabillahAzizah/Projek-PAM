plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.9.0"
    alias(libs.plugins.google.gms.google.services)
    ("apply plugin: com.google.gms.google-services")

}

android {
    namespace = "com.example.bookproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bookproject"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // AndroidX
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Material Design
    implementation("com.google.android.material:material:1.6.0")

    // Google Play Services Auth
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.monitor)
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)

    // Firebase Realtime Database
    implementation ("com.google.firebase:firebase-database:21.0.0")

    testImplementation(libs.junit.junit)
}
