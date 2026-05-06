package tornaco.apps.shortx.services.input.opls;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.InputChannel;

import tornaco.apps.shortx.core.BuildPropInternal;

/* loaded from: classes.dex */
class OplusInputConsumerManager {
    private static final String TAG = "OIM_OplusInputConsumerManager";
    private static volatile OplusInputConsumerManager sInstance;
    private Context mContext;
    private final ArrayMap<String, OplusInputConsumer> mInputConsumers = new ArrayMap<>();
    private boolean mDebug = BuildPropInternal.APP_IS_DEBUG;

    OplusInputConsumerManager() {
    }

    public static OplusInputConsumerManager getInstance() {
        if (sInstance == null) {
            synchronized (OplusInputConsumerManager.class) {
                if (sInstance == null) {
                    sInstance = new OplusInputConsumerManager();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public void createMonitorInputConsumer(String name, InputChannel inputChannel) {
        Slog.i(TAG, "createMonitorInputConsumer name = " + name);
        synchronized (this.mInputConsumers) {
            if (this.mInputConsumers.containsKey(name)) {
                throw new IllegalStateException("Existing input consumer found with name: " + name);
            }
            OplusInputConsumer consumer = new OplusInputConsumer(this.mContext, name, inputChannel);
            this.mInputConsumers.put(name, consumer);
        }
    }

    public boolean destroyMonitorInputConsumer(String name) {
        Slog.i(TAG, "destroyMonitorInputConsumer name = " + name);
        synchronized (this.mInputConsumers) {
            OplusInputConsumer consumer = this.mInputConsumers.remove(name);
            if (consumer != null) {
                consumer.dispose();
                return true;
            }
            return false;
        }
    }

    public void pilferPointers(String name) {
        if (this.mDebug) {
            Slog.i(TAG, "start pilferPointers, consumer name: " + name);
        }
        synchronized (this.mInputConsumers) {
            OplusInputConsumer consumer = this.mInputConsumers.getOrDefault(name, null);
            if (consumer != null) {
                consumer.pilferPointers();
            } else {
                Slog.e(TAG, "Uncorrect input monitor name, pilferPointers failed");
            }
        }
    }

    public boolean moniting(String name) {
        synchronized (this.mInputConsumers) {
            OplusInputConsumer consumer = this.mInputConsumers.getOrDefault(name, null);
            if (consumer != null) {
                return consumer.moniting();
            }
            Slog.e(TAG, "Uncorrect input monitor name, unknow monitor");
            return false;
        }
    }
}