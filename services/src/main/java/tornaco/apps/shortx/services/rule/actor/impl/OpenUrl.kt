package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.google.protobuf.Message
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.UserHandleUtils
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object OpenUrl : Actor {

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.OpenUrl::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val openUrl =
            action unpack_ tornaco.apps.shortx.core.proto.action.OpenUrl::class.java
        val cv = RuleContextAndVars(params)
        val url = openUrl.url.compileContextAndVars(cv)
        val browserPkg = openUrl.browserPkg.compileContextAndVars(cv).ifBlank {
            resolveDefaultBrowser(params.androidContext)
        }

        logger.d("openUrl: $url, browserPkg: $browserPkg")
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (browserPkg.isNotBlank()) {
            intent.setPackage(browserPkg)
        }
        androidContext.startActivityAsUser(
            intent,
            UserHandleUtils.userIdToUserHandle(0)
        )
        Actor.Res.EMPTY
    }
}

fun resolveDefaultBrowser(context: android.content.Context): String {
    val pm = context.packageManager
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"))
    val resolveInfo = pm.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
    val defaultPkg = resolveInfo?.activityInfo?.packageName.orEmpty()
    if (defaultPkg.isNotBlank() && defaultPkg != BuildPropInternal.APP_PACKAGE_NAME) {
        return defaultPkg
    }
    val allBrowsers = pm.queryIntentActivities(browserIntent, PackageManager.MATCH_ALL)
    return allBrowsers.firstOrNull { it.activityInfo.packageName != BuildPropInternal.APP_PACKAGE_NAME }
        ?.activityInfo?.packageName.orEmpty()
}
