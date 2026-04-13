plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("shortxmvnpub")
}

android {
    namespace = "io.github.rosemoe.sora.app"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = false
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(platform("io.github.rosemoe:editor-bom:0.24.4"))
    implementation("io.github.rosemoe:editor")
    implementation("io.github.rosemoe:language-java")
    implementation("io.github.rosemoe:language-textmate")
    implementation("io.github.rosemoe:oniguruma-native")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.core.ktx)
    // Note: Do not upgrade this lib, ComposeOverlay.
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
