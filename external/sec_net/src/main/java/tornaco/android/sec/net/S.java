package tornaco.android.sec.net;

/**
 * Sec.
 */
public final class S {

    static {
        System.loadLibrary("tn");
    }

    private S() {
    }

    /**
     * Check
     */
    public static native void c(String plain, String expect);
}
