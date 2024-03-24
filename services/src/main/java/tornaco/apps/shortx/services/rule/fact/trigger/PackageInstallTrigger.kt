package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.AppAdded
import tornaco.apps.shortx.core.proto.fact.AppRemoved
import tornaco.apps.shortx.core.proto.fact.AppUpdated
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext

class PackageInstallTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) : Trigger {
    private val logger = Logger("PackageInstallTrigger")

    override fun register() {
        shortXContext.appState.onPackageAdded {
            logger.d("onPackageAdded: $it")
            shortXContext.rule.publishFact(
                "onPackageAdded",
                AppAdded.newBuilder().build(),
                mapOf(
                    ContextDataMapping.AppAdded.userId.name to it.pkg.userId,
                    ContextDataMapping.AppAdded.pkgName.name to it.pkg.pkgName,
                    ContextDataMapping.AppAdded.label.name to it.label,
                )
            )
        }

        shortXContext.appState.onPackageRemoved {
            logger.d("onPackageRemoved: $it")
            shortXContext.rule.publishFact(
                "onPackageRemoved",
                AppRemoved.newBuilder().addAllApps(listOf(it.pkg)).build(),
                mapOf(
                    ContextDataMapping.AppRemoved.userId.name to it.pkg.userId,
                    ContextDataMapping.AppRemoved.pkgName.name to it.pkg.pkgName,
                    ContextDataMapping.AppRemoved.label.name to it.label,
                )
            )
        }

        shortXContext.appState.onPackageUpdated { from, to ->
            logger.d("onPackageUpdated: $from ---> $to")
            shortXContext.rule.publishFact(
                "onPackageUpdated",
                AppUpdated.newBuilder().addAllApps(listOf(to.pkg)).build()
            ) {
                mapOf(
                    ContextDataMapping.AppUpdated.userId.name to to.pkg.userId,
                    ContextDataMapping.AppUpdated.pkgName.name to to.pkg.pkgName,
                    ContextDataMapping.AppUpdated.label.name to to.label,
                    ContextDataMapping.AppUpdated.fromVersionCode.name to from.versionCode,
                    ContextDataMapping.AppUpdated.toVersionCode.name to to.versionCode,
                )
            }
        }
    }

    override fun unRegister() {
    }
}