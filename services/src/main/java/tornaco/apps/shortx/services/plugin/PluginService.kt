package tornaco.apps.shortx.services.plugin

import tornaco.apps.shortx.core.plugin.PluginLoader
import tornaco.apps.shortx.core.proto.plugins.ShortXPlugin
import tornaco.apps.shortx.core.proto.plugins.ShortXPluginAction
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService

class PluginService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "PluginService") {

    private val loader: PluginLoader by lazy { PluginLoader(context) }

    fun getInstalledPlugins(): List<ShortXPlugin> {
        return loader.getInstalledPlugins()
    }

    fun getInstalledPluginActions(): List<ShortXPluginAction> {
        return loader.getInstalledPluginActions()
    }

    fun getInstalledPluginConditions(): List<ByteArrayWrapper> {
        TODO("Not yet implemented")
    }

    fun getInstalledPluginFacts(): List<ByteArrayWrapper> {
        TODO("Not yet implemented")
    }
}
