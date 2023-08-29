plugins {
    id("java")
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    implementation(project(":core"))
    implementation(libs.dalvik.dx)
}

