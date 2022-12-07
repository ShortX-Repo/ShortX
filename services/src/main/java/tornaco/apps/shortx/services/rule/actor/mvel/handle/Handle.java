package tornaco.apps.shortx.services.rule.actor.mvel.handle;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import tornaco.apps.shortx.services.ShortXContext;
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.ActivityImpl;
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.BatteryImpl;
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.DataImpl;
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.HwImpl;
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.UIImpl;

public enum Handle {
    Activity {
        @Override
        public Object getHandle(Context context, ShortXContext shortXContext) {
            return new ActivityImpl(context, shortXContext);
        }
    },

    Ui {
        @Override
        public Object getHandle(Context context, ShortXContext shortXContext) {
            return new UIImpl(context, shortXContext);
        }
    }, Data {
        @Override
        public Object getHandle(Context context, ShortXContext shortXContext) {
            return new DataImpl(context, shortXContext);
        }
    },

    Hw {
        @Override
        public Object getHandle(Context context, ShortXContext shortXContext) {
            return new HwImpl(context, shortXContext);
        }
    },

    Battery {
        @Override
        public Object getHandle(Context context, ShortXContext shortXContext) {
            return new BatteryImpl(context);
        }
    },
    ;

    public abstract Object getHandle(Context context, ShortXContext shortXContext);

    public static Map<String, Object> inject(Context context, ShortXContext shortXContext, Map<String, Object> objectMap) {
        if (sHandleMapBuilt.compareAndSet(false, true)) {
            buildHandleMap(context, shortXContext);
        }
        for (String handleName : staticHandleMap.keySet()) {
            if (objectMap.get(handleName) != null) {
                throw new IllegalStateException("Dup handle name: " + handleName);
            }
            objectMap.put(handleName, staticHandleMap.get(handleName));
        }
        return objectMap;
    }

    private static final AtomicBoolean sHandleMapBuilt = new AtomicBoolean(false);
    private static final Map<String, Object> staticHandleMap = new HashMap<>();

    private static void buildHandleMap(Context context, ShortXContext shortXContext) {
        for (Handle handle : values()) {
            String name = handle.name().toLowerCase();
            Object obj = handle.getHandle(context, shortXContext);
            staticHandleMap.put(name, obj);
        }
    }
}
