plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.englishlearningsoftware"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.englishlearningsoftware"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.transition)
    implementation(libs.material)
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")
    implementation("com.google.mlkit:image-labeling:17.0.9")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation("androidx.test:core:1.5.0")
    // OkHttp supports Java callbacks without coroutines or Retrofit.
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
}
