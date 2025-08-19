package tornaco.apps.shortx.core

import android.content.ComponentName
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppPkg

val unknownAppComponent = AppComponent(unKnownPkg, "n/a")

fun AppComponent(
    pkg: AppPkg,
    className: String
): AppComponent = AppComponent.newBuilder().setClassName(className).setPkg(pkg).build()

fun AppComponent.toComponentName() = ComponentName(pkg.pkgName, className)

fun ComponentName.toAppComponent(userId: Int) = AppComponent.newBuilder()
    .setPkg(AppPkg.newBuilder().setUserId(userId).setPkgName(packageName))
    .setClassName(className)
    .build()

fun AppComponent.appComponentToString() =
    "${this.pkg.pkgName}-${this.className} u${this.pkg.userId}"