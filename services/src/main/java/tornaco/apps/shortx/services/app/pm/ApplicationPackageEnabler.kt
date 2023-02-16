package tornaco.apps.shortx.services.app.pm

import tornaco.apps.shortx.core.proto.common.AppPkg

interface ApplicationPackageEnabler {
    fun enable(pkg: AppPkg)
    fun disable(pkg: AppPkg)
    fun isEnabled(pkg: AppPkg): Boolean
}