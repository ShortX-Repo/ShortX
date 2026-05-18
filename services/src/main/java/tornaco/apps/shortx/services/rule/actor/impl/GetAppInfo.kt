package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object GetAppInfo : Actor {
    private const val FRONT_PKG_NAME = "front"

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.GetAppInfo::class.java
    }

    override suspend fun handle(params: HandleParams): Actor.Res = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.GetAppInfo::class.java
        val cv = RuleContextAndVars(params)
        val pkgName = data.pkgName.compileContextAndVars(cv)
        val userId = data.userId.toString().compileContextAndVars(cv).toIntOrNull() ?: data.userId
        val appPkg = resolveAppPkg(pkgName, userId)
        val app = requireNotNull(shortXContext.appState.pkgToApp(appPkg)) {
            "App not found: ${appPkg.pkgName}, userId=${appPkg.userId}"
        }
        val isRunning = with(shortXContext.appState) { appPkg.isRunning() }

        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetAppInfo.appLabel.name)
                .fallbackOnEmpty { ContextDataMapping.GetAppInfo.appLabel.name },
            app.label
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetAppInfo.versionCode.name)
                .fallbackOnEmpty { ContextDataMapping.GetAppInfo.versionCode.name },
            app.versionCode.toString()
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetAppInfo.versionName.name)
                .fallbackOnEmpty { ContextDataMapping.GetAppInfo.versionName.name },
            app.versionName
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetAppInfo.isRunning.name)
                .fallbackOnEmpty { ContextDataMapping.GetAppInfo.isRunning.name },
            isRunning.toString()
        )

        Actor.Res(
            "${app.label} (${app.pkg.pkgName}), versionCode=${app.versionCode}, versionName=${app.versionName}, isRunning=$isRunning"
        )
    }

    private fun HandleParams.resolveAppPkg(pkgName: String, userId: Int): AppPkg {
        if (pkgName.equals(FRONT_PKG_NAME, ignoreCase = true)) {
            return requireNotNull(shortXContext.appState.currentFrontPkgs().firstOrNull()) {
                "No front app found"
            }
        }
        return AppPkg.newBuilder()
            .setPkgName(pkgName)
            .setUserId(userId)
            .build()
    }
}
