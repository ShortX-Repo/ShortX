package tornaco.project.android.shortx

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

/**
 * ShortX Maven 发布插件，为模块提供通用的 Maven 发布配置。
 */
class ShortXMavenPublishPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 应用 maven-publish 插件
        project.plugins.apply("maven-publish")

        // 配置 Maven 发布
        project.configure<PublishingExtension> {
            publications {
                // 为不同类型的模块创建不同的发布配置
                val isAndroidLibrary = project.plugins.hasPlugin("com.android.library")
                val isKotlinJvm = project.plugins.hasPlugin("org.jetbrains.kotlin.jvm")

                if (isAndroidLibrary) {
                    // Android 库模块
                    register<MavenPublication>("mavenRelease") {
                        setupPublication(project)
                        // Android 库需要在 afterEvaluate 中配置组件
                        project.afterEvaluate {
                            from(project.components.getByName("release"))
                        }
                    }
                } else if (isKotlinJvm) {
                    // 纯 Kotlin JVM 模块
                    register<MavenPublication>("mavenRelease") {
                        setupPublication(project)
                        from(project.components.getByName("java"))
                    }
                }
            }

            // 配置 Maven Local 仓库
            repositories {
                mavenLocal()
            }
        }
    }

    /**
     * 设置发布的通用配置。
     */
    private fun MavenPublication.setupPublication(project: Project) {
        val artifactId = project.path.split(":").last()
        groupId = Configs.sxPackageName
        version = "1.0.0"

        // 配置 POM 信息
        pom {
            name.set(artifactId)
            description.set("ShortX external library: $artifactId")
            url.set("https://github.com/ShortX-Repo")

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

            developers {
                developer {
                    id.set("shortx")
                    name.set("ShortX Team")
                    email.set("shortx@example.com")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/ShortX-Repo/ShortX.git")
                developerConnection.set("scm:git:ssh://github.com/ShortX-Repo/ShortX.git")
                url.set("https://github.com/ShortX-Repo/ShortX")
            }
        }
    }
}