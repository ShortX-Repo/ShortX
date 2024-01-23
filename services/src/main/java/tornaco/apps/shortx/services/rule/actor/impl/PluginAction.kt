package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import com.google.protobuf.Message
import dalvik.system.PathClassLoader
import shortx.plugin.Intents
import shortx.plugin.ParamsData
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File

object PluginAction : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.PluginAction::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.PluginAction::class.java
        logger.i("execute plugin action: ${d.pluginId}")
        val paramsData = d.params.let { wrapper ->
            ParamsData.newBuilder()
                .putAllStringData(wrapper.stringDataMap)
                .putAllIntData(wrapper.intDataMap)
                .putAllLongData(wrapper.longDataMap)
                .putAllFloatData(wrapper.floatDataMap)
                .putAllBoolData(wrapper.boolDataMap)
                .putAllStringListData(wrapper.stringListDataMap.mapValues {
                    shortx.plugin.StringList.newBuilder().addAllString(it.value.stringList).build()
                })
                // TODO Add More.
                .build()
        }
        val pluginId = d.pluginId
        val actionEvaluatorIns =
            requireNotNull(getPluginActionEvaluatorObject(pluginId, androidContext)) {
                "Can not load plugin action evaluator object."
            }
        val actionEvaluator = actionEvaluatorIns as shortx.plugin.ActionEvaluator
        actionEvaluator.evaluate(
            shortx.plugin.ActionEvaluator.Input(
                androidContext,
                emptyMap(),
                paramsData
            )
        )
    }

    private fun getPluginActionEvaluatorObject(pkgName: String, context: Context): Any? {
        val appInfo = requireNotNull(
            PkgUtils.getApplicationInfo(
                context,
                pkgName
            )
        ) { "Plugin not installed: $pkgName" }

        val entry: String? = appInfo.metaData.getString(Intents.META_DATA_ACTION_EVALUATOR_ENTRY)
        require(!entry.isNullOrBlank()) {
            "Can not find plugin action evaluator, message to dev: did you add meta data to your AndroidManifest.xml?"
        }

        val apkPath: File = requireNotNull(PkgUtils.getApkPath(pkgName)) {
            "Apk path not found for: $pkgName"
        }
        logger.d("Apk path $apkPath")
        return getPluginActionEvaluatorObject(apkPath, entry, context)
    }

    private fun getPluginActionEvaluatorObject(
        apkPath: File,
        className: String,
        context: Context
    ): Any? {
        logger.i("getPluginActionEvaluatorObject: $className from apk: $apkPath")
        val appCl: ClassLoader = PathClassLoader(
            apkPath.absolutePath, PluginAction::class.java.classLoader
        )
        val overlayClass = appCl.loadClass(className)
        logger.d("overlayClass: $overlayClass")
        val constructor = overlayClass.getConstructor()
        return constructor.newInstance()
    }
}