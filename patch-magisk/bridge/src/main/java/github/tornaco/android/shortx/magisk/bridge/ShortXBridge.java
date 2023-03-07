package github.tornaco.android.shortx.magisk.bridge;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;

import tornaco.apps.shortx.core.util.Logger;

public class ShortXBridge {
    static {
        Printer androidPrinter = new AndroidPrinter();
        XLog.init(new LogConfiguration.Builder()
                        .logLevel(LogLevel.ALL)
                        .tag("ShortM")
                        .build(),
                androidPrinter);
    }

    private static final Logger LOGGER = new Logger("Bridge");

    public static void main(String command, String arg) {
        new ShortXBridge().run(command, arg);
    }

    private void run(String event, String arg) {
        NativeEvent nativeEvent = NativeEvent.valueOf(event);
        LOGGER.d(String.format("Bridge main with event: %s, args: %s", nativeEvent, arg));

        switch (nativeEvent) {
            case postSpecializeSystemServer:
                onSystemServerProcess();
                break;
            case postSpecializeApp:
                onAppProcess(arg);
                break;
        }
    }

    private void onSystemServerProcess() {

    }

    private void onAppProcess(String processName) {

    }

    /**
     * Do not bother the typo and case.
     */
    enum NativeEvent {
        preSpecializeApp,
        preSpecializeSystemServer,
        postSpecializeApp,
        postSpecializeSystemServer,
    }
}
