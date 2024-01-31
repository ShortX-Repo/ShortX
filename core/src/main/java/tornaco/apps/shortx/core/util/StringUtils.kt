package tornaco.apps.shortx.core.util

fun String?.fallbackOnEmpty(string: String): String {
    return if (this.isNullOrEmpty()) string else this
}

fun String?.fallbackOnEmpty(string: () -> String): String {
    return if (this.isNullOrEmpty()) string() else this
}


fun String.replaceLazy(
    oldValue: String,
    lazyNewValue: () -> String,
    ignoreCase: Boolean = false
): String {
    run {
        var occurrenceIndex: Int = indexOf(oldValue, 0, ignoreCase)
        // FAST PATH: no match
        if (occurrenceIndex < 0) return this

        val oldValueLength = oldValue.length
        val searchStep = oldValueLength.coerceAtLeast(1)

        val newValue = lazyNewValue()
        val newLengthHint = length - oldValueLength + newValue.length
        if (newLengthHint < 0) throw OutOfMemoryError()
        val stringBuilder = StringBuilder(newLengthHint)

        var i = 0
        do {
            stringBuilder.append(this, i, occurrenceIndex).append(newValue)
            i = occurrenceIndex + oldValueLength
            if (occurrenceIndex >= length) break
            occurrenceIndex = indexOf(oldValue, occurrenceIndex + searchStep, ignoreCase)
        } while (occurrenceIndex > 0)
        return stringBuilder.append(this, i, length).toString()
    }
}

fun String.maskAll(): String {
    return "*".repeat(this.length)
}

fun String.removeSpecialCharacters(): String {
    // 使用正则表达式匹配字母和数字，去除其他特殊字符
    return replace(Regex("[^a-zA-Z0-9]"), "")
}
