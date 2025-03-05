import com.android.build.gradle.api.AndroidBasePlugin
import com.github.megatronking.stringfog.plugin.StringFogExtension
import tornaco.project.android.shortx.Configs.outDir
import tornaco.project.android.shortx.Configs.sxVersionCode
import tornaco.project.android.shortx.Configs.sxVersionName
import tornaco.project.android.shortx.log

buildscript {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        classpath(libs.stringfog.plugin)
        classpath(libs.stringfog.xor)
    }
}

plugins {
    alias(libs.plugins.agp.lib) apply false
    alias(libs.plugins.agp.app) apply false

    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.dagger.hilt.android) apply false

    alias(libs.plugins.protobuf.gradle.plugin) apply false
    alias(libs.plugins.gmazzo.buildconfig) apply false

    alias(libs.plugins.gladed.androidgitversion) apply true
    alias(libs.plugins.diffplug.spotless) apply true
    alias(libs.plugins.kover) apply false

    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt)

    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.3" apply false

    id("shortx")
}

androidGitVersion {
    prefix = "v"
    codeFormat = "MNPBBB"
    baseCode = 5555
}

// Make sure new code > released.
val legacyCodeBase = 100000
// 2000.1.1 millis
val startOf2000 = 946684800000L
// +1 10min
val versionCodeByTime =
    ((System.currentTimeMillis() - startOf2000) / 1000 / 60 / 10).toInt() + legacyCodeBase
sxVersionCode = versionCodeByTime
sxVersionName = androidGitVersion.name()

val androidSourceCompatibility by extra(JavaVersion.VERSION_21)
val androidTargetCompatibility by extra(JavaVersion.VERSION_21)

subprojects {
    log("subprojects: ${this.name}")

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(
            arrayOf(
                "-Xmaxerrs",
                "1000"
            )
        )
        options.encoding = "UTF-8"
    }

    buildDir = file("${outDir}${path.replace(":", File.separator)}")

    plugins.withType(com.android.build.gradle.api.AndroidBasePlugin::class.java) {
        extensions.configure(com.android.build.api.dsl.CommonExtension::class.java) {
            compileSdk = tornaco.project.android.shortx.Configs.compileSdkVersion
            ndkVersion = tornaco.project.android.shortx.Configs.ndkVersion

            externalNativeBuild {
                cmake {
                    version = "3.22.1+"
                }
            }

            defaultConfig {
                minSdk = tornaco.project.android.shortx.Configs.minSdkVersion
                if (this is com.android.build.api.dsl.ApplicationDefaultConfig) {
                    targetSdk = tornaco.project.android.shortx.Configs.targetSdkVersion
                    versionCode = sxVersionCode
                    versionName = sxVersionName
                    testInstrumentationRunner =
                        tornaco.project.android.shortx.Configs.testInstrumentationRunner
                }
            }

            lint {
                abortOnError = true
                checkReleaseBuilds = false
            }

            compileOptions {
                sourceCompatibility = androidSourceCompatibility
                targetCompatibility = androidTargetCompatibility
            }

        }
    }
    plugins.withType(JavaPlugin::class.java) {
        extensions.configure(JavaPluginExtension::class.java) {
            sourceCompatibility = androidSourceCompatibility
            targetCompatibility = androidTargetCompatibility
        }
    }

    apply(plugin = rootProject.libs.plugins.detekt.get().pluginId)
    detekt {
        buildUponDefaultConfig = true // 在默认配置基础上自定义
        allRules = false // 不启用所有规则
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        baseline = file("$rootDir/config/detekt/baseline.xml")
    }

    dependencies {
        detektPlugins(rootProject.libs.detekt.formatting)
        // 添加Compose规则集
        detektPlugins(rootProject.libs.detekt.compose)
    }

    project.afterEvaluate {
        val hasAndroidPlugin = project.plugins.hasPlugin(AndroidBasePlugin::class)
        log("Check publishing subproject: ${project.name}, hasAndroidPlugin: $hasAndroidPlugin")
        val fogModules = listOf(
            "patch",
            "ui"
        )
        if (fogModules.contains(project.name)) {
            log("Fogging: ${project.name}")
            project.plugins.apply("stringfog")

            project.configure<StringFogExtension> {
                // 必要：加解密库的实现类路径，需和上面配置的加解密算法库一致。
                implementation = "com.github.megatronking.stringfog.xor.StringFogImpl"
                // 可选：加密开关，默认开启。
                enable = true
                // 可选：指定需加密的代码包路径，可配置多个，未指定将默认全部加密。
                // fogPackages = arrayOf("com.xxx.xxx")
                kg = com.github.megatronking.stringfog.plugin.kg.RandomKeyGenerator()
                // base64或者bytes
                mode = com.github.megatronking.stringfog.plugin.StringFogMode.bytes
            }
            project.dependencies {
                add("implementation", libs.stringfog.xor)
            }
        }

    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

rootProject.buildDir = outDir
tasks["clean"].doLast {
    delete(outDir)
}
