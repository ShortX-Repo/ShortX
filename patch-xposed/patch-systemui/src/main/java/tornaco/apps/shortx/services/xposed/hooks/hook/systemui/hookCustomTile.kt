package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.services.xposed.hooks.hook.afterMethod
import tornaco.apps.shortx.services.xposed.hooks.hook.api.IPackageLoaded
import util.Reflections
private val shortx by lazy { shortXManager }
private val tileNumberRegex = Regex("Tile(\\d+)")
fun SystemUIHook.hookCustomTile(lpparam: IPackageLoaded.Param) = runCatching {
    if (PackageNames.systemUI == lpparam.packageName) {
        afterMethod(
            clazz = Reflections.findClass(
                "com.android.systemui.qs.external.CustomTile",
                lpparam.classLoader
            ),
            methodName = "getTileLabel"
        ) { param ->
            logger.logOnDebug("CustomTile.getTileLabel: ${param.args.map { it }}")
            val customTile = param.thisObject
            val state = Reflections.getObjectField(customTile,"mState")
            logger.d("CustomTile.getTileLabel state: $state")
            if (state == null) return@afterMethod
            val spec = Reflections.getObjectField(state,"spec") ?: return@afterMethod
            logger.d("CustomTile.getTileLabel spec: $spec")
            // spec=custom(tornaco.apps.shortx/.ui.service.tile.Tile7)
            val specStr = spec.toString()
            if (!specStr.contains("tornaco.apps.shortx")) return@afterMethod
            val tileWithNumber = tileNumberRegex.find(specStr)?.groupValues?.get(1)
            // Tile 1
            // Tile 2..
            val number = tileWithNumber?.toIntOrNull() ?: return@afterMethod
            logger.d("CustomTile.getTileLabel number: $number")
            val sbtOrNull = StatusBarTile.forNumber(number)
            logger.d("CustomTile.getTileLabel sbtOrNull: $sbtOrNull")
            if (sbtOrNull != null) {
                val overlayLabel =
                    shortx.getStatusBarTileSettingsByNumber(sbtOrNull.number).label
                logger.d("CustomTile.getTileLabel getLabel for number: $number, overlayLabel?: $overlayLabel")
                if (overlayLabel != null) {
                    param.result = overlayLabel
                }
            }
        }
    }
}.onFailure {
    logger.e(it, "hookCustomTile")
}