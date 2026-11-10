import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
}

// 스크립트 프로퍼티는 선언 순서대로 초기화되므로, 이를 사용하는 android 블록보다 위에 둔다.
val localProperties: Properties by lazy {
  Properties().apply {
    providers.fileContents(rootProject.layout.projectDirectory.file("local.properties"))
      .asText.orNull?.let { load(it.reader()) }
  }
}

fun readProperty(key: String): String =
  localProperties.getProperty(key) ?: error("local.properties 에 $key 가 없습니다")

android {
  namespace = "kr.susemi99.seoulwomen"
  compileSdk {
    version = release(37)
  }

  defaultConfig {
    applicationId = "kr.susemi99.seoulwomen"
    minSdk = 26
    targetSdk = 37
    versionCode = 18
    versionName = "18"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    buildConfigField("String", "API_KEY", readProperty("API_KEY"))
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    // R8이 적용된 release와 같은 빌드를 debug 키로 서명해 기기에서 확인하기 위한 빌드 타입
    create("releaseCheck") {
      initWith(getByName("release"))
      applicationIdSuffix = ".check"
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks += "release"
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons.core)

  testImplementation(libs.junit)
  testImplementation(libs.androidx.paging.testing)
  androidTestImplementation(libs.androidx.junit)
  // ui-test-junit4가 가져오는 3.5.0은 API 37에서 InputManager.getInstance 오류로 실패하므로 버전을 올려 둔다.
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.test.manifest)

  // api
  implementation(libs.retrofit)
  implementation(libs.retrofit.json.converter)
  implementation(libs.retrofit.logging.interceptor)

  // json
  implementation(libs.kotlinx.serialization.json)

  // pagination
  implementation(libs.androidx.paging.compose)

  // hilt
  implementation(libs.hilt.android)
  ksp(libs.hilt.android.compiler)
  implementation(libs.hilt.lifecycle.viewmodel.compose)

  // preference
  implementation(libs.kotpref)
}
