plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.kabadiwalaconnect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kabadiwalaconnect"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources.excludes += "META-INF/*"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    val material3 = "androidx.compose.material3:material3"
    val materialIcons = "androidx.compose.material:material-icons-extended"
    val activityCompose = "androidx.activity:activity-compose:1.9.0"
    val navigationCompose = "androidx.navigation:navigation-compose:2.7.7"
    val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
    val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-compose:2.7.0"
    val room = "androidx.room:room-runtime:2.6.1"
    val roomCompiler = "androidx.room:room-compiler:2.6.1"
    val roomKtx = "androidx.room:room-ktx:2.6.1"
    val kotlinxCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
    val kotlinxSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0"
    val coil = "io.coil-kt:coil-compose:2.5.0"
    val accompanist = "com.google.accompanist:accompanist-permissions:0.32.0"
    val coreKtx = "androidx.core:core-ktx:1.12.0"
    val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.1.0"
    val gson = "com.google.code.gson:gson:2.10.1"

    implementation(composeBom)
    implementation(material3)
    implementation(materialIcons)
    implementation(activityCompose)
    implementation(navigationCompose)
    implementation(lifecycleViewModel)
    implementation(lifecycleRuntime)
    implementation(room)
    implementation(roomKtx)
    kapt(roomCompiler)
    implementation(kotlinxCoroutines)
    implementation(kotlinxSerialization)
    implementation(coil)
    implementation(accompanist)
    implementation(coreKtx)
    implementation(constraintLayout)
    implementation(gson)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.5")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.5")
    debugImplementation("androidx.compose.ui:ui-tooling-preview:1.6.5")
}