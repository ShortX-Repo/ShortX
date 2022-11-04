package tornaco.project.android.shortx

import org.gradle.api.Project


fun Project.log(message: String) {
    logger.quiet(">>>>>> $message")
}