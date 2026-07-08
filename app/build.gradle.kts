import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

// ---------------------------------------------------------------------------
// Read local.properties (never committed) for the football-data.org API token
// and base URL. Values are exposed through BuildConfig, never hardcoded.
// ---------------------------------------------------------------------------
val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        FileInputStream(localFile).use { load(it) }
    }
}

fun readConfig(key: String, default: String): String {
    // Priority: environment variable (useful for CI) -> local.properties -> default
    return System.getenv(key)
        ?: localProperties.getProperty(key)
        ?: default
}

val footballApiToken: String = readConfig("FOOTBALL_DATA_API_TOKEN", "")
val footballApiBaseUrl: String =
    readConfig("FOOTBALL_API_BASE_URL", "https://api.football-data.org/v4")

// ---------------------------------------------------------------------------
// Release signing. Credentials come only from environment variables
// (GitHub Secrets in CI) or local.properties for local release builds.
// Nothing sensitive is committed to the repository.
// ---------------------------------------------------------------------------
val keystorePath: String? = System.getenv("ANDROID_KEYSTORE_PATH")
    ?: localProperties.getProperty("ANDROID_KEYSTORE_PATH")
val keystorePassword: String? = System.getenv("ANDROID_KEYSTORE_PASSWORD")
    ?: localProperties.getProperty("ANDROID_KEYSTORE_PASSWORD")
val keyAlias: String? = System.getenv("ANDROID_KEY_ALIAS")
    ?: localProperties.getProperty("ANDROID_KEY_ALIAS")
val keyPassword: String? = System.getenv("ANDROID_KEY_PASSWORD")
    ?: localProperties.getProperty("ANDROID_KEY_PASSWORD")

val hasReleaseSigning: Boolean =
    !keystorePath.isNullOrBlank() &&
        file(keystorePath!!).exists() &&
        !keystorePassword.isNullOrBlank() &&
        !keyAlias.isNullOrBlank() &&
        !keyPassword.isNullOrBlank()

android {
    namespace = "com.kickplan.training"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kickplan.training"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        // Expose API configuration through BuildConfig. The token is escaped
        // as a string literal and is NEVER printed to logs by the app.
        buildConfigField("String", "FOOTBALL_DATA_API_TOKEN", "\"${footballApiToken}\"")
        buildConfigField("String", "FOOTBALL_API_BASE_URL", "\"${footballApiBaseUrl}\"")

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        // Only create a real release signing config when all credentials exist.
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(keystorePath!!)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
                // PKCS12 keystore support (recommended by Google).
                storeType = "PKCS12"
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            // R8 / resource shrinking enabled. Verify a non-minified release
            // first (see README), then keep these enabled for Play uploads.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = if (hasReleaseSigning) {
                signingConfigs.getByName("release")
            } else {
                // Fall back to debug signing ONLY for local developer builds
                // when no release keystore is configured. CI always provides
                // the release keystore via GitHub Secrets.
                signingConfigs.getByName("debug")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.03")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core & lifecycle
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")

    // Compose UI + Material 3
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.1")

    // Splash screen (backwards-compatible custom splash)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // DataStore Preferences (local JSON storage)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Networking (football-data.org API only)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Official Retrofit kotlinx.serialization converter (package retrofit2.converter.kotlinx.serialization)
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Logging interceptor is used in DEBUG builds only.
    debugImplementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Debug tooling
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
