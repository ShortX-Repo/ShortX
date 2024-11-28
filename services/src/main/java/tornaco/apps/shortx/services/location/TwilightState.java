package tornaco.apps.shortx.services.location;

import android.text.format.DateFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * The twilight state, consisting of the sunrise and sunset times (in millis) for the current
 * period.
 * <p/>
 * Note: This object is immutable.
 */
public final class TwilightState {

    private final long mSunriseTimeMillis;
    private final long mSunsetTimeMillis;

    public TwilightState(long sunriseTimeMillis, long sunsetTimeMillis) {
        mSunriseTimeMillis = sunriseTimeMillis;
        mSunsetTimeMillis = sunsetTimeMillis;
    }

    /**
     * Returns the time (in UTC milliseconds from epoch) of the upcoming or previous sunrise if
     * it's night or day respectively.
     */
    public long sunriseTimeMillis() {
        return mSunriseTimeMillis;
    }

    /**
     * Returns a new {@link LocalDateTime} instance initialized to {@link #sunriseTimeMillis()}.
     */
    public LocalDateTime sunrise() {
        final ZoneId zoneId = TimeZone.getDefault().toZoneId();
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(mSunriseTimeMillis), zoneId);
    }

    /**
     * Returns the time (in UTC milliseconds from epoch) of the upcoming or previous sunset if
     * it's day or night respectively.
     */
    public long sunsetTimeMillis() {
        return mSunsetTimeMillis;
    }

    /**
     * Returns a new {@link LocalDateTime} instance initialized to {@link #sunsetTimeMillis()}.
     */
    public LocalDateTime sunset() {
        final ZoneId zoneId = TimeZone.getDefault().toZoneId();
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(mSunsetTimeMillis), zoneId);
    }

    /**
     * Returns {@code true} if it is currently night time.
     */
    public boolean isNight() {
        final long now = System.currentTimeMillis();
        return now >= mSunsetTimeMillis && now < mSunriseTimeMillis;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TwilightState && equals((TwilightState) o);
    }

    public boolean equals(TwilightState other) {
        return other != null
                && mSunriseTimeMillis == other.mSunriseTimeMillis
                && mSunsetTimeMillis == other.mSunsetTimeMillis;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(mSunriseTimeMillis) ^ Long.hashCode(mSunsetTimeMillis);
    }

    @Override
    public String toString() {
        return "TwilightState {"
                + " sunrise=" + DateFormat.format("MM-dd HH:mm", mSunriseTimeMillis)
                + " sunset=" + DateFormat.format("MM-dd HH:mm", mSunsetTimeMillis)
                + " }";
    }
}