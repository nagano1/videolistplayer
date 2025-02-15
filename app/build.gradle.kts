@file:Suppress("UnstableApiUsage")
import kotlin.math.sqrt

plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)

    alias(libs.plugins.com.google.devtools.ksp)

}


android {
    signingConfigs {
        getByName("debug") {
            val bytes = byteArrayOf(
                sqrt(11025.0).toInt().toByte(),
                109, sqrt(9409.0).toInt().toByte(), 105, sqrt(13456.0).toInt().toByte(), 105, 100,
                sqrt(sqrt(151807041.0)).toInt().toByte()
            )

            storeFile = file("C:\\GitProjects\\canlang_android\\CanLang\\release.jks")
            storePassword = String(bytes)
            keyAlias = "key0"
            keyPassword = String(bytes)
        }
    }

    namespace = "org.rokist.videolistplayer"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }

    defaultConfig {
        applicationId = "org.rokist.videolistplayer"

        minSdk = 26
        //23 	Android 6.0 	Marshmallow 2015/10/5
        //24 	Android 7.0 	Nougat
        //25 	Android 7.1 – 7.1.1 Nougat
        //26 	Android 8.0 	Oreo
        //27 	8.1 Oreo
        //28 	9 	Pie
        //29    10
        //30 	11

        targetSdk = compileSdk
        versionCode = 1
        versionName = "1.0"
        externalNativeBuild {
            cmake {
                // cppFlags "-fopenmp"
            }
        }
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            //abiFilters.add("arm64-v8a")
            //abiFilters.add("x86_64")
        }

        signingConfig = signingConfigs.getByName("debug")
    }

    buildFeatures {
        //dataBinding = true
        viewBinding = true
        //compose = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    composeOptions {
        //kotlinCompilerExtensionVersion = "1.4.3"
    }

    buildTypes {
        debug {
            resValue("string", "deggvalue", "AJOPFVOIOFIWE_")
//            isMinifyEnabled = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
        }
        release {
            resValue("string", "deggvalue", "1")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// ./j2objc  -classpath jgit3.7.jar -sourcepath jgit/jgit-stable-3.7/org.eclipse.jgit/ `find jgit/jgit-stable-3.7/org.eclipse.jgit/src/org/eclipse/jgit/diff  -name '*.java'`


dependencies {


    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    /*
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
*/

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    testImplementation (libs.junit)

    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    implementation(libs.androidx.work.runtime) // (Java only)
    implementation(libs.androidx.work.runtime.ktx) // Kotlin + coroutines


    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)


    //implementation "androidx.work:work-runtime:$work_version"


    implementation(libs.fuel.json)
    implementation(libs.fuel)


    implementation(libs.androidx.appcompat)
    // https://mvnrepository.com/artifact/androidx.core/core-ktx?repo=google
    implementation(libs.androidx.constraintlayout)
    //implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation(libs.androidx.documentfile)
    //implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    //implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")


    // https://bigbadaboom.github.io/androidsvg/
    //implementation(libs.androidsvg.aar)

/*
    if (hiltVersion != localHiltVersion) {
        throw Exception("different hilt version")
    }
*/
}

// Allow references to generated code
/*
kapt {
    correctErrorTypes = true
}
*/