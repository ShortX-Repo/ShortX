package tornaco.apps.shortx.core

import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.UserComponent

val unknownUserComponent = UserComponent(unKnownPkg, "n/a")

fun UserComponent(
    pkg: AppPkg,
    className: String
): UserComponent = UserComponent.newBuilder().setClassName(className).setPkg(pkg).build()