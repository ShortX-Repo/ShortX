plugins {
    alias(libs.plugins.kotlin.jvm)
    id("shortxmvnpub")
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/34/android-34.jar"))
    implementation(libs.okhttp)
    implementation("org.simpleframework:simple-xml:2.7.1") {
        exclude(module = "stax")
        exclude(module = "stax-api")
        exclude(module = "xpp3")
    }
}
