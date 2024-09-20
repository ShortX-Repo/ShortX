plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    compileOnly(files("../services-ui/jar/service-ui-stubs.jar"))
    compileOnly(libs.xposed.api)

    implementation(project(":services"))
    implementation(project(":core"))
    implementation(libs.shortx.ext.dex.maker)

    implementation(libs.mvel2)

    testImplementation(libs.junit)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.reflect)
}