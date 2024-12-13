package tornaco.apps.shortx.services.overlay.sidebar;

import android.animation.TimeInterpolator;

public class LogDecelerateInterpolator implements TimeInterpolator {
    private final float mBase;
    private final float mDrift;
    private final float mTimeScale;
    private final float mOutputScale;

    public LogDecelerateInterpolator() {
        this(400f, 1.4f, 0);
    }

    private LogDecelerateInterpolator(float base, float timeScale, float drift) {
        mBase = base;
        mDrift = drift;
        mTimeScale = 1f / timeScale;

        mOutputScale = 1f / computeLog(1f);
    }

    private float computeLog(float t) {
        return 1f - (float) Math.pow(mBase, -t * mTimeScale) + (mDrift * t);
    }

    @Override
    public float getInterpolation(float t) {
        return computeLog(t) * mOutputScale;
    }
}