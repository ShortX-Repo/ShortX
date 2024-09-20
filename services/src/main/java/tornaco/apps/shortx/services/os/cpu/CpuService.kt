package tornaco.apps.shortx.services.os.cpu

import android.util.SparseArray
import tornaco.apps.shortx.core.BuildPropExt
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.os.cpu.CpuInfoReader.CpuInfo


class CpuService(shortXContext: ShortXContext) : SystemService(shortXContext, "CpuService") {
    private val cpuInfoReader by lazy { CpuInfoReader() }

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

        BuildPropExt.onDebugBuild {
            logger.d(readCpuInfos())
        }
    }


    fun readCpuInfos(): SparseArray<CpuInfo>? = cpuInfoReader.readCpuInfos()
}