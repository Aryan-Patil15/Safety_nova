plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.safetynova"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.safetynova"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX and Material components
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Google Play Services for maps and location
    implementation(libs.play.services.maps)
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Firebase dependencies
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation("com.google.firebase:firebase-auth:21.3.0")
    implementation("com.google.firebase:firebase-database:20.3.3")
    implementation("com.google.firebase:firebase-core:21.1.1")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Additional debugging tools
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
}
