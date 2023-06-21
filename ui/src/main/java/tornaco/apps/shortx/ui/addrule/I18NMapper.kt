package tornaco.apps.shortx.ui.addrule

import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON
import tornaco.apps.shortx.ui.base.PREFIX_MD_ICON

fun MutableList<String>.labelLinesFromAppsAndPkgSets(
    apps: List<App> = emptyList(),
    pkgSets: List<PkgSet> = emptyList()
): MutableList<String> {
    return apply {
        if (apps.isNotEmpty()) {
            addAll(apps.map {
                "${it.label}$PREFIX_APP_ICON${it.pkg.pkgName}"
            })
        } else if (pkgSets.isNotEmpty()) {
            addAll(pkgSets.map {
                "${it.label}${PREFIX_MD_ICON}${Remix.Document.folder_4_line}"
            })
        }
    }
}

fun MutableList<String>.labelLinesFromAppComponents(
    apps: List<AppComponent> = emptyList(),
): MutableList<String> {
    return apply {
        if (apps.isNotEmpty()) {
            addAll(apps.map {
                "${it.toComponentName().flattenToShortString()} $PREFIX_APP_ICON${it.pkg.pkgName}"
            })
        }
    }
}