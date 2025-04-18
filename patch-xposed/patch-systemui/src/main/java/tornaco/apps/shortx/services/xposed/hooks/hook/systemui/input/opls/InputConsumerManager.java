package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls;

import android.content.Context;
import android.util.ArrayMap;
import android.view.InputChannel;

import tornaco.apps.shortx.core.BuildPropInternal;
import tornaco.apps.shortx.core.util.Logger;

// OplusInputConsumerManager
class InputConsumerManager {
    private static volatile InputConsumerManager sInstance;
    private Context mContext;
    private final ArrayMap<String, InputConsumer> mInputConsumers = new ArrayMap<>();
    private final Logger LOGGER = new Logger("InputConsumerManager");

    InputConsumerManager() {
    }

    public static InputConsumerManager getInstance() {
        if (sInstance == null) {
            synchronized (InputConsumerManager.class) {
                if (sInstance == null) {
                    sInstance = new InputConsumerManager();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public void createMonitorInputConsumer(String name, InputChannel inputChannel) {
        LOGGER.i("createMonitorInputConsumer name = " + name);
        synchronized (this.mInputConsumers) {
            if (this.mInputConsumers.containsKey(name)) {
                throw new IllegalStateException("Existing input consumer found with name: " + name);
            }
            InputConsumer consumer = new InputConsumer(this.mContext, name, inputChannel);
            this.mInputConsumers.put(name, consumer);
        }
    }

    public boolean destroyMonitorInputConsumer(String name) {
        LOGGER.i("destroyMonitorInputConsumer name = " + name);
        synchronized (this.mInputConsumers) {
            InputConsumer consumer = this.mInputConsumers.remove(name);
            if (consumer != null) {
                consumer.dispose();
                return true;
            }
            return false;
        }
    }

    public void pilferPointers(String name) {
        LOGGER.i("start pilferPointers, consumer name: " + name);
        synchronized (this.mInputConsumers) {
            InputConsumer consumer = this.mInputConsumers.getOrDefault(name, null);
            if (consumer != null) {
                consumer.pilferPointers();
            } else {
                LOGGER.e("Uncorrect input monitor name, pilferPointers failed");
            }
        }
    }

    public boolean moniting(String name) {
        synchronized (this.mInputConsumers) {
            InputConsumer consumer = this.mInputConsumers.getOrDefault(name, null);
            if (consumer != null) {
                return consumer.moniting();
            }
            LOGGER.e("Uncorrect input monitor name, unknow monitor");
            return false;
        }
    }
}