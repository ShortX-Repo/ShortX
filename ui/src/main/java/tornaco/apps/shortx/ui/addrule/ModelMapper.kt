package tornaco.apps.shortx.ui.addrule

import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppCategory
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.shortXManager

fun Pair<List<AppPkg>, List<String>>.protoPkgsAndPkgSetsToModels(): Pair<List<App>, List<PkgSet>> {
    val apps = first.map { appPkg ->
        val pkg = Pkg(appPkg.pkgName, appPkg.userId)
        App(pkg = pkg, label = shortXManager.getAppLabel(pkg), AppCategory.User)
    }
    val pkgSets = second.mapNotNull { shortXManager.getPkgSetByLabel(it) }

    return apps to pkgSets
}