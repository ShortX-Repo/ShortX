package tornaco.apps.shortx.core.util

/**
 * 提供将任意嵌套层级的 List 扁平化的工具。
 * - 如果元素是 List，则展开其元素到最外层 List。
 * - 非 List 元素原样保留。
 */
object ListFlatten {
    /**
     * 深度扁平化，将所有嵌套的 List 递归展开为单层 List。
     * @param list 源列表，可以为 null
     * @return 扁平化后的列表（元素类型为 Any?）
     */
    @JvmStatic
    fun flattenDeep(list: List<*>?): List<Any?> {
        if (list == null) return emptyList()
        val result = ArrayList<Any?>()
        for (elem in list) {
            if (elem is List<*>) {
                result.addAll(flattenDeep(elem))
            } else {
                result.add(elem)
            }
        }
        return result
    }

    /**
     * 浅度扁平化，仅展开一层 List。
     * @param list 源列表，可以为 null
     * @return 扁平化后的列表（元素类型为 Any?）
     */
    @JvmStatic
    fun flattenShallow(list: List<*>?): List<Any?> {
        if (list == null) return emptyList()
        val result = ArrayList<Any?>()
        for (elem in list) {
            if (elem is List<*>) {
                result.addAll(elem)
            } else {
                result.add(elem)
            }
        }
        return result
    }

    /**
     * 深度扁平化后做类型过滤，返回期望类型的列表。
     * 非期望类型的元素将被忽略。
     */
    @JvmStatic
    fun <T> flattenDeepTyped(list: List<*>?, clazz: Class<T>): List<T> {
        val flattened = flattenDeep(list)
        val typed = ArrayList<T>()
        for (e in flattened) {
            if (e != null && clazz.isInstance(e)) {
                @Suppress("UNCHECKED_CAST")
                typed.add(e as T)
            }
        }
        return typed
    }
}

/**
 * Kotlin 扩展：深度扁平化当前 List。
 */
fun List<*>.flattenDeep(): List<Any?> = ListFlatten.flattenDeep(this)

/**
 * Kotlin 扩展：浅度扁平化当前 List。
 */
fun List<*>.flattenShallow(): List<Any?> = ListFlatten.flattenShallow(this)