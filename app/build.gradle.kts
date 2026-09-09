import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.iranbinaryoption.trdsig"
    minSdk = 24
    targetSdk = 36
    versionCode = 3
    versionName = "1.2.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      // Keystore is NEVER committed to the repository. Release signing material is
      // injected via environment variables / CI secrets (KEYSTORE_PATH, STORE_PASSWORD,
      // KEY_ALIAS, KEY_PASSWORD) — see .github/workflows/release-apk.yml and
      // docs/RELEASE_SIGNING_SETUP.md. The stable-signature guarantee comes from CI
      // reusing the SAME secret-backed keystore in every run, not from a tracked binary
      // in git. Values below are configuration placeholders ONLY; hard validation for
      // real release packaging happens in gradle.taskGraph.whenReady (fail loudly).
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/release.keystore"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD") ?: "android"
      keyAlias = System.getenv("KEY_ALIAS") ?: "release"
      keyPassword = System.getenv("KEY_PASSWORD") ?: "android"
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = true
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
      buildConfigField("String", "BACKEND_BASE_URL", "\"https://iranbinaryoption.com/\"")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
      buildConfigField("String", "BACKEND_BASE_URL", "\"https://iranbinaryoption.com/\"")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// HARD RELEASE SIGNING VALIDATION (problems2 #1 / problems3 #3):
// fail loudly BEFORE any release packaging task runs when signing material is
// missing, instead of silently producing an unsigned/broken APK. Never triggers
// for debug builds or unit-test tasks.
gradle.taskGraph.whenReady {
  val isReleaseAssemble = allTasks.any {
    it.name.startsWith("assembleRelease") || it.name.startsWith("bundleRelease")
  }
  if (isReleaseAssemble) {
    val missing = buildList {
      if (System.getenv("STORE_PASSWORD").isNullOrBlank()) add("STORE_PASSWORD")
      if (System.getenv("KEY_ALIAS").isNullOrBlank()) add("KEY_ALIAS")
      if (System.getenv("KEY_PASSWORD").isNullOrBlank()) add("KEY_PASSWORD")
      val ks = file(System.getenv("KEYSTORE_PATH") ?: "${rootDir}/release.keystore")
      if (!ks.exists() || ks.length() == 0L) add("KEYSTORE_PATH (valid keystore file at $ks)")
    }
    if (missing.isNotEmpty()) {
      throw GradleException(
        "RELEASE SIGNING FAILED: missing signing material -> ${missing.joinToString()}. " +
          "Release credentials must come from CI secrets or local env — " +
          "see docs/RELEASE_SIGNING_SETUP.md (the keystore is never committed)."
      )
    }
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.lottie.compose)
  implementation(libs.firebase.ai)
  // Firestore dependency:
  implementation(libs.firebase.firestore)
  implementation(libs.firebase.messaging)

  // Firebase Auth and Google Sign-In via Credential Manager:
  implementation(libs.firebase.auth)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services)
  implementation(libs.googleid)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.koin.android)
  implementation(libs.koin.androidx.compose)
  implementation(libs.bcrypt)
  implementation(libs.okhttp)
  implementation(libs.converter.moshi)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
