package tornaco.project.android.shortx

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.util.*

private val props = Properties()

object Configs {
    const val sxPackageName = "tornaco.apps.shortx"
    var sxVersionCode: Int? = 0
    var sxVersionName: String? = null

    const val compileSdkVersion = 33
    const val minSdkVersion = 26
    const val targetSdkVersion = 33

    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // We use this value to find some build tools binaries.
    // consider to find the latest version more gracefully
    // currently we set to 30.0.3 to test circle ci.
    const val buildToolsVersion = "30.0.3"
    const val ndkVersion = "21.1.6352462"
    val cmakeVersion get() = "3.16.3"

    val Project.resPrefix: String get() = "${this.name}_"

    val Project.outDir: File get() = rootProject.file("out")

    const val KeyStorePath = "environment/keys/android.jks"

    operator fun get(key: String): String? {
        val v = props[key] as? String ?: return null
        return v.ifBlank { null }
    }

    fun Project.keyStoreAlias(): String {
        return Configs["keyAlias"] ?: this.findProperty("keyAlias")?.toString() ?: ""
    }

    fun Project.keyStorePassword(): String {
        return Configs["keyPassword"] ?: this.findProperty("keyPassword")?.toString() ?: ""
    }
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
    }

}
