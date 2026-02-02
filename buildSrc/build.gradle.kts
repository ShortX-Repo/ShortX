repositories {
    mavenCentral()
    google()
}

plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("shortx") {
            id = "shortx"
            implementationClass = "tornaco.project.android.shortx.ShortXBuildPlugin"
        }
        register("shortxmvnpub") {
            id = "shortxmvnpub"
            implementationClass = "tornaco.project.android.shortx.ShortXMavenPublishPlugin"
        }
    }
}
