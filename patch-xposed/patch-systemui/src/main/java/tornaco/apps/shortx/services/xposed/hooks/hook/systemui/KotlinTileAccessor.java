package tornaco.apps.shortx.services.xposed.hooks.hook.systemui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tornaco.apps.shortx.core.util.Logger;

public class KotlinTileAccessor {
    private static final Logger LOGGER = new Logger("QSHostAdapterUtil");

    // 缓存：类名 -> 已验证可用的方案
    private static final Map<Class<?>, AccessPlan> CACHE = new ConcurrentHashMap<>();

    // 封装可执行方案
    private interface AccessPlan {
        Object get(Object obj) throws Exception;
    }

    /**
     * 获取 tiles List，不管底层是接口方法还是 StateFlow 字段
     */
    public static Object getTiles(Object obj) {
        Class<?> clazz = obj.getClass();

        // 查缓存
        AccessPlan plan = CACHE.get(clazz);
        if (plan != null) {
            try {
                return plan.get(obj);
            } catch (Exception e) {
                throw new RuntimeException("Cached access failed", e);
            }
        }

        // 没有缓存 → 探测
        List<AccessPlan> candidates = new ArrayList<>();

        // 1. 接口方法 getCurrentQSTiles
        candidates.add(buildInterfaceMethodPlan(
                "com.android.systemui.qs.pipeline.domain.interactor.CurrentTilesInteractor",
                "getCurrentQSTiles"));

        // 3. 接口方法 getCurrentTiles
        candidates.add(buildInterfaceMethodPlan(
                "com.android.systemui.qs.pipeline.domain.interactor.CurrentTilesInteractor",
                "getCurrentTiles"));

        // 4. 字段 currentTiles + getValue
        candidates.add(buildFieldPlan("currentTiles"));

        // 挨个尝试
        for (AccessPlan candidate : candidates) {
            try {
                LOGGER.d("Try candidate: " + candidate);
                Object result = candidate.get(obj);
                LOGGER.d("Try candidate, res: " + result);
                if (result != null) {
                    CACHE.put(clazz, candidate); // 缓存
                    return result;
                }
            } catch (Exception ignored) {
            }
        }

        throw new RuntimeException("No valid access plan found for " + clazz.getName());
    }

    // ---------- 构造候选方案 ----------

    private static AccessPlan buildInterfaceMethodPlan(String ifaceName, String methodName) {
        return obj -> {
            Class<?> iface = Class.forName(ifaceName);
            Method m = iface.getMethod(methodName);
            return m.invoke(obj);
        };
    }

    private static AccessPlan buildFieldPlan(String fieldName) {
        return obj -> {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object fieldValue = f.get(obj);
            if (fieldValue == null) return null;

            try {
                Method getValue = fieldValue.getClass().getMethod("getValue");
                return getValue.invoke(fieldValue);
            } catch (NoSuchMethodException e) {
                return fieldValue; // 如果没有 getValue，直接返回 StateFlow
            }
        };
    }
}
