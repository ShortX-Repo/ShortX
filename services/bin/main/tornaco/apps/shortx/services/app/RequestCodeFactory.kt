package tornaco.apps.shortx.services.app

import java.util.concurrent.atomic.AtomicInteger

object RequestCodeFactory {
    private val ID = AtomicInteger(1)

    private val ID_MAP = HashMap<Any, Int>()

    @JvmStatic
    fun getNext(): Int {
        return ID.getAndIncrement()
    }

    @JvmStatic
    fun getByTag(tag: Any): Int {
        var id: Int? = ID_MAP[tag]
        if (id == null) {
            id = getNext()
            ID_MAP[tag] = id
        }
        return id
    }
}