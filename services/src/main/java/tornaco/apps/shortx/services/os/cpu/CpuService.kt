package tornaco.apps.shortx.services.os.cpu

import android.util.SparseArray
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.os.cpu.CpuAvailabilityMonitoringConfig.CPUSET_ALL
import tornaco.apps.shortx.services.os.cpu.CpuAvailabilityMonitoringConfig.CPUSET_BACKGROUND
import tornaco.apps.shortx.services.os.cpu.CpuInfoReader.CpuInfo
import tornaco.apps.shortx.services.os.cpu.CpuMonitorInternal.CpuAvailabilityCallback

val MONITORING_CONFIG_ALL_CPUSET = CpuAvailabilityMonitoringConfig.Builder(
    CPUSET_ALL
).apply {
    arrayOf(10, 20, 30, 40, 50, 60, 70, 80, 90).forEach {
        addThreshold(it)
    }
}.build()

val MONITORING_CONFIG_BG_CPUSET = CpuAvailabilityMonitoringConfig.Builder(
    CPUSET_BACKGROUND
).apply {
    arrayOf(10, 20, 30, 40, 50, 60, 70, 80, 90).forEach {
        addThreshold(it)
    }
}.build()

class CpuService(shortXContext: ShortXContext) : SystemService(shortXContext, "CpuService") {
    private val cpuInfoReader by lazy { CpuInfoReader() }
    private val cupMonitorService: CpuMonitorService by lazy { CpuMonitorService(context) }

    override fun systemReady() {
        super.systemReady()
        // Initialize CPU info reader and perform the first read to make sure the CPU stats are
        // readable without any issues.
        if (!cpuInfoReader.init() || cpuInfoReader.readCpuInfos() == null) {
            logger.e(
                "Failed to initialize CPU info reader. This happens when the CPU "
                        + "frequency stats are not available or the sysfs interface has changed in "
                        + "the Kernel. Cannot monitor CPU without these stats. Terminating CPU monitor "
                        + "service"
            )
        }


        cupMonitorService.start(context)

        if (BuildPropInternal.APP_IS_DEBUG) {
            logger.logOnDebug { readCpuInfos() }

            cupMonitorService.localService.addCpuAvailabilityCallback(
                null,
                MONITORING_CONFIG_ALL_CPUSET,
                object : CpuAvailabilityCallback {
                    override fun onAvailabilityChanged(info: CpuAvailabilityInfo?) {
                        logger.d("onAvailabilityChanged: $info")
                    }

                    override fun onMonitoringIntervalChanged(intervalMilliseconds: Long) {
                        logger.d("onMonitoringIntervalChanged, intervalMilliseconds: $intervalMilliseconds")
                    }
                })
        }
    }


    fun readCpuInfos(): SparseArray<CpuInfo>? = cpuInfoReader.readCpuInfos()
}