import tornaco.project.android.shortx.addAidlTask

plugins {
    id("java")
    id("kotlin")
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    implementation(tornaco.project.android.shortx.Others.okhttp)
    implementation(tornaco.project.android.shortx.Others.okio)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}