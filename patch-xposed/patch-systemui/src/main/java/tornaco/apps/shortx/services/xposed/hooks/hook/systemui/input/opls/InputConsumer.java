package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Binder;
import android.view.InputChannel;
import android.view.InputMonitor;

import tornaco.apps.shortx.core.util.Logger;
import util.Reflections;

// OplusInputConsumer
public class InputConsumer {
    private InputManager mInputManager;
    private InputMonitor mInputMonitor;
    private final Logger LOGGER = new Logger("InputConsumer");

    public InputConsumer(Context context, String name, InputChannel inputChannel) {
        this.mInputManager = null;
        if (context == null) {
            LOGGER.e("context is null, init OplusInputConsumer failed");
            return;
        }
        this.mInputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
        long ident = Binder.clearCallingIdentity();
        try {
            // this.mInputManager.monitorGestureInput(name, 0);
            InputMonitor monitorGestureInput = (InputMonitor) Reflections.callMethod(this.mInputManager, "monitorGestureInput", name, 0);
            this.mInputMonitor = monitorGestureInput;
            InputChannel client = monitorGestureInput.getInputChannel();
            if (inputChannel == null) {
                LOGGER.e("Has no input channel in InputMonitor, init OplusInputConsumer failed");
            } else {
                client.copyTo(inputChannel);
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    public boolean moniting() {
        return this.mInputMonitor != null;
    }

    public void pilferPointers() {
        InputMonitor inputMonitor = this.mInputMonitor;
        if (inputMonitor != null) {
            inputMonitor.pilferPointers();
        } else {
            LOGGER.e("Has no input monitor, pilferPointers failed");
        }
    }

    public void dispose() {
        try {
            try {
                InputMonitor inputMonitor = this.mInputMonitor;
                if (inputMonitor != null) {
                    inputMonitor.dispose();
                    this.mInputMonitor = null;
                }
            } catch (Exception e) {
                LOGGER.e("dispose error:" + e);
            }
        } finally {
            this.mInputMonitor = null;
        }
    }
}