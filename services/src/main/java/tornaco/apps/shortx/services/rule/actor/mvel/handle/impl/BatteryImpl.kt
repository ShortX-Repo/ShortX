/*
 * (C) Copyright 2022 Thanox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package tornaco.apps.shortx.services.rule.actor.mvel.handle.impl

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.rule.actor.mvel.handle.BatteryState
import tornaco.apps.shortx.services.rule.actor.mvel.handle.IBattery

class BatteryImpl(private val context: Context) : IBattery {

    override fun getBatteryState(): BatteryState? {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }
        return batteryStatus?.let {
            val res = retrieveBatteryState(it)
            res
        }
    }

    companion object {
        private val logger = Logger("BatteryImpl")

        fun retrieveBatteryState(intent: Intent): BatteryState {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val charging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL

            // How are we charging?
            val chargePlug: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
            val wirelessCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS

            return BatteryState(
                batteryLevel = level,
                isCharging = charging,
                isUsbCharge = usbCharge,
                isAcCharge = acCharge,
                isPlug = usbCharge || acCharge || wirelessCharge
            )
        }
    }
}