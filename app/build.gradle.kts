plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    alias(libs.plugins.ktx.serialization)
}

android {
    namespace = "com.wargon.onlypassword"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wargon.onlypassword"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true  //启用代码混淆
            isShrinkResources = true    //移除无用资源
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    //自适应
    implementation("androidx.compose.material3:material3-window-size-class")
    //icon extend
    implementation(libs.material.icons.extended)
    //room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    //view model compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    //navigation compose
    implementation(libs.androidx.navigation.compose)
    //tink 加密
    implementation(libs.tink) {
        exclude(group = "com.google.api-client", module = "google-api-client")
        exclude(group = "com.google.http-client", module = "google-http-client")
        exclude(group = "joda-time", module = "joda-time")
    }
    //serialization
    implementation(libs.ktx.serialization)
    //biometric 生物认证
//    implementation(libs.biometric)

    compileOnly("com.google.errorprone:error_prone_annotations:2.30.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    // 使用 AndroidX 的 Nullable 注解（推荐）
    compileOnly("androidx.annotation:annotation:1.9.1")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
