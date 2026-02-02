plugins {
    alias(libs.plugins.kotlin.jvm)
    id("shortxmvnpub")
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/34/android-34.jar"))
    implementation(libs.rxjava)
}
