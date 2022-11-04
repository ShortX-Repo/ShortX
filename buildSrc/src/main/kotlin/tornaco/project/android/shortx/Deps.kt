package tornaco.project.android.shortx

object ClassPaths {
    private const val gradleVersion = "7.2.1"

    const val gradlePlugin = "com.android.tools.build:gradle:$gradleVersion"
    const val gradleApi = "com.android.tools.build:gradle-api:$gradleVersion"
    const val gradleBuilderModel = "com.android.tools.build:builder-model:$gradleVersion"
    const val androidToolsCommon = "com.android.tools:common:30.0.1"

    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0"
}


object Compose {
    const val composeVersion = "1.2.0"
}

object Coroutines {
    private const val version = "1.5.0-RC"
    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.0"
    const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
}