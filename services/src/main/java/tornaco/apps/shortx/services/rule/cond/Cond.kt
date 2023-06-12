package tornaco.apps.shortx.services.rule.cond

import android.content.Context
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext

interface Cond {
    val logger get() = Logger("Cond-${condClass().simpleName}")

    fun condClass(): Class<out Message>

    fun evaluate(param: CondParam): Boolean

    data class CondParam(
        val context: Context,
        val shortXContext: ShortXContext,
        val condition: ProtoAny,
        val contextData: ContextData
    )
}

interface CondProvider {
    val cond: Cond
}

enum class PrebuiltCond : CondProvider {
    CurrentPkgList {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.CurrentPkgList
    },
    True {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.True
    },
    False {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.False
    },
    MatchMVEL {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.MatchMVEL
    },
    EvaluateGlobalVar {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.EvaluateGlobalVar
    },
    BatteryPercent {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.BatteryPercent
    },
    AppIsRunning {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.AppIsRunning
    },

}

fun prebuiltCondFor(condition: ProtoAny): Cond? {
    return PrebuiltCond.values().firstOrNull { condition is_ it.cond.condClass() }?.cond
}