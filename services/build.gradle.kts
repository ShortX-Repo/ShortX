import tornaco.project.android.shortx.Coroutines

plugins {
    id("java")
    id("kotlin")
    id("com.google.protobuf").version("0.9.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))

    implementation(project(":core"))
    implementation(project(":external:utils"))

    implementation(Coroutines.android)
    implementation(Coroutines.core)
    implementation(Coroutines.stdlib)

    implementation("com.google.protobuf:protobuf-java:3.17.2")
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.6.1"
    }
}
