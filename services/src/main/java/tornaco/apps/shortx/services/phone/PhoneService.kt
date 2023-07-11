package tornaco.apps.shortx.services.phone

import android.annotation.SuppressLint
import android.telephony.CellInfo
import android.telephony.CellLocation
import android.telephony.TelephonyCallback
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.telephonyManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.runClearCallingIdentity


class PhoneService(shortXContext: ShortXContext) : SystemService(shortXContext, "PhoneService") {

    override fun systemReady() {
        super.systemReady()
    }

    @SuppressLint("MissingPermission")
    fun getCellInfos() {
        logger.d("getCellInfos")
        runClearCallingIdentity {
            context.telephonyManager.allCellInfo?.forEach {
                logger.p("cell: ${it.cellIdToString()}")
            }
        }
    }

    @SuppressLint("NewApi")
    private class TelephonyCallbackImpl : TelephonyCallback(),
        TelephonyCallback.CellLocationListener,
        TelephonyCallback.CellInfoListener {
        private val logger = Logger("TelephonyCallbackImpl")

        override fun onCellLocationChanged(location: CellLocation?) {
            location?.let {
                logger.p("onCellLocationChanged: $location")
            }
        }

        override fun onCellInfoChanged(cellInfo: List<CellInfo>?) {
            cellInfo?.let {
                logger.p("onCellInfoChanged: ${cellInfo.map { it }}")
            }
        }

    }
}


