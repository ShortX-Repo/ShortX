package tornaco.apps.shortx.core

object ContextDataMapping {
    inline fun <reified T : Enum<T>> T.accessExample() = "\$${name}"

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
}