@file:Suppress("FunctionName")

package tornaco.apps.shortx.core

import android.content.pm.ApplicationInfo
import android.os.UserHandle
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppCategory
import tornaco.apps.shortx.core.proto.common.AppPkg

val unKnownPkg = Pkg("unknown", 0)

fun Pkg(pkgName: String, userId: Int = UserHandle.myUserId()): AppPkg =
    AppPkg.newBuilder().setPkgName(pkgName).setUserId(userId).build()

fun App(pkg: AppPkg, label: String, category: AppCategory): App =
    App.newBuilder().setCategory(category).setLabel(label)
        .setPkg(pkg).build()

fun App(
    pkg: AppPkg,
    label: String,
    category: AppCategory,
    uid: Int,
    isGame: Boolean,
    isXposedMode: Boolean,
    isDisabled: Boolean,
    isSuspend: Boolean,
    isShortcutStub: Boolean,
    versionCode: Long,
    versionName: String
): App =
    App.newBuilder()
        .setCategory(category)
        .setLabel(label)
        .setUid(uid)
        .setPkg(pkg)
        .setIsGame(isGame)
        .setIsXposedMod(isXposedMode)
        .setIsDisabled(isDisabled)
        .setIsSuspend(isSuspend)
        .setIsShortcutStub(isShortcutStub)
        .setVersionCode(versionCode)
        .setVersionName(versionName)
        .build()

fun ApplicationInfo.toAppPkg(): AppPkg {
    return AppPkg.newBuilder()
        .setPkgName(this.packageName)
        .setUserId(UserHandle.getUserId(this.uid))
        .build()
}