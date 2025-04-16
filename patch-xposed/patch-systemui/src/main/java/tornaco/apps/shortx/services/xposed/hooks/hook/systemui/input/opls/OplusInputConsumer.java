package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Binder;
import android.util.Slog;
import android.view.InputChannel;
import android.view.InputMonitor;

import util.Reflections;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class OplusInputConsumer {
    private static final String TAG = "OIM_OplusInputConsumer";
    private InputManager mInputManager;
    private InputMonitor mInputMonitor;

    public OplusInputConsumer(Context context, String name, InputChannel inputChannel) {
        this.mInputManager = null;
        if (context == null) {
            Slog.e(TAG, "context is null, init OplusInputConsumer failed");
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
                Slog.e(TAG, "Has no input channel in InputMonitor, init OplusInputConsumer failed");
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
            Slog.e(TAG, "Has no input monitor, pilferPointers failed");
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
                Slog.e(TAG, "dispose error:" + e);
            }
        } finally {
            this.mInputMonitor = null;
        }
    }
}