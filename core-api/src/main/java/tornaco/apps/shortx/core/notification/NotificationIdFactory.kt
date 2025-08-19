package tornaco.apps.shortx.core.notification

import java.util.concurrent.atomic.AtomicInteger

object NotificationIdFactory {
    private val ID = AtomicInteger(0)

    private val ID_MAP = HashMap<Any, Int>()

    @JvmStatic
    fun getNextId(): Int {
        return ID.getAndIncrement()
    }

    @JvmStatic
    fun getIdByTag(tag: Any): Int {
        var id: Int? = ID_MAP[tag]
        if (id == null) {
            id = getNextId()
            ID_MAP[tag] = id
        }
        return id
    }
}