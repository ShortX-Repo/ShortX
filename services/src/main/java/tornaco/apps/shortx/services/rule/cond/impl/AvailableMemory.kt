package tornaco.apps.shortx.services.rule.cond.impl

import android.app.ActivityManager
import android.content.Context
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.IntOp
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object AvailableMemory : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.AvailableMemory::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val currentAvailableMb = currentAvailableMemoryMb(context)
            val availableMemory =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.AvailableMemory::class.java
            val res = matchIntOp(
                op = availableMemory.op ?: IntOp.UNRECOGNIZED,
                currentValue = currentAvailableMb,
                expectedValue = availableMemory.valueMb
            )

            if (availableMemory.isInvert) {
                !res
            } else {
                res
            }.toResult("Current value: ${currentAvailableMb}MB")
        }
    }

    private fun currentAvailableMemoryMb(context: Context): Int {
        val activityManager = context.getSystemService(ActivityManager::class.java)
            ?: error("Fail get ActivityManager")
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return (memoryInfo.availMem / (1024L * 1024L)).toInt()
    }
}