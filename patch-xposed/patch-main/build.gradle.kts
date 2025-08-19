plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    compileOnly(files("${rootProject.rootDir.path}/services-ui/jar/classes.jar"))
    compileOnly(files("libs/api-82.jar"))
    compileOnly(files("libs/libxposed-api.jar"))

    implementation(project(":services"))
    implementation(project(":core"))
    implementation(project(":core-api"))
    implementation(project(":patch-xposed:patch-systemui"))
    implementation(project(":patch-xposed:patch-common"))
    implementation(project(":external:dex-maker"))
    implementation(project(":external:utils"))

    implementation(libs.mvel2)

    testImplementation(libs.junit)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.reflect)
}