package util


@Suppress("UNCHECKED_CAST")
fun <T> Any.field(fieldName: String): T {
    return Reflections.getObjectField(this, fieldName) as T
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.fieldOrNull(fieldName: String): T? {
    return kotlin.runCatching { Reflections.getObjectField(this, fieldName) as T }
        .getOrElse { null }
}
