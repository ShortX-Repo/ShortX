package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtra
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.systemServiceScope


object PostNotification : Actor {
    private const val PARAMS_ID = "paramsId"
    private val visibleParams = mutableMapOf<String, HandleParams>()

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.PostNotification::class.java
    }

    override fun init(androidContext: Context, shortXContext: ShortXContext) {
        super.init(androidContext, shortXContext)
        shortXContext.rule.registerGlobalVarObs(object : IGlobalVarObserver.Stub() {
            override fun onAddOrUpdate(id: String?) {
                shortXContext.notification.systemServiceScope.launch {
                    logger.d("Global var changed: $id")
                    id?.let {
                        val gv = shortXContext.rule.getGlobalVarByName(id)
                        gv?.let {
                            updateN(shortXContext, gv)
                        }
                    }
                }
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

            val paramsId = evaluateContext.srcId + "-" + d.id

            val extras = listOf(d.title, d.message, d.overrideAppName).mapNotNull {
                if (!it.contains(GLOBAL_VAR_REF_PREFIX)) null
                else {
                    AndroidIntentExtra.newBuilder().setKey(it).setValue("ShortX").build()
                }
            }
            logger.p("extras: $extras")

            visibleParams[paramsId] = params

            val inflatedN =
                PostNotification.newBuilder(d)
                    .setTitle(inflatedTitle)
                    .setMessage(inflatedMessage)
                    .setOverrideAppName(inflatedAppName)
                    .addAllExtras(extras)
                    .addExtras(
                        AndroidIntentExtra.newBuilder().setKey(PARAMS_ID).setValue(paramsId).build()
                    )
                    .build()
            shortXContext.notification.post(inflatedN)
        }
    }

    private suspend fun updateN(shortXContext: ShortXContext, globalVar: GlobalVar) {
        logger.d("updateN for gv: ${globalVar.name}")
        val globalVarNotifications =
            shortXContext.notification.getActiveNotifications().mapNotNull { notification ->
                val extras = notification.extrasList
                val params = extras.firstOrNull { it.key == PARAMS_ID }

                if (params == null) {
                    null
                } else {
                    val isGV = notification.extrasList.any {
                        it.key.contains(
                            "${GLOBAL_VAR_REF_PREFIX}${globalVar.name}"
                        )
                    }
                    if (isGV) {
                        params.value
                    } else {
                        null
                    }
                }

            }

        logger.d("globalVarNotifications: $globalVarNotifications")

        globalVarNotifications.forEach { n ->
            visibleParams[n]?.let {
                handle(it)
            }
        }
    }
}