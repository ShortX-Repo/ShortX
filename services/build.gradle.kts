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
    implementation(project(":services-33"))
    implementation(project(":external:utils"))
    implementation(project(":external:picasso"))

    implementation(Coroutines.android)
    implementation(Coroutines.core)
    implementation(Coroutines.stdlib)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.google.protobuf:protobuf-java:3.17.2")

    // https://mvnrepository.com/artifact/org.mvel/mvel2
    implementation("org.mvel:mvel2:2.4.14.Final")
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.6.1"
    }
}
