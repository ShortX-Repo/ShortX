package tornaco.apps.shortx.ui.util;

public abstract class Singleton2Params<P, T> {
    private T mInstance;

    protected abstract T create(P p);

    public final T get(P p) {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create(p);
            }
            return mInstance;
        }
    }
}