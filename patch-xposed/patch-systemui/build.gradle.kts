plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    compileOnly(files("${rootProject.rootDir.path}/services-ui/jar/classes.jar"))
    compileOnly(files("${rootProject.rootDir.path}/patch-xposed/patch-main/libs/api-82.jar"))

    implementation(project(":core"))
    implementation(project(":patch-xposed:patch-common"))

    implementation(libs.shortx.ext.dex.maker)

    implementation(libs.mvel2)

    testImplementation(libs.junit)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.reflect)
}