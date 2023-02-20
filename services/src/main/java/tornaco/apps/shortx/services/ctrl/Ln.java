package tornaco.apps.shortx.services.ctrl;

import tornaco.apps.shortx.core.util.Logger;

/**
 * Log both to Android logger (so that logs are visible in "adb logcat") and standard output/error (so that they are visible in the terminal
 * directly).
 */
public final class Ln {
    private static final Logger LOGGER = new Logger("LN-SERVER");

    public static void v(String message) {
        LOGGER.d(message);
    }

    public static void d(String message) {
        LOGGER.d(message);
    }

    public static void i(String message) {
        LOGGER.i(message);
    }

    public static void w(String message, Throwable throwable) {
        Throwable e = throwable;
        if (e == null) e = new Throwable();
        LOGGER.w(message);
    }

    public static void w(String message) {
        w(message, null);
    }

    public static void e(String message, Throwable throwable) {
        Throwable e = throwable;
        if (e == null) e = new Throwable();
        LOGGER.e(e, message);
    }

    public static void e(String message) {
        e(message, null);
    }
}
