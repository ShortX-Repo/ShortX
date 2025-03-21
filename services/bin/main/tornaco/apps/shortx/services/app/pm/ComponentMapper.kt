package tornaco.apps.shortx.services.app.pm

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.content.pm.ServiceInfo
import tornaco.apps.shortx.core.proto.common.AppComponentInfo
import tornaco.apps.shortx.core.toAppComponent
import tornaco.apps.shortx.core.util.PkgUtils

object ComponentMapper {

    @JvmStatic
    fun serviceInfoToAppComponentInfo(
        userId: Int,
        androidPm: PackageManager,
        serviceInfo: ServiceInfo,
    ): AppComponentInfo {
        return AppComponentInfo.newBuilder()
            .setComponent(PkgUtils.getComponentName(serviceInfo).toAppComponent(userId))
            .setLabel(serviceInfo.loadLabel(androidPm).toString())
            .setEnableSetting(
                PMSFuncs.getComponentEnabledSetting(
                    userId,
                    PkgUtils.getComponentName(serviceInfo)
                )
            )
            .build()
    }

    @JvmStatic
    fun activityInfoToAppComponentInfo(
        userId: Int,
        androidPm: PackageManager,
        activityInfo: ActivityInfo,
    ): AppComponentInfo {
        return AppComponentInfo.newBuilder()
            .setComponent(PkgUtils.getComponentName(activityInfo).toAppComponent(userId))
            .setLabel(activityInfo.loadLabel(androidPm).toString())
            .setEnableSetting(
                PMSFuncs.getComponentEnabledSetting(
                    userId,
                    PkgUtils.getComponentName(
                        activityInfo
                    )
                )
            )
            .build()
    }

    @JvmStatic
    fun providerInfoToAppComponentInfo(
        userId: Int,
        androidPm: PackageManager,
        providerInfo: ProviderInfo,
    ): AppComponentInfo {
        return AppComponentInfo.newBuilder()
            .setComponent(PkgUtils.getComponentName(providerInfo).toAppComponent(userId))
            .setLabel(providerInfo.loadLabel(androidPm).toString())
            .setEnableSetting(
                PMSFuncs.getComponentEnabledSetting(
                    userId,
                    PkgUtils.getComponentName(
                        providerInfo
                    )
                )
            )
            .build()
    }
}