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

        // WICHTIG: Deine FFmpeg-Datei ist nur für ARM64.
        // Das verhindert Abstürze auf falschen Geräten/Emulatoren.
        ndk {
            abiFilters.add("arm64-v8a")
        }
    }

    // Explizites Einbinden des jniLibs Ordners
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

    // --- HIER IST DER SCHUTZ FÜR DEINE DATEI ---
    packaging {
        jniLibs {
            // 1. Zwingt Android, die Datei physisch zu entpacken (nötig für Ausführung)
            useLegacyPackaging = true

            // 2. WICHTIGSTE ZEILE: Verhindert, dass Gradle die Datei "kleinrechnet"
            // Ohne das hier ist deine 150MB Datei nach dem Build kaputt!
            keepDebugSymbols.add("**/libffmpeg.so")

            // Konfliktlösung
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
    // Die Basis-Bibliothek (ohne FFmpeg, da wir es manuell haben)
    implementation("io.github.junkfood02.youtubedl-android:library:0.17.1")

    // Standard Android & Compose Dependencies
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}