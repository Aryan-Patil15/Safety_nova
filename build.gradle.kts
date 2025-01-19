// This is the Project-Level build.gradle file

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        //noinspection GradlePluginVersion
        classpath ("com.android.tools.build:gradle:<your-gradle-version>")// Keep your existing Gradle version
        classpath ("com.google.gms:google-services:4.4.2")
        classpath ("com.google.gms:google-services:4.3.15")// Add this line for Firebase
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}
