/*
 * Copyright (C) 2016 The Android Open Source Project
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

package tornaco.apps.shortx.services.location;

import static android.location.LocationManager.FUSED_PROVIDER;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.Calendar;
import android.location.LastLocationRequest;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Slog;

import com.android.internal.annotations.GuardedBy;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.TimeZone;

import tornaco.apps.shortx.core.BuildPropInternal;
import tornaco.apps.shortx.core.util.DateUtils;
import tornaco.apps.shortx.services.ShortXContext;
import tornaco.apps.shortx.services.SystemService;
import tornaco.apps.shortx.services.util.CalendarAstronomer;

/**
 * Figures out whether it's twilight time based on the user's location.
 * <p>
 * Used by the UI mode manager and other components to adjust night mode
 * effects based on sunrise and sunset.
 */
@SuppressWarnings({"ResultOfMethodCallIgnored", "NewApi"})
// API 30+
public final class TwilightService
        extends SystemService
        implements Handler.Callback, LocationListener {

    private static final String TAG = "ShortXTwilightService";
    private static final boolean DEBUG = BuildPropInternal.APP_IS_DEBUG;

    private static final int MSG_START_LISTENING = 1;
    private static final int MSG_STOP_LISTENING = 2;

    @GuardedBy("mListeners")
    private final ArrayMap<TwilightListener, Handler> mListeners = new ArrayMap<>();

    private final Handler mHandler;

    private LocationManager mLocationManager;

    private boolean mBootCompleted;
    private boolean mHasListeners;

    private BroadcastReceiver mTimeChangedReceiver;
    private Location mLastLocation;

    @GuardedBy("mListeners")
    private TwilightState mLastTwilightState;

    public final TwilightManager manager = new TwilightManager() {
        @Override
        public void registerListener(@NonNull TwilightListener listener,
                                     @NonNull Handler handler) {
            synchronized (mListeners) {
                final boolean wasEmpty = mListeners.isEmpty();
                mListeners.put(listener, handler);

                if (wasEmpty) {
                    mHandler.sendEmptyMessage(MSG_START_LISTENING);
                }
            }
        }

        @Override
        public void unregisterListener(@NonNull TwilightListener listener) {
            synchronized (mListeners) {
                final boolean wasEmpty = mListeners.isEmpty();
                mListeners.remove(listener);

                if (!wasEmpty && mListeners.isEmpty()) {
                    mHandler.sendEmptyMessage(MSG_STOP_LISTENING);
                }
            }
        }

        @Override
        public TwilightState getLastTwilightState() {
            synchronized (mListeners) {
                return mLastTwilightState;
            }
        }
    };

    public TwilightService(ShortXContext context) {
        super(context, "TwilightService");
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public void start(@NotNull Context context) {
        super.start(context.createAttributionContext(getServiceName()));
    }

    @Override
    public void systemReady() {
        super.systemReady();
        final Context c = getContext();
        mLocationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

        mBootCompleted = true;
        if (mHasListeners) {
            startListening();
        }
    }

    @SuppressWarnings("EnhancedSwitchMigration")
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_START_LISTENING:
                if (!mHasListeners) {
                    mHasListeners = true;
                    if (mBootCompleted) {
                        startListening();
                    }
                }
                return true;
            case MSG_STOP_LISTENING:
                if (mHasListeners) {
                    mHasListeners = false;
                    if (mBootCompleted) {
                        stopListening();
                    }
                }
                return true;
        }
        return false;
    }

    @SuppressLint({"MissingPermission"})
    private void startListening() {
        try {
            Slog.d(TAG, "startListening");

            // Start listening for location updates (default: low power, max 1h, min 10m).
            mLocationManager.requestLocationUpdates(
                    null /* default */, this, Looper.getMainLooper());

            // Request the device's location immediately if a previous location isn't available.
            if (mLocationManager.getLastKnownLocation(FUSED_PROVIDER, new LastLocationRequest.Builder()
                    .build()) == null) {
                if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    mLocationManager.getCurrentLocation(
                            LocationManager.NETWORK_PROVIDER, null, getContext().getMainExecutor(),
                            this::onLocationChanged);
                } else if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    mLocationManager.getCurrentLocation(
                            LocationManager.GPS_PROVIDER, null, getContext().getMainExecutor(),
                            this::onLocationChanged);
                }
            }

            // Update whenever the system clock is changed.
            if (mTimeChangedReceiver == null) {
                mTimeChangedReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Slog.d(TAG, "onReceive: " + intent);
                        updateTwilightState();
                    }
                };

                final IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_CHANGED);
                intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
                getContext().registerReceiver(mTimeChangedReceiver, intentFilter);
            }

            // Force an update now that we have listeners registered.
            updateTwilightState();
        } catch (Throwable e) {
            getShortXContext().getWatchDog().onFatalError(e, "startListening");
        }
    }

    private void stopListening() {
        try {
            Slog.d(TAG, "stopListening");

            if (mTimeChangedReceiver != null) {
                getContext().unregisterReceiver(mTimeChangedReceiver);
                mTimeChangedReceiver = null;
            }

            mLocationManager.removeUpdates(this);
            mLastLocation = null;
        } catch (Throwable e) {
            getShortXContext().getWatchDog().onFatalError(e, "stopListening");
        }
    }

    @SuppressLint("MissingPermission")
    private void updateTwilightState() {
        try {
            // Calculate the twilight state based on the current time and location.
            final long currentTimeMillis = System.currentTimeMillis();
            final Location location = mLastLocation != null ? mLastLocation
                    : mLocationManager.getLastLocation();
            final TwilightState state = calculateTwilightState(location, currentTimeMillis);
            if (DEBUG) {
                Slog.d(TAG, "updateTwilightState: " + state);
            }

            // Notify listeners if the state has changed.
            synchronized (mListeners) {
                if (!Objects.equals(mLastTwilightState, state)) {
                    mLastTwilightState = state;

                    for (int i = mListeners.size() - 1; i >= 0; --i) {
                        final TwilightListener listener = mListeners.keyAt(i);
                        final Handler handler = mListeners.valueAt(i);
                        handler.post(() -> {
                            try {
                                listener.onTwilightStateChanged(state);
                            } catch (Throwable e) {
                                getShortXContext().getWatchDog().onFatalError(e, "Call onTwilightStateChanged");
                            }
                        });
                    }
                }
            }
        } catch (Throwable e) {
            getShortXContext().getWatchDog().onFatalError(e, "updateTwilightState");
        }
    }

    @Override
    public void onLocationChanged(@Nullable Location location) {
        if (location != null) {
            Slog.d(TAG, "onLocationChanged:"
                    + " provider=" + location.getProvider()
                    + " accuracy=" + location.getAccuracy()
                    + " time=" + location.getTime());
            mLastLocation = location;
            updateTwilightState();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    /**
     * Calculates the twilight state for a specific location and time.
     *
     * @param location   the location to use
     * @param timeMillis the reference time to use
     * @return the calculated {@link TwilightState}, or {@code null} if location is {@code null}
     */
    private static TwilightState calculateTwilightState(Location location, long timeMillis) {
        if (location == null) {
            return null;
        }

        // Solution 2. test.
        if (DEBUG) {
            com.luckycatlabs.sunrisesunset.dto.Location dotLoc
                    = new com.luckycatlabs.sunrisesunset.dto.Location(location.getLatitude(), location.getLongitude());
            SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(dotLoc, TimeZone.getDefault());
            java.util.Calendar sunrise =
                    calculator.getOfficialSunriseCalendarForDate(java.util.Calendar.getInstance());
            java.util.Calendar sunset = calculator.getOfficialSunsetCalendarForDate(java.util.Calendar.getInstance());
            Slog.d(TAG, "SunriseSunsetCalculator sunrise-" + sunrise.getTime() + ", sunset-" + sunset.getTime());
        }


        final CalendarAstronomer ca = new CalendarAstronomer(
                location.getLongitude(), location.getLatitude());

        final Calendar noon = Calendar.getInstance();
        noon.setTimeInMillis(timeMillis);
        noon.set(Calendar.HOUR_OF_DAY, 12);
        noon.set(Calendar.MINUTE, 0);
        noon.set(Calendar.SECOND, 0);
        noon.set(Calendar.MILLISECOND, 0);
        ca.setTime(noon.getTimeInMillis());
        Slog.d(TAG, "Current time: " + ca.getDate());
        Slog.d(TAG, "Current time format: " + DateUtils.formatLong(timeMillis));

        long sunriseTimeMillis = ca.getSunRiseSet(true /* rise */);
        long sunsetTimeMillis = ca.getSunRiseSet(false /* rise */);

        // 日落以后了，晚上了，更新日出时间+1
        if (sunsetTimeMillis < timeMillis) {
            Slog.d(TAG, "日落以后了");
            noon.add(Calendar.DATE, 1);
            ca.setTime(noon.getTimeInMillis());
            sunriseTimeMillis = ca.getSunRiseSet(true /* rise */);
        } else if (sunriseTimeMillis > timeMillis) {
            Slog.d(TAG, "日出之后，日落之前");
            // 日出之后，日落之前，
            noon.add(Calendar.DATE, -1);
            ca.setTime(noon.getTimeInMillis());
            sunsetTimeMillis = ca.getSunRiseSet(false /* rise */);
        } else {
            Slog.d(TAG, "日出之前");
        }

        return new TwilightState(sunriseTimeMillis, sunsetTimeMillis);
    }
}