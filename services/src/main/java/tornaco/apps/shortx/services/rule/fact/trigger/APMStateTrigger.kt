package tornaco.apps.shortx.services.rule.fact.trigger

import android.annotation.SuppressLint
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.Settings
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.APMStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.cond.impl.isAPMModeEnabled

class APMStateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) : Trigger {
    private val logger = Logger("APMStateTrigger")
    private val contentObs =
        object : ContentObserver(newHandlerOfNewThread("APMStateTrigger")) {
            @SuppressLint("MissingPermission")
            override fun onChange(selfChange: Boolean, uri: Uri) {
                super.onChange(selfChange, uri)
                logger.d("onChange: $uri")
                val isAPMEnabled = context.isAPMModeEnabled()
                shortXContext.rule.publishFact(
                    "APM state change",
                    APMStatusChanged.newBuilder()
                        .setOoa(isAPMEnabled.select(OnOffAny.On, OnOffAny.Off))
                        .build()
                ) { emptyMap() }
            }
        }

    override fun register() {
        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.AIRPLANE_MODE_ON),
            true,
            contentObs
        )
    }

    override fun unRegister() {
        context.contentResolver.unregisterContentObserver(contentObs)
    }
}