plugins {
    alias(libs.plugins.agp.lib)
}

android {
    namespace = "external.apkbuilder"

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
        viewBinding = false
        dataBinding = false
    }
}

dependencies {
    implementation(files("libs/tiny-sign-0.9.jar"))
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("commons-io:commons-io:2.15.0")
}


