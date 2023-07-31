package tornaco.apps.shortx.core.context

import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.Pools


class ContextData private constructor(initialData: Map<String, Any> = emptyMap()) {
    private val logger = Logger("ContextData")

    companion object {
        val pool = Pools.SynchronizedPool<ContextData>(100)

        fun obtain(initialData: Map<String, Any> = emptyMap()): ContextData =
            pool.acquire()?.apply {
                // Put all default value first
                map.putAll(allKeysWithDefaultValue)
                map.putAll(initialData)
            } ?: ContextData(initialData)
    }

    val map: MutableMap<String, Any?> by lazy {
        mutableMapOf<String, Any?>().apply {
            putAll(allKeysWithDefaultValue)
            putAll(initialData)
        }
    }

    fun add(key: String, value: Any?) {
        logger.p("add $key $value")
        if (map.containsKey(key)) {
            logger.w("Override key: $key")
        }
        map[key] = value
    }

    fun get(key: String): Any? = map[key]

    fun forEach(action: (Pair<String, Any?>) -> Unit) = map.forEach {
        action(it.key to it.value)
    }

    fun recycle() {
        logger.p("recycle ${hashCode()}")
        map.clear()
        pool.release(this)
    }
}

