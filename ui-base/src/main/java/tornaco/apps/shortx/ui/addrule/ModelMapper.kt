package tornaco.apps.shortx.ui.addrule

import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.shortXManager

fun Pair<List<AppPkg>, List<String>>.protoPkgsAndPkgSetsToModels(): Pair<List<App>, List<PkgSet>> {
    val apps = first.mapNotNull { appPkg ->
        shortXManager.pkgToApp(appPkg)
    }
    val pkgSets = second.map {
        shortXManager.getPkgSetByLabel(it) ?: PkgSet.newBuilder().setLabel(it).build()
    }
    return apps to pkgSets
}