package tornaco.apps.shortx.services.os.cpu

import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.os.cpu.CpuAvailabilityMonitoringConfig.CPUSET_ALL
import tornaco.apps.shortx.services.os.cpu.CpuMonitorInternal.CpuAvailabilityCallback
import java.util.concurrent.Executors


class CpuService(shortXContext: ShortXContext) : SystemService(shortXContext, "CpuService") {
    private val cpuMonitorService by lazy {
        CpuMonitorService(context)
    }

    override fun systemReady() {
        super.systemReady()

        if (OsUtils.isQOrAbove()) {
            cpuMonitorService.start(context)
            cpuMonitorService.localService.addCpuAvailabilityCallback(
                Executors.newSingleThreadExecutor(),
                CpuAvailabilityMonitoringConfig.Builder(CPUSET_ALL)
                    .addThreshold(20)
                    .addThreshold(80)
                    .build(),
                object : CpuAvailabilityCallback {
                    override fun onAvailabilityChanged(info: CpuAvailabilityInfo) {
                        logger.d("onAvailabilityChanged: $info")
                    }

                    override fun onMonitoringIntervalChanged(intervalMilliseconds: Long) {
                        logger.d("onMonitoringIntervalChanged: $intervalMilliseconds")
                    }
                }
            )
        }
    }

}