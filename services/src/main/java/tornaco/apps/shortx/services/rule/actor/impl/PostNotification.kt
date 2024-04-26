package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtra
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object PostNotification : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.PostNotification::class.java
    }

    override fun init(androidContext: Context, shortXContext: ShortXContext) {
        super.init(androidContext, shortXContext)
        shortXContext.rule.registerGlobalVarObs(object : IGlobalVarObserver.Stub() {
            override fun onAddOrUpdate(id: String?) {
                logger.d("Global var changed: $id")
                updateN(shortXContext)
            }

            override fun onDelete(id: String?) {
            }
        })
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.Main) {
            val d = action unpack_ PostNotification::class.java
            val cv = RuleContextAndVars(params)
            val inflatedTitle = d.title.compileContextAndVars(cv)
            val inflatedMessage = d.message.compileContextAndVars(cv)
            val inflatedAppName = d.overrideAppName.compileContextAndVars(cv)

            val extras = listOf(d.title, d.message, d.overrideAppName).mapNotNull {
                if (!it.contains(GLOBAL_VAR_REF_PREFIX)) null
                else {
                    AndroidIntentExtra.newBuilder().setKey(it).setValue("ShortX").build()
                }
            }
            logger.p("extras: $extras")

            val inflatedN =
                PostNotification.newBuilder(d)
                    .setTitle(inflatedTitle)
                    .setMessage(inflatedMessage)
                    .setOverrideAppName(inflatedAppName)
                    .addAllExtras(extras)
                    .build()
            shortXContext.notification.post(inflatedN)
        }
    }

    private fun updateN(shortXContext: ShortXContext) {
        val globalVarNotifications =
            shortXContext.notification.getActiveNotifications().filter { notification ->
                notification.ongoing && notification.extrasList.any {
                    it.key.contains(
                        GLOBAL_VAR_REF_PREFIX
                    )
                }
            }
        logger.d(globalVarNotifications)
    }
}