// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false

    alias(libs.plugins.agp.lib) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.protobuf.gradle.plugin) apply false
}