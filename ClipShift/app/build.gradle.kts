plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.clipshift"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.clipshift"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // IMPORTANT: Your FFmpeg file is only for ARM64.
        // This prevents crashes on incorrect devices/emulators.
        ndk {
            abiFilters.add("arm64-v8a")
        }
    }

    // Explicitly include the jniLibs folder
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    // --- PROTECTION FOR YOUR FILE ---
    packaging {
        jniLibs {
            // 1. Forces Android to physically unpack the file (necessary for execution)
            useLegacyPackaging = true

            // 2. MOST IMPORTANT LINE: Prevents Gradle from "downscaling" the file
            // Without this, your 150MB file will be broken after the build!
            keepDebugSymbols.add("**/libffmpeg.so")

            // Conflict resolution
            pickFirst("lib/*/libpython.zip.so")
            pickFirst("lib/*/libffmpeg.so")
            pickFirst("**/*.so")
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // The base library (without FFmpeg, since we have it manually)
    implementation("io.github.junkfood02.youtubedl-android:library:0.17.1")

    // Standard Android & Compose Dependencies
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.uiautomator)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}