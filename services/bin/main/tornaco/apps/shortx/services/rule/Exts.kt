package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.services.ShortXContext

fun List<String>.pkgSetIdsToPkgs(shortXContext: ShortXContext): List<AppPkg> = mapNotNull {
    shortXContext.pkgSet.getPkgSetByLabel(it)
}.flatMap { it.appPkgsList }