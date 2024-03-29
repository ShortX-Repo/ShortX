@file:Suppress("EnumEntryName")

package tornaco.apps.shortx.core.context

import tornaco.apps.shortx.core.annotations.DoNotStrip

@DoNotStrip
object ContextDataMapping {
    fun String.asAccessExample() = "{${this}}"

    @DoNotStrip
    enum class Notification(val description: String) {
        userId("The user id"),
        pkgName("App package name of the Notification owner"),
        title("Title of the notification"),
        contentText("Content text of the notification"),
        notificationTag("Tag of the notification, nullable")
    }

    @DoNotStrip
    enum class AppHasNotification(val description: String) {
        matchedAppHasNotificationUserId("The user id of the matched Notification"),
        matchedAppHasNotificationPkgName("App package name of the matched Notification owner"),
        matchedAppHasNotificationTitle("Title of the matched notification"),
        matchedAppHasNotificationContentText("Content text of the matched notification"),
        matchedAppHasNotificationNotificationTag("Tag of the matched notification, nullable")
    }

    @DoNotStrip
    enum class ExecuteMVEL(val description: String) {
        mvelRet("The return value of the MVEL expression")
    }

    @DoNotStrip
    enum class ExecuteJS(val description: String) {
        jsRet("The return value of the JS expression")
    }

    @DoNotStrip
    enum class HttpRequest(val description: String) {
        httpRequestRet("The return value of the Http request, as String")
    }

    @DoNotStrip
    enum class ActivityStarted(val description: String) {
        pkgName("App package name of the started Activity"),
        userId("App user id the started Activity"),
        componentName("Short string of the started Activity component name"),
        appLabel("The app label"),
        activityIntentUri("Intent to uri")
    }

    @DoNotStrip
    enum class ActivityStopped(val description: String) {
        pkgName("App package name of the started Activity"),
        userId("App user id the started Activity"),
        componentName("Short string of the started Activity component name"),
        appLabel("The app label"),
    }

    @DoNotStrip
    enum class AppBecomeFg(val description: String) {
        pkgName("App package name of the FG Activity"),
        userId("App user id the FG Activity"),
        appLabel("The app label")
    }

    @DoNotStrip
    enum class AppBecomeBg(val description: String) {
        pkgName("App package name of the BG Activity"),
        userId("App user id the BG Activity"),
        appLabel("The app label")
    }

    @DoNotStrip
    enum class TaskRemoved(val description: String) {
        appLabel("The app label"),
        taskId("Id of the removed task"),
        userId("The user id"),
        pkgName("App package name of the task owner"),
    }

    @DoNotStrip
    enum class AppProcessStarted(val description: String) {
        processName("Process name"),
        appLabel("The process owner's app label"),
        userId("The process owner's user id"),
        pkgName("The process owner's package name"),
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
        fromVersionCode("Version code before update"),
        toVersionCode("Version code after update"),
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
    enum class CallStateChanged(val description: String) {
        callState("The call state, maybe one of CallStateRinging, CallStateOffHook, CallStateIdle"),
        incomingNumber("Incoming number"),
    }

    @DoNotStrip
    enum class ClipboardContentChanged(val description: String) {
        clipboardContent("Text read from clipboard"),
    }

    @DoNotStrip
    enum class ReadClipboard(val description: String) {
        clipboardContent("Text read from clipboard"),
    }

    @DoNotStrip
    enum class GetTextFromScreenNode(val description: String) {
        textOfTheView("Text of the matched View"),
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
    enum class ConnectedWifiSignalLevelChanged(val description: String) {
        ssid("SSID of the connection"),
        rssi("Wifi rssi"),
        level("Wifi signal level"),
        statusLabel("Wifi status label"),
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

    @DoNotStrip
    enum class BTDeviceConnected(val description: String) {
        btDeviceAlias("Alias of the connected device"),
        btDeviceAddress("Address of the connected device"),
        btDeviceBatteryLevel("Battery level(int) of the connected device"),
    }

    @DoNotStrip
    enum class BTDeviceDisConnected(val description: String) {
        btDeviceAlias("Alias of the disconnected device"),
        btDeviceAddress("Address of the disconnected device"),
    }

    @DoNotStrip
    enum class OnStartOp(val description: String) {
        opLabel("Label of the op"),
        opCode("App ops code of the op, see AppOpsManager for details."),
        opAppLabel("App label"),
        opAppPkgName("App package name"),
        opAppUid("The uid of the App"),
        opAppUserId("The userId of the App"),
    }

    @DoNotStrip
    enum class OnFinishOp(val description: String) {
        opLabel("Label of the op"),
        opCode("App ops code of the op, see AppOpsManager for details."),
        opAppLabel("App label"),
        opAppPkgName("App package name"),
        opAppUid("The uid of the App"),
        opAppUserId("The userId of the App"),
    }

    @DoNotStrip
    enum class ShowTextFieldDialog(val description: String) {
        textFieldInput("Input of the text field"),
    }

    @DoNotStrip
    enum class SystemSettingsChanged(val description: String) {
        settingsUrl("Url of the Settings"),
        settingsValue("Current value"),
    }

    @DoNotStrip
    enum class AppGainWindowFocus(val description: String) {
        userId("The user id"),
        pkgName("App package name of the focused package"),
    }

    @DoNotStrip
    enum class AppLostWindowFocus(val description: String) {
        userId("The user id"),
        pkgName("App package name of the losing focus package"),
    }

    @DoNotStrip
    enum class HasFoundNodeOnScreen(val description: String) {
        matchedViewText("Matched text of the view"),
        matchedViewTextIsRegex("true if matched text is regex"),
        matchedViewId("Matched id of the view"),
        sourceNodeId("sourceNodeId of the AccessibilityNodeInfo"),
        windowId("windowId of the AccessibilityNodeInfo"),
    }

    @DoNotStrip
    enum class ForEachPkgSet(val description: String) {
        loopAppLabel("App label of current loop"),
        loopAppPkgName("App package name of current loop"),
        loopAppUserId("App user id of current loop"),
    }

    @DoNotStrip
    enum class DownloadFile(val description: String) {
        isDownloadSuccess("If download is successful or not. true/false"),
        downloadFileUri("Uri of the file is it is successful"),
        downloadFilePath("Path of the file is it is successful"),
    }
}