import tornaco.project.android.shortx.Others
import tornaco.project.android.shortx.Compose
import tornaco.project.android.shortx.Configs
import tornaco.project.android.shortx.Configs.resPrefix
import tornaco.project.android.shortx.Hilt
import tornaco.project.android.shortx.log

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "${Configs.sxPackageName}.ui"

    defaultConfig {
        minSdk = Configs.minSdkVersion
        compileSdk = Configs.compileSdkVersion
        targetSdk = Configs.targetSdkVersion
        testInstrumentationRunner = Configs.testInstrumentationRunner

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        val noMinify = project.findProperty("no-minify")?.toString().toBoolean()
        log("noMinify: $noMinify")

        getByName("release") {
            isMinifyEnabled = !noMinify
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isMinifyEnabled = !noMinify
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
        viewBinding = false
        dataBinding = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Compose.composeVersion
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    resourcePrefix = resPrefix
}

dependencies {
    implementation(Hilt.library)
    implementation(Hilt.hiltNavigationCompose)
    kapt(Hilt.googleAndroidCompiler)

    implementation(Compose.enro)
    kapt(Compose.enroApt)

    implementation(Others.landscapistGlide)
    implementation(Others.glide)
    kapt(Others.glideCompiler)
    annotationProcessor(Others.glideCompiler)

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json-jvm
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.4.1")

    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.8.0-beta01")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.ui:ui:1.3.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.3.2")
    implementation("androidx.compose.material3:material3:1.1.0-alpha03")
    implementation("androidx.compose.material:material:1.3.1")
    implementation(Compose.composeMaterialIconsExtended)

    implementation(project(":binder-retriever"))
    implementation(project(":core"))
    implementation(project(":external:shell"))
    implementation(project(":external:dateformatter"))
    implementation(project(":external:utils"))

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.3.2")

    debugImplementation("androidx.compose.ui:ui-tooling:1.3.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.3.2")
}