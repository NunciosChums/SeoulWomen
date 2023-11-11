plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("kotlinx-serialization")
  id("kotlin-kapt")
  id("dagger.hilt.android.plugin")
}

android {
  namespace = "kr.susemi99.seoulwomen"
  compileSdk = 34

  defaultConfig {
    applicationId = "kr.susemi99.seoulwomen"
    minSdk = 23
    targetSdk = 34
    versionCode = 12
    versionName = "4"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.4" // https://developer.android.com/jetpack/androidx/releases/compose-compiler
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  val lifecycleVersion = "2.6.2"

  val composeBom = platform("androidx.compose:compose-bom:2023.10.01") // https://developer.android.com/jetpack/compose/setup#bom-version-mapping
  implementation(composeBom)
  androidTestImplementation(composeBom)
  debugImplementation(composeBom)

  implementation("androidx.compose.foundation:foundation")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
  implementation("androidx.activity:activity-compose:1.8.0")

  testImplementation("junit:junit:4.13.2")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

  // api
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
  implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.9")

  // json
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

  // logcat
  implementation("com.jakewharton.timber:timber:5.0.1")

  // view model
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")

  // pagination
  implementation("androidx.paging:paging-compose:3.2.1")

  // hilt
  implementation("com.google.dagger:hilt-android:2.48.1")
  kapt("com.google.dagger:hilt-android-compiler:2.48.1")

  // preference
  implementation("com.chibatching.kotpref:kotpref:2.13.2")
  implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
}