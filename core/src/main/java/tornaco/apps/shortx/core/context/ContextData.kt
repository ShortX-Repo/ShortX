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
        if (map.containsKey(key) && !allKeys.contains(key)) {
            logger.w("Override key: $key ${Throwable().stackTraceToString()}")
        }
        map[key] = value
    }

    fun override(key: String, value: Any?) {
        logger.p("override $key $value")
        map[key] = value
    }

    fun add(map: Map<String, Any?>) {
        logger.p("add $map")
        map.forEach { (s, any) ->
            add(s, any)
        }
    }

    fun get(key: String): Any? = map[key]

    fun pop(key: String): Any? = map.remove(key)

    fun forEach(action: (Pair<String, Any?>) -> Unit) = map.forEach {
        action(it.key to it.value)
    }

    fun recycle() {
        logger.p("recycle ${hashCode()}")
        map.clear()
        pool.release(this)
    }
}

