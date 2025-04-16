package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;

/* loaded from: classes.dex */
public class OplusInputMonitorHelper {
    public static final String TAG = "SHORTX_INPUT_OIM_Helper";
    private static volatile OplusInputMonitorHelper sInstance;
    public static final String OPLUS_EX_CHANNEL_NAME = "ShortXInputReceiver";
    public static String sExChannelName = OPLUS_EX_CHANNEL_NAME;
    private int mChannelNameIndex = 1;
    private ColorExInputEventReceiver mExInputEventReceiver = null;
    private HandlerThread mAsyncThread = new HandlerThread(TAG, -2);

    /* loaded from: classes.dex */
    public interface IExInputEventReceiverCallback {
        boolean onInputEvent(InputEvent inputEvent);
    }

    public static OplusInputMonitorHelper getInstance() {
        if (sInstance == null) {
            synchronized (OplusInputMonitorHelper.class) {
                if (sInstance == null) {
                    sInstance = new OplusInputMonitorHelper();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        OplusInputConsumerManager.getInstance().init(context);
        this.mAsyncThread.start();
    }

    public boolean enableInputReceiver(IExInputEventReceiverCallback callback, boolean reRegister) {
        if (reRegister) {
            updateChannelName();
        }
        boolean exMoniting = OplusInputConsumerManager.getInstance().moniting(sExChannelName);
        Log.d(TAG, "enableInputReceiver, receiver=" + this.mExInputEventReceiver + ", moniting=" + exMoniting + " sExChannelName=" + sExChannelName);
        if (this.mExInputEventReceiver != null && exMoniting) {
            return false;
        }
        InputChannel exInputChannel = new InputChannel();
        OplusInputConsumerManager.getInstance().createMonitorInputConsumer(sExChannelName, exInputChannel);
        ColorExInputEventReceiver colorExInputEventReceiver = new ColorExInputEventReceiver(exInputChannel, this.mAsyncThread.getLooper());
        this.mExInputEventReceiver = colorExInputEventReceiver;
        colorExInputEventReceiver.setCallback(callback);
        return true;
    }

    public void disableInputReceiver() {
        try {
            try {
                ColorExInputEventReceiver colorExInputEventReceiver = this.mExInputEventReceiver;
                if (colorExInputEventReceiver != null) {
                    colorExInputEventReceiver.setCallback(null);
                    this.mExInputEventReceiver.dispose();
                }
            } catch (Exception e) {
                Log.e(TAG, "dispose receiver error:" + e);
            }
            Log.d(TAG, "disableInputReceiver done!");
        } finally {
            this.mExInputEventReceiver = null;
            OplusInputConsumerManager.getInstance().destroyMonitorInputConsumer(sExChannelName);
        }
    }

    public void pilferPointers() {
        OplusInputConsumerManager.getInstance().pilferPointers(sExChannelName);
    }

    /* loaded from: classes.dex */
    private final class ColorExInputEventReceiver extends InputEventReceiver {
        private IExInputEventReceiverCallback mCallback;

        public void setCallback(IExInputEventReceiverCallback callback) {
            this.mCallback = callback;
        }

        public ColorExInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        public void onInputEvent(InputEvent event) {
            boolean consumed = false;
            IExInputEventReceiverCallback iExInputEventReceiverCallback = this.mCallback;
            if (iExInputEventReceiverCallback != null) {
                consumed = iExInputEventReceiverCallback.onInputEvent(event);
            }
            finishInputEvent(event, consumed);
        }
    }

    private void updateChannelName() {
        StringBuilder append = new StringBuilder().append(OPLUS_EX_CHANNEL_NAME);
        int i = this.mChannelNameIndex;
        this.mChannelNameIndex = i + 1;
        sExChannelName = append.append(i).toString();
    }
}