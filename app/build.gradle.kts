plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // 👈 NECESARIO PARA ROOM
}

android {
    namespace = "com.example.travelgo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.travelgo"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    // 🔧 Forzamos la versión 1.10.1 de androidx.activity
    configurations.all {
        resolutionStrategy {
            force("androidx.activity:activity:1.10.1")
            eachDependency {
                if (requested.group == "com.intellij" && requested.name == "annotations") {
                    useTarget("org.jetbrains:annotations:23.0.0")
                }
            }
        }
    }

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // ✅ Room: Runtime + KTX + kapt
    implementation(libs.androidx.room.runtime.android)
    implementation("androidx.room:room-ktx:2.6.1")
    kapt(libs.androidx.room.compiler)

    // ✅ Google Maps y Location Services
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.material:material:1.9.0")

    // ✅ OpenStreetMap
    implementation("org.osmdroid:osmdroid-android:6.1.16")

    // ✅ Glide (para cargar imágenes sin pedir permisos)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}



