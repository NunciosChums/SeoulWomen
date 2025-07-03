import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.hilt)
  alias(libs.plugins.kotlin.kapt)
}

android {
  namespace = "kr.susemi99.seoulwomen"
  compileSdk = 36

  defaultConfig {
    applicationId = "kr.susemi99.seoulwomen"
    minSdk = 26
    targetSdk = 36
    versionCode = 13
    versionName = "5"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    buildConfigField("String", "API_KEY", readProperty("API_KEY"))
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlin {
    jvmToolchain(17)
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
}

fun readProperty(key: String): String = gradleLocalProperties(rootDir, providers).getProperty(key)

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

  // api
  implementation(libs.retrofit)
  implementation(libs.retrofit.json.converter)
  implementation(libs.retrofit.logging.interceptor)

  // json
  implementation(libs.kotlinx.serialization.json)

  // logcat
  implementation(libs.timber)

  // view model
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // pagination
  implementation(libs.androidx.paging.compose)

  // hilt
  implementation(libs.hilt.android)
  kapt(libs.hilt.android.compiler)

  // preference
  implementation(libs.kotpref)
  implementation(libs.androidx.security.crypto)
}