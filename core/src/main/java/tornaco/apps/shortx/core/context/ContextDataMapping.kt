@file:Suppress("EnumEntryName")

package tornaco.apps.shortx.core.context

import tornaco.apps.shortx.core.annotations.DoNotStrip

@DoNotStrip
object ContextDataMapping {
    inline fun <reified T : Enum<T>> T.accessExample() = "{${name}}"

    @DoNotStrip
    enum class Context(val description: String) {
        contextSrcId("Source id of the context, maybe id of a Rule or DA"),
        contextSrcTitle("Source title of the context, maybe Title of a Rule or DA"),
        contextSrcEvent("User event, why this action/condition is executing?")
    }

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
    enum class HttpRequest(val description: String) {
        httpRequestRet("The return value of the Http request")
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

    @DoNotStrip
    enum class ShellCommand(val description: String) {
        shellOut("Output message of the shell command"),
        shellErr("Error message of the shell command, error will redirect to out by default."),
        shellCode("Code of the shell command"),
    }

    @DoNotStrip
    enum class WifiConnectedTo(val description: String) {
        ssid("SSID of the connection"),
        isWifiEnabled("True of wifi is in Enable state"),
        wifiStatusLabel("Status label of current wifi connection"),
    }

    @DoNotStrip
    enum class WifiDisconnectedFrom(val description: String) {
        ssid("SSID from which wifi disconnected"),
        isWifiEnabled("True of wifi is in Enable state"),
        wifiStatusLabel("Status label of current wifi connection"),
    }

    @DoNotStrip
    enum class BatteryTemperatureChanged(val description: String) {
        batteryTemperature("Temperature value as Float. e.g. 36.2"),
    }

    @DoNotStrip
    enum class BatteryLevelChanged(val description: String) {
        batteryLevel("Level value as Int. e.g. 86"),
    }

    @DoNotStrip
    enum class MediaStoreInsert(val description: String) {
        mediaUri("Uri of the inserted media file"),
        mediaFilePath("Resolved file path of the inserted media"),
    }

    @DoNotStrip
    enum class AdvancedKeyEvent(val description: String) {
        keyCode("Key code"),
        pressTimes("Press times of the Key"),
    }
}