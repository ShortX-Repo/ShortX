plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.protobuf.gradle.plugin)
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.stdlib.jdk8)

    api(libs.protobuf.java)

    compileOnly(project(":android-api"))
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.25.2"
    }
}
