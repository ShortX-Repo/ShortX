plugins {
    id("com.android.application")
}

android {
    defaultConfig {
        applicationId = tornaco.project.android.shortx.Configs.sxPackageName
        vectorDrawables.useSupportLibrary = true
        versionName = tornaco.project.android.shortx.Configs.sxVersionName
        versionCode = tornaco.project.android.shortx.Configs.sxVersionCode

        minSdk = tornaco.project.android.shortx.Configs.minSdkVersion
        compileSdk = tornaco.project.android.shortx.Configs.compileSdkVersion
        targetSdk = tornaco.project.android.shortx.Configs.targetSdkVersion
        testInstrumentationRunner = tornaco.project.android.shortx.Configs.testInstrumentationRunner
        multiDexEnabled = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = false
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
        viewBinding = false
        dataBinding = false
    }
}

dependencies {
    implementation(project(":patch-magisk:bridge"))
}

tasks.register("extractBridgeJar", Jar::class.java) {
    group = "dex"
    from(zipTree("$buildDir/outputs/apk/release/bridge-dex-app-release-unsigned.apk"))
    include("classes.dex")
    destinationDirectory.set(file("$buildDir").resolve("outputs"))
    archiveFileName.set("shortx-bridge.jar")
}

afterEvaluate {
    tasks.findByName("extractBridgeJar")?.dependsOn("assembleRelease")
}