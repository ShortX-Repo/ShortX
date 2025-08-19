package tornaco.apps.shortx.core.proto

import tornaco.apps.shortx.core.proto.common.TimeUnit

fun convertToMillis(value: Long, unit: TimeUnit): Long {
    return when (unit) {
        TimeUnit.TimeUnit_MS -> value
        TimeUnit.TimeUnit_S -> value * 1000
        TimeUnit.TimeUnit_M -> value * 60 * 1000
        TimeUnit.TimeUnit_H -> value * 60 * 60 * 1000
        TimeUnit.TimeUnit_D -> value * 24 * 60 * 60 * 1000
        TimeUnit.UNRECOGNIZED -> value // 默认返回原始值，也可以改为抛异常
    }
}
