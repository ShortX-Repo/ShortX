package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.util.PkgUtils

fun Context.requireExt(minVersion: Int = 5) {
    val extPkgInfo = PkgUtils.getPackageInfoAsUser(
        this,
        BuildPropInternal.SHORTX_EXT_APP_PKG_NAME,
        myUserId
    )
    requireNotNull(extPkgInfo) {
        "Pls install ShortX Ext. (请安装ShortX拓展包)"
    }
    require(extPkgInfo.versionCode >= minVersion) {
        "Pls update ShortX Ext to version: $minVersion (请升级ShortX拓展包至版本 $minVersion)"
    }
}