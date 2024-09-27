package tornaco.apps.shortx.services.rule.fact.trigger

import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug


abstract class KeyPressTimesInIntervalListener(
    private val targetPressCount: Int,
    private val timeIntervalMillis: Long
) {
    private val logger = Logger("KeyPressTimesInIntervalListener")

    private var keyPressCount = 0
    private var startTime: Long = 0

    fun onKeyPressed() {
        val currentTime = System.currentTimeMillis()
        // 检查是否在规定时间内
        if (currentTime - startTime > timeIntervalMillis) {
            keyPressCount = 1
            startTime = currentTime
        } else {
            keyPressCount++

            logger.d("keyPressCount +1 ---> $keyPressCount")

            // 如果在规定时间内达到了指定按键次数，发送消息通知
            if (keyPressCount >= targetPressCount) {
                keyPressCount = 0 // 重置计数
                keyPressCountHit()
            }
        }
    }

    fun onOtherKeyPressed() {
        keyPressCount = 0
        startTime = 0
    }

    abstract fun keyPressCountHit()
}