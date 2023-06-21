plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.protobuf.gradle.plugin)
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    compileOnly(files("lib/service-ui-stubs.jar"))

    implementation(project(":core"))
    implementation(project(":services-33"))
    implementation(project(":external:utils"))
    implementation(project(":external:picasso"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.stdlib.jdk8)

    implementation(libs.kotlinx.serialization.json.jvm)
    implementation(libs.kotlin.reflect)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)

    implementation(libs.protobuf.java)

    // https://mvnrepository.com/artifact/org.mvel/mvel2
    implementation(libs.mvel2)

    testImplementation(libs.junit)
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.23.2"
    }
}
