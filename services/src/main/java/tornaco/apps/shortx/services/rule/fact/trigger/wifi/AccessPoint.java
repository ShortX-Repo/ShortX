/*
 * Copyright (C) 2015 The Android Open Source Project
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
 */

package tornaco.apps.shortx.services.rule.fact.trigger.wifi;

import android.annotation.IntDef;
import android.annotation.Nullable;
import android.content.Context;
import android.net.ScoredNetwork;
import android.net.wifi.ScanResult;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a selectable Wifi Network for use in various wifi selection menus backed by
 * {@link WifiStatusTracker}.
 *
 * <p>An AccessPoint, which would be more fittingly named "WifiNetwork", is an aggregation of
 * {@link ScanResult ScanResults} along with pertinent metadata (e.g. current connection info,
 * network scores) required to successfully render the network to the user.
 */
public class AccessPoint {

    /**
     * Return the speed label for a {@link ScoredNetwork} at the specified {@code rssi} level.
     */
    @Nullable
    public static String getSpeedLabel(Context context, ScoredNetwork scoredNetwork, int rssi) {
        return getSpeedLabel(context, roundToClosestSpeedEnum(scoredNetwork.calculateBadge(rssi)));
    }

    private static String getSpeedLabel(Context context, int speed) {
        switch (speed) {
            case Speed.VERY_FAST:
                return "VF";
            case Speed.FAST:
                return "F";
            case Speed.MODERATE:
                return "OK";
            case Speed.SLOW:
                return "SL";
            case Speed.NONE:
            default:
                return null;
        }
    }


    @Nullable
    @Speed
    private static int roundToClosestSpeedEnum(int speed) {
        if (speed < Speed.SLOW) {
            return Speed.NONE;
        } else if (speed < (Speed.SLOW + Speed.MODERATE) / 2) {
            return Speed.SLOW;
        } else if (speed < (Speed.MODERATE + Speed.FAST) / 2) {
            return Speed.MODERATE;
        } else if (speed < (Speed.FAST + Speed.VERY_FAST) / 2) {
            return Speed.FAST;
        } else {
            return Speed.VERY_FAST;
        }
    }

    @IntDef({Speed.NONE, Speed.SLOW, Speed.MODERATE, Speed.FAST, Speed.VERY_FAST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Speed {
        /**
         * Constant value representing an unlabeled / unscored network.
         */
        int NONE = 0;
        /**
         * Constant value representing a slow speed network connection.
         */
        int SLOW = 5;
        /**
         * Constant value representing a medium speed network connection.
         */
        int MODERATE = 10;
        /**
         * Constant value representing a fast speed network connection.
         */
        int FAST = 20;
        /**
         * Constant value representing a very fast speed network connection.
         */
        int VERY_FAST = 30;
    }
}