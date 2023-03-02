package tornaco.project.android.shortx

object ClassPaths {
    private const val gradleVersion = "7.2.1"

    const val gradlePlugin = "com.android.tools.build:gradle:$gradleVersion"
    const val gradleApi = "com.android.tools.build:gradle-api:$gradleVersion"
    const val gradleBuilderModel = "com.android.tools.build:builder-model:$gradleVersion"
    const val androidToolsCommon = "com.android.tools:common:30.0.1"

    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10"
}


object Compose {
    const val composeVersion = "1.3.0"
    const val bomVersion = "2023.01.00"

    const val enro = "dev.enro:enro:2.0.0-alpha10"
    const val enroApt = "dev.enro:enro-processor:2.0.0-alpha10"
}

object Others {
    const val glide = "com.github.bumptech.glide:glide:4.13.0"
    const val glideCompiler = "com.github.bumptech.glide:compiler:4.13.0"
    const val landscapistGlide = "com.github.skydoves:landscapist-glide:1.5.0"
}

object Coroutines {
    private const val version = "1.5.0-RC"
    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.0"
    const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
}

object Hilt {
    private const val version = "2.42"
    const val googleAndroidCompiler = "com.google.dagger:hilt-android-compiler:$version"
    const val testing = "com.google.dagger:hilt-android-testing:$version"
    const val library = "com.google.dagger:hilt-android:$version"
    const val gradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$version"
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0"
}
