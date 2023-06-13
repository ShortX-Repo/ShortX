@file:Suppress("EnumEntryName")

package tornaco.apps.shortx.core.context

import tornaco.apps.shortx.core.annotations.DoNotStrip

@DoNotStrip
object ContextDataMapping {
    inline fun <reified T : Enum<T>> T.accessExample() = "{${name}}"

    @DoNotStrip
    enum class NotificationUpdated(val description: String) {
        userId("The user id"),
        pkgName("App package name of the Notification owner"),
        title("Title of the notification"),
        contentText("Content text of the notification")
    }

    @DoNotStrip
    enum class NotificationPosted(val description: String) {
        userId("The user id"),
        pkgName("App package name of the Notification owner"),
        title("Title of the notification"),
        contentText("Content text of the notification")
    }

    @DoNotStrip
    enum class NotificationRemoved(val description: String) {
        userId("The user id"),
        pkgName("App package name of the Notification owner"),
        title("Title of the notification"),
        contentText("Content text of the notification")
    }

    @DoNotStrip
    enum class ExecuteMVEL(val description: String) {
        mvelRet("The return value of the MVEL expression")
    }

    @DoNotStrip
    enum class ActivityStarted(val description: String) {
        pkgName("App package name of the started Activity"),
        componentName("Short string of the started Activity component name"),
        appLabel("The app label")
    }

    @DoNotStrip
    enum class AppBecomeFg(val description: String) {
        pkgName("App package name of the FG Activity"),
        componentName("Short string of the FG Activity component name"),
        appLabel("The app label")
    }

    @DoNotStrip
    enum class AppBecomeBg(val description: String) {
        pkgName("App package name of the BG Activity"),
        appLabel("The app label")
    }

    @DoNotStrip
    enum class TaskRemoved(val description: String) {
        taskId("Id of the removed task"),
        userId("The user id"),
        pkgName("App package name of the task owner"),
    }

    @DoNotStrip
    enum class AppAdded(val description: String) {
        userId("The user id"),
        pkgName("App package name of the installed package"),
        label("App label of the installed package"),
    }

    @DoNotStrip
    enum class AppRemoved(val description: String) {
        userId("The user id"),
        pkgName("App package name of the uninstalled package"),
        label("App label of the uninstalled package"),
    }

    @DoNotStrip
    enum class AppUpdated(val description: String) {
        userId("The user id"),
        pkgName("App package name of the updated package"),
        label("App label of the updated package"),
    }

    @DoNotStrip
    enum class PkgStopRunning(val description: String) {
        userId("The user id"),
        pkgName("App package name of the stopped package"),
    }

    @DoNotStrip
    enum class AudioFocusChanged(val description: String) {
        userId("The user id"),
        pkgName("App package name of the focus changed package"),
        isGain("true if it has gain audio focus"),
        isLost("true if it has lost audio focus"),
    }

    @DoNotStrip
    enum class ReadClipboard(val description: String) {
        text("Text read from clipboard"),
    }
}