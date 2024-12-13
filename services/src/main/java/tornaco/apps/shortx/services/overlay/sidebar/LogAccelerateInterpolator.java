package tornaco.apps.shortx.services.overlay.sidebar;

import android.animation.TimeInterpolator;

public class LogAccelerateInterpolator implements TimeInterpolator {
    private final int mBase;
    private final int mDrift;
    private final float mLogScale;

    public LogAccelerateInterpolator() {
        this(100, 0);
    }

    private LogAccelerateInterpolator(int base, int drift) {
        mBase = base;
        mDrift = drift;
        mLogScale = 1f / computeLog(1, mBase, mDrift);
    }

    private static float computeLog(float t, int base, int drift) {
        return (float) -Math.pow(base, -t) + 1 + (drift * t);
    }

    @Override
    public float getInterpolation(float t) {
        return 1 - computeLog(1 - t, mBase, mDrift) * mLogScale;
    }
}
