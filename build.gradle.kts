// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.com.google.devtools.ksp) apply false

//    id("dagger.hilt.android.plugin")

}
//plugins {
//    id("com.android.application")
//    id("kotlin-android")
//
//    id("kotlin-kapt")
//    id("dagger.hilt.android.plugin")
//
//    id("com.google.devtools.ksp")
//}

/*

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    val hiltVersion by extra("2.47")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {

        // https://developer.android.com/studio/releases/gradle-plugin

        classpath ("com.android.tools.build:gradle:7.4.2")

        // this is not going to get auto-update
        // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-gradle-plugin
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")

        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

}
*/

/*
tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
*/
true // Needed to ma
