package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.services.ui.Pools


class ContextData private constructor(initialData: Map<String, Any> = emptyMap()) {
    companion object {
        val pool = Pools.SynchronizedPool<ContextData>(100)

        fun obtain(initialData: Map<String, Any> = emptyMap()): ContextData =
            pool.acquire()?.apply {
                map.putAll(initialData)
            } ?: ContextData(initialData)
    }

    private val map: MutableMap<String, Any> by lazy {
        mutableMapOf<String, Any>().apply {
            putAll(initialData)
        }
    }

    fun add(key: String, value: Any) {
        require(!map.containsKey(key)) {
            "Key: $key dup."
        }
        map[key] = value
    }

    fun isEmpty() = map.isEmpty()

    fun forEach(action: (Pair<String, Any>) -> Unit) = map.forEach {
        action(it.key to it.value)
    }

    fun recycle() {
        map.clear()
        pool.release(this)
    }
}

