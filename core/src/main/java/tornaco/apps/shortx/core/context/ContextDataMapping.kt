@file:Suppress("EnumEntryName")

package tornaco.apps.shortx.core.context

object ContextDataMapping {
    inline fun <reified T : Enum<T>> T.accessExample() = "{${name}}"

    enum class NotificationUpdated(val description: String) {
        userId("The user id"),
        pkgName("App package name of the Notification owner"),
        title("Title of the notification"),
        contentText("Content text of the notification")
    }

    enum class NotificationPosted(val description: String) {
        userId("The user id"),
        pkgName("App package name of the Notification owner"),
        title("Title of the notification"),
        contentText("Content text of the notification")
    }

    enum class NotificationRemoved(val description: String) {
        userId("The user id"),
        pkgName("App package name of the Notification owner"),
        title("Title of the notification"),
        contentText("Content text of the notification")
    }

    enum class ExecuteMVEL(val description: String) {
        mvelRet("The return value of the MVEL expression")
    }

    enum class ActivityStarted(val description: String) {
        pkgName("App package name of the started Activity"),
        componentName("Short string of the started Activity component name")
    }

    enum class AppBecomeFg(val description: String) {
        pkgName("App package name of the FG Activity"),
        componentName("Short string of the FG Activity component name")
    }

    enum class AppBecomeBg(val description: String) {
        pkgName("App package name of the BG Activity"),
    }

    enum class TaskRemoved(val description: String) {
        taskId("Id of the removed task"),
        userId("The user id"),
        pkgName("App package name of the task owner"),
    }

    enum class AppAdded(val description: String) {
        userId("The user id"),
        pkgName("App package name of the installed package"),
        label("App label of the installed package"),
    }

    enum class AppRemoved(val description: String) {
        userId("The user id"),
        pkgName("App package name of the uninstalled package"),
        label("App label of the uninstalled package"),
    }

    enum class AppUpdated(val description: String) {
        userId("The user id"),
        pkgName("App package name of the updated package"),
        label("App label of the updated package"),
    }

    enum class ReadClipboard(val description: String) {
        text("Text read from clipboard"),
    }
}