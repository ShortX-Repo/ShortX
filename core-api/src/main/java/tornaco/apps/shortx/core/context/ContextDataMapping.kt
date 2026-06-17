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
    enum class WebSocket(val description: String) {
        wsMessage("Received message from WebSocket, if it is MESSAGE event"),
        wsReason("Reason of the event"),
        wsEvent("WebSocket event: one of WS_OPEN, WS_MESSAGE, WS_CLOSED, WS_FAILURE")
    }

    @DoNotStrip
    enum class ActivityStarted(val description: String) {
        pkgName("App package name of the started Activity"),
        userId("App user id the started Activity"),
        componentName("Short string of the started Activity component name"),
        appLabel("The app label"),
        taskLabel("The task label"),
        activityIntentUri("Intent to uri")
    }

    @DoNotStrip
    enum class ActivityStopped(val description: String) {
        pkgName("App package name of the stopped Activity"),
        userId("App user id the stopped Activity"),
        componentName("Short string of the stopped Activity component name"),
        appLabel("The app label"),
        taskLabel("The task label"),
    }

    @DoNotStrip
    enum class ActivityDestroyed(val description: String) {
        pkgName("App package name of the destroyed Activity"),
        userId("App user id the destroyed Activity"),
        componentName("Short string of the destroyed Activity component name"),
        appLabel("The app label"),
        taskLabel("The task label"),
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
    enum class AppProcessRemoved(val description: String) {
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
        appLabel("App label of the stopped package"),
    }

    @DoNotStrip
    enum class PkgStartRunning(val description: String) {
        userId("The user id"),
        pkgName("App package name of the started package"),
        appLabel("App label of the started package"),
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
    enum class GetScreenOnTime(val description: String) {
        screenOnTime("The screen on time in seconds"),
    }

    @DoNotStrip
    enum class GetCurrentLocationInfo(val description: String) {
        latitude("Latitude of the current location"),
        longitude("Longitude of the current location"),
        provider("Provider of the current location"),
        accuracy("Accuracy of the current location in meters"),
    }

    @DoNotStrip
    enum class GetAppInfo(val description: String) {
        appLabel("App label"),
        versionCode("App version code"),
        versionName("App version name"),
        isRunning("True if the app has a running process"),
    }

    @DoNotStrip
    enum class GetCurrentLocationAddress(val description: String) {
        addressLine("Full address line of the current location"),
        countryName("Country name of the current location"),
        adminArea("Admin area of the current location"),
        subAdminArea("Sub-admin area of the current location"),
        locality("Locality of the current location"),
        subLocality("Sub-locality of the current location"),
        thoroughfare("Thoroughfare of the current location"),
        subThoroughfare("Sub-thoroughfare of the current location"),
        postalCode("Postal code of the current location"),
        featureName("Feature name of the current location"),
    }

    @DoNotStrip
    enum class AreaScreenshot(val description: String) {
        screenshotFilePath("The file path of the screenshot"),
        screenshotFileUri("The file uri of the screenshot"),
    }

    @DoNotStrip
    enum class TakePhoto(val description: String) {
        photoFilePath("The file path of the photo"),
        photoFileUri("The file uri of the photo"),
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
    enum class MediaStoreDelete(val description: String) {
        mediaUri("Uri of the deleted media file"),
        mediaFilePath("Resolved file path of the deleted media"),
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
    enum class ShowChoiceDialog(val description: String) {
        choices("The choice(s)"),
    }

    @DoNotStrip
    enum class ShowListDialog(val description: String) {
        selectedListItem("The selected list item(s)"),
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
    enum class Logcat(val description: String) {
        line("Matched log line"),
    }

    @DoNotStrip
    enum class Broadcast(val description: String) {
        intent(
            """
                Intent of the Broadcast, you can use MVEL to access the extras. 
                
                MVEL example: 
                Get action: `intent.action`
                Get extra: `intent.getStringExtra("some key")`
            """.trimIndent()
        ),
    }

    @DoNotStrip
    enum class UsbDeviceAttachDetach(val description: String) {
        productName("USB device product name"),
        manufacturerName("USB device manufacturer name"),
    }

    @DoNotStrip
    enum class LightSensor(val description: String) {
        lightIntensity("Sensor value")
    }

    @DoNotStrip
    enum class ProximitySensor(val description: String) {
        distance("Sensor value"),
        maximumRange("maximumRange Sensor value"),
    }

    @DoNotStrip
    enum class AccelerometerSensor(val description: String) {
        accX("Sensor value, X"),
        accY("Sensor value, Y"),
        accZ("Sensor value, Z"),
    }

    @DoNotStrip
    enum class Sound(val description: String) {
        decibels("Decibels value"),
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

    @DoNotStrip
    enum class WebDavList(val description: String) {
        webdavResources(
            """
            Webdav list resources entity. See:
            [DavResource.java](https://github.com/thegrizzlylabs/sardine-android/blob/master/src/main/java/com/thegrizzlylabs/sardineandroid/DavResource.java)
        """.trimIndent()
        ),
        webdavPaths("Paths of the list result"),
        webdavNames("Names of the list result"),
    }


    @DoNotStrip
    enum class WebDavGet(val description: String) {
        webdavGetPath("Local path of the file"),
    }

    @DoNotStrip
    enum class MatchRegex(val description: String) {
        isMatch("Indicates whether the regular expression matches the input"),
        matchResult("Returns a sequence of all occurrences of a regular expression within the input string"),
    }

    @DoNotStrip
    enum class ReplaceRegex(val description: String) {
        replaceResult("Returns the replaced string"),
    }

    @DoNotStrip
    enum class TextProcessing(val description: String) {
        textProcessResult("Returns the processed string"),
    }

    @DoNotStrip
    enum class ExportBackup(val description: String) {
        backupFilePath("Path of the backup file"),
    }

    @DoNotStrip
    enum class Rect(val description: String) {
        rectLeft("The offset of the left edge of this rectangle from the x axis."),
        rectTop("The offset of the top edge of this rectangle from the y axis."),
        rectRight("The offset of the right edge of this rectangle from the x axis."),
        rectBottom("The offset of the bottom edge of this rectangle from the y axis."),
        rectFlattenToString("Rect flatten to String"),
    }

    @DoNotStrip
    enum class OcrDetect(val description: String) {
        ocrResult("String of the detected text"),
    }

    @DoNotStrip
    enum class FindImagePosition(val description: String) {
        pointX("X offset of the image from screen, or 0 is not found"),
        pointY("Y offset of the image from screen, or 0 is not found"),
        isImageFound("If the image has been found"),
        findImageJson("JSON string with detailed template-match result when output type is JSON (fields depend on the CV engine)"),
    }

    @DoNotStrip
    enum class FindPointsByColor(val description: String) {
        pointX("X offset of the color from screen, or 0 is not found"),
        pointY("Y offset of the color from screen, or 0 is not found"),
        isColorFound("If the color has been found"),
        findPointsByColorJson("JSON string with detailed color-match result when output type is JSON (fields depend on the CV engine)"),
    }

    @DoNotStrip
    enum class ForEach(val description: String) {
        foreachIndex("Index of the loop"),
        foreachData("Data of the loop"),
    }

    @DoNotStrip
    enum class CustomContextMenu(val description: String) {
        selectedText("Selected text"),
    }

    @DoNotStrip
    enum class ScreenGesture(val description: String) {
        eventX("The screen.x of the motion event"),
        eventY("The screen.y of the motion event"),
    }

    @DoNotStrip
    enum class EdgeGesture(val description: String) {
        eventX("The screen.x of the motion event"),
        eventY("The screen.y of the motion event"),
    }

    @DoNotStrip
    enum class CornerSwipeToCenter(val description: String) {
        eventX("The screen.x of the motion event"),
        eventY("The screen.y of the motion event"),
        corner("The triggered screen corner"),
    }

    @DoNotStrip
    enum class CpuAvgAvailability(val description: String) {
        latestAvgAvailabilityPercent("The latest average CPU availability percent."),
        pastNMillisAvgAvailabilityPercent("The past N-millisecond average CPU availability percent."),
        pastNMillisDuration("The duration over which the pastNMillisAvgAvailabilityPercent was calculated."),
    }

    @DoNotStrip
    enum class OnStartDynamicShortcut(val description: String) {
        pkgName("Package name of the Dynamic shortcut start event"),
        userId("User id of the Dynamic shortcut start event"),
    }

    @DoNotStrip
    enum class IncomingShare(val description: String) {
        sharedText("The shared text content"),
        sharedUri("The shared URI (content:// or file://)"),
        sharedMimeType("The MIME type of the shared content"),
        sourceAppPkg("The package name of the app that shared the content"),
    }
}
