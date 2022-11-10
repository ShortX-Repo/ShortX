import tornaco.project.android.shortx.Compose
import tornaco.project.android.shortx.Configs
import tornaco.project.android.shortx.Configs.keyStoreAlias
import tornaco.project.android.shortx.Configs.keyStorePassword
import tornaco.project.android.shortx.Hilt
import tornaco.project.android.shortx.log

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    defaultConfig {
        applicationId = Configs.sxPackageName
        vectorDrawables.useSupportLibrary = true
        versionName = Configs.sxVersionName
        versionCode = Configs.sxVersionCode

        minSdk = Configs.minSdkVersion
        compileSdk = Configs.compileSdkVersion
        targetSdk = Configs.targetSdkVersion
        testInstrumentationRunner = Configs.testInstrumentationRunner
        multiDexEnabled = true
    }

    signingConfigs {
        create("release") {
            Configs.KeyStorePath.also {
                storeFile = rootProject.file(it)
                storePassword = keyStorePassword()
                keyAlias = keyStoreAlias()
                keyPassword = keyStorePassword()
            }
        }
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
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            isMinifyEnabled = !noMinify
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
        viewBinding = false
        dataBinding = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Compose.composeVersion
    }

    packagingOptions {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }

    applicationVariants.all {
        outputs.all {
            val impl =
                this as com.android.build.gradle.internal.api.ApkVariantOutputImpl
            impl.outputFileName =
                "shortx-${Configs.sxVersionName}(${Configs.sxVersionCode}).apk"
            log("impl.outputFileName changed to:${impl.outputFileName}")
        }
    }
}

dependencies {
    implementation(Hilt.library)
    implementation(Hilt.hiltNavigationCompose)
    kapt(Hilt.googleAndroidCompiler)

    implementation(project(":ui"))
    implementation(project(":services"))
    implementation(project(":patch"))
    implementation(project(":core"))

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.3.0")

    debugImplementation("androidx.compose.ui:ui-tooling:1.3.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.3.0")
}








