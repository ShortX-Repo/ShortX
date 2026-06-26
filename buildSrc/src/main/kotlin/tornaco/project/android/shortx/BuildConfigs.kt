package tornaco.project.android.shortx

import org.gradle.api.Plugin
import org.gradle.api.Project
import tornaco.project.android.shortx.Configs.sxFlavor
import tornaco.project.android.shortx.Configs.sxIsDebugBuild
import java.io.File
import java.util.Properties

private val props = Properties()

object Configs {
    const val sxPackageName = "tornaco.apps.shortx"
    var sxVersionCode: Int? = 0
    var sxVersionName: String? = null
    val sxVersionNameSuffix: String get() = System.getProperty("versionNameSuffix", "")
    var sxIsDebugBuild: Boolean = false
    var sxFlavor: Flavor = Flavor.ROW

    const val compileSdkVersion = 36
    const val minSdkVersion = 26
    const val targetSdkVersion = 36

    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // We use this value to find some build tools binaries.
    // consider to find the latest version more gracefully
    // currently we set to 30.0.3 to test circle ci.
    const val buildToolsVersion = "30.0.3"
    // NDK r26+ 才完全支持 16 KB page size
    const val ndkVersion = "29.0.14206865"
    val cmakeVersion get() = "3.22.1"

    val Project.resPrefix: String get() = "${this.name}_"

    val Project.outDir: File get() = rootProject.file("out")
    val Project.magiskModuleBuildDir get() = File("${outDir}/magisk_module")

    const val KeyStorePath = "environment/keys/android.jks"
    const val CIKeyStorePath = "environment/keys/ci.jks"
    const val CIKeyAlias = "ci-shortx"
    const val CIKeyPassword = "ci123456"

    val isCI: Boolean get() = System.getenv("CI") == "true"

    val effectiveKeyStorePath: String
        get() = if (isCI) CIKeyStorePath else KeyStorePath

    operator fun get(key: String): String? {
        val v = props[key] as? String ?: return null
        return v.ifBlank { null }
    }

    fun flavorValue(key: String, flavor: Flavor = sxFlavor): String? {
        val flavorKey = "$key.${flavor.name.lowercase()}"
        return get(flavorKey) ?: get(key)
    }

    fun Project.keyStoreAlias(): String {
        if (isCI) return CIKeyAlias
        return Configs["keyAlias"] ?: this.findProperty("keyAlias")?.toString() ?: ""
    }

    fun Project.keyStorePassword(): String {
        if (isCI) return CIKeyPassword
        return Configs["keyPassword"] ?: this.findProperty("keyPassword")?.toString() ?: ""
    }
}

object MagiskModConfigs {
    /*
       This name will be used in the name of the so file ("lib${moduleLibraryName}.so").
       If this module need to support Riru pre-v24 (API < 24), moduleLibraryName must start with "riru_".
    */
    const val moduleLibraryName = "zygisk_shortX"

    /*
       Magisk module ID
       Since Magisk use it to distinguish different modules, you should never change it.
       Note, the older version of the template uses '-' instead of '_', if your are upgrading from
       the older version, please pay attention.
    */
    const val magiskModuleId = "zygisk_shortX"

    const val moduleName = "Zygisk - ShortX"
    const val moduleAuthor = "Tornaco"
    const val moduleDescription =
        """Provides hooks for ShortX. Support Android11 & Android12. Requires Magisk 24.0+ and Zygisk"""
    val moduleVersion = Configs.sxVersionName
    val moduleVersionCode = Configs.sxVersionCode
}


@ExperimentalStdlibApi
class ShortXBuildPlugin : Plugin<Project> {
    override fun apply(project: Project) = project.applyPlugin()

    private fun Project.applyPlugin() {
        props.clear()
        rootProject.file("gradle.properties").let { propFile ->
            if (propFile.exists()) {
                propFile.inputStream().use { props.load(it) }
            }
        }
        rootProject.file("local.properties").let { propFile ->
            if (propFile.exists()) {
                propFile.inputStream().use { props.load(it) }
            }
        }

        updateBuildConfigs()
    }

    private fun Project.updateBuildConfigs() {
        sxIsDebugBuild = getBuildVariant() == Variant.DEBUG
        sxFlavor = getBuildFlavor()
        log("sxIsDebugBuild: $sxIsDebugBuild")
        log("sxFlavor: $sxFlavor")
    }

    private fun Project.getBuildVariant(): Variant {
        val tskReqStr = gradle.startParameter.taskRequests.map {
            it.args
        }.toString()
        log("getBuildVariant, tskReqStr: $tskReqStr")
        val isDebug = tskReqStr.contains(Variant.DEBUG.name, ignoreCase = true)
        return if (isDebug) {
            Variant.DEBUG
        } else {
            Variant.RELEASE
        }
    }

    private fun Project.getBuildFlavor(): Flavor {
        val tskReqStr = gradle.startParameter.taskRequests.map {
            it.args
        }.toString()
        log("getBuildFlavor, tskReqStr: $tskReqStr")
        val isDebug = tskReqStr.contains(Flavor.PRC.name, ignoreCase = true)
        return if (isDebug) {
            Flavor.PRC
        } else {
            Flavor.ROW
        }
    }
}

enum class Variant {
    DEBUG,
    RELEASE
}

enum class Flavor {
    ROW,
    PRC
}
