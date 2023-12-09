plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.protobuf.gradle.plugin)
    alias(libs.plugins.kover)
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    compileOnly(files("lib/service-ui-stubs13.jar"))

    implementation(project(":core"))
    implementation(project(":services-29"))
    implementation(project(":services-30"))
    implementation(project(":services-33"))
    implementation(project(":services-34"))
    implementation(project(":external:utils"))
    implementation(project(":external:picasso"))
    implementation(project(":external:rhino-android"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.stdlib.jdk8)

    implementation(libs.kotlinx.serialization.json.jvm)
    implementation(libs.kotlin.reflect)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.protobuf.java)

    // https://mvnrepository.com/artifact/org.mvel/mvel2
    implementation(libs.mvel2)

    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    // For HTML. mvel
    implementation(libs.jsoup)

    implementation(libs.zxing.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.24.4"
    }
}
