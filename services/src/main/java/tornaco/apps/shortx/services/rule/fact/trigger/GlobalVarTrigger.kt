package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.proto.fact.GlobalVarChanged
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.services.ShortXContext

class GlobalVarTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("GlobalVarTrigger")

    override fun register() {
        shortXContext.rule.registerGlobalVarObs(
            object : IGlobalVarObserver.Stub() {
                override fun onAddOrUpdate(id: String) {
                    logger.d("Var changed: $id")

                    shortXContext.rule.publishFact(
                        "Global changed",
                        GlobalVarChanged.newBuilder()
                            .setGvId(id)
                            .build()
                    )
                }

                override fun onDelete(id: String?) {
                }
            }
        )
    }

    override fun unRegister() {

    }
}