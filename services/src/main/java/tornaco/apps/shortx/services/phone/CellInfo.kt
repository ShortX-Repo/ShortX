package tornaco.apps.shortx.services.phone

import android.annotation.SuppressLint
import android.telephony.CellIdentityCdma
import android.telephony.CellIdentityGsm
import android.telephony.CellIdentityLte
import android.telephony.CellIdentityNr
import android.telephony.CellIdentityTdscdma
import android.telephony.CellIdentityWcdma
import android.telephony.CellInfo

@SuppressLint("NewApi")
fun CellInfo.cellIdToString(): String {
    return when (val cellId = cellIdentity) {
        is CellIdentityCdma -> {
            "CellIdentityCdma basestationId:${cellId.basestationId} - networkId: ${cellId.networkId} - systemId: ${cellId.systemId}"
        }

        is CellIdentityGsm -> {
            "CellIdentityGsm lac:${cellId.lac} - cid: ${cellId.cid}"
        }

        is CellIdentityLte -> {
            "CellIdentityLte mPci:${cellId.pci} - ci: ${cellId.ci}"
        }

        is CellIdentityNr -> {
            // New Radio 5G
            "CellIdentityNr nci:${cellId.nci}"
        }

        is CellIdentityTdscdma -> {
            "CellIdentityTdscdma cid:${cellId.cid}"
        }

        is CellIdentityWcdma -> {
            "CellIdentityWcdma cid:${cellId.cid}"
        }

        else -> {
            "Unknown-${cellId.javaClass.name}"
        }
    }

}