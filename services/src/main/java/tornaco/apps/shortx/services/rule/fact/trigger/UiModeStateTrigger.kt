package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.DarkModeStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.SettingsImpl

class UiModeStateTrigger(
    override val context: Context, override val shortXContext: ShortXContext
) : Trigger {
    private val logger = Logger("UiModeStateTrigger")
    private val settings = SettingsImpl(context)

    private var darkModeEnabled = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action?.let {
                if (it == Intent.ACTION_CONFIGURATION_CHANGED) {
                    val isDarkMode = settings.isDarkModeEnabled
                    if (isDarkMode != darkModeEnabled) {
                        darkModeEnabled = isDarkMode
                        logger.d("Config changed, isDarkMode: $isDarkMode")
                        shortXContext.rule.publishFact(
                            "DarkMode changed",
                            DarkModeStatusChanged.newBuilder()
                                .setOoa(isDarkMode.select(OnOffAny.On, OnOffAny.Off))
                                .build()
                        )
                    }
                }
            }
        }
    }


    override fun register() {
        darkModeEnabled = settings.isDarkModeEnabled

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED)
        context.registerReceiver(receiver, filter)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }
}