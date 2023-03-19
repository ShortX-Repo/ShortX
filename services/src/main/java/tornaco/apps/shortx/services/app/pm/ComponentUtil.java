package tornaco.apps.shortx.services.app.pm;

import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.content.pm.PackageManager.GET_PROVIDERS;
import static android.content.pm.PackageManager.GET_RECEIVERS;
import static android.content.pm.PackageManager.GET_SERVICES;

import android.annotation.NonNull;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import tornaco.apps.shortx.core.util.Logger;

/**
 * Created by guohao4 on 2017/11/17.
 * Email: Tornaco@163.com
 */

public class ComponentUtil {
    private static final Logger LOGGER = new Logger("ComponentUtil");

    /**
     * Those that has not service and broadcast and no activity.
     */
    public static boolean isGreenPackage(PackageInfo packageInfo) {
        return (packageInfo.receivers == null || packageInfo.receivers.length == 0)
                && (packageInfo.services == null || packageInfo.services.length == 0)
                && (packageInfo.activities == null || packageInfo.activities.length == 0);
    }

    @NonNull
    public static ArrayList<ServiceInfo> getServices(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                packageInfo = pm.getPackageInfo(pkg, GET_SERVICES | PackageManager.MATCH_DISABLED_COMPONENTS);
            } else {
                packageInfo = pm.getPackageInfo(pkg, GET_SERVICES | PackageManager.GET_DISABLED_COMPONENTS);
            }
            if (packageInfo == null) return new ArrayList<>(0);
            ServiceInfo[] serviceInfoArray = packageInfo.services;
            if (serviceInfoArray == null || serviceInfoArray.length == 0)
                return new ArrayList<>(0);
            return newArrayList(serviceInfoArray);
        } catch (Exception e) {
            LOGGER.e("getServices: " + Log.getStackTraceString(e));
            return new ArrayList<>(0);
        }
    }

    @NonNull
    public static ArrayList<ActivityInfo> getActivities(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                packageInfo = pm.getPackageInfo(pkg, GET_ACTIVITIES | PackageManager.MATCH_DISABLED_COMPONENTS);
            } else {
                packageInfo = pm.getPackageInfo(pkg, GET_ACTIVITIES | PackageManager.GET_DISABLED_COMPONENTS);
            }
            if (packageInfo == null) return new ArrayList<>(0);
            ActivityInfo[] activityInfos = packageInfo.activities;
            if (activityInfos == null || activityInfos.length == 0)
                return new ArrayList<>(0);
            return newArrayList(activityInfos);
        } catch (Exception e) {
            LOGGER.e("getActivities: " + Log.getStackTraceString(e));
            return new ArrayList<>(0);
        }
    }

    @NonNull
    public static ArrayList<ActivityInfo> getBroadcasts(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                packageInfo = pm.getPackageInfo(pkg, GET_RECEIVERS | PackageManager.MATCH_DISABLED_COMPONENTS);
            } else {
                packageInfo = pm.getPackageInfo(pkg, GET_RECEIVERS | PackageManager.GET_DISABLED_COMPONENTS);
            }
            if (packageInfo == null) return new ArrayList<>(0);
            ActivityInfo[] activityInfos = packageInfo.receivers;
            if (activityInfos == null || activityInfos.length == 0)
                return new ArrayList<>(0);
            return newArrayList(activityInfos);
        } catch (Exception e) {
            LOGGER.e("getBroadcasts: " + Log.getStackTraceString(e));
            return new ArrayList<>(0);
        }
    }

    @NonNull
    public static ArrayList<ProviderInfo> getProviders(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                packageInfo = pm.getPackageInfo(pkg, GET_PROVIDERS | PackageManager.MATCH_DISABLED_COMPONENTS);
            } else {
                packageInfo = pm.getPackageInfo(pkg, GET_PROVIDERS | PackageManager.GET_DISABLED_COMPONENTS);
            }
            if (packageInfo == null) return new ArrayList<>(0);
            ProviderInfo[] providerInfos = packageInfo.providers;
            if (providerInfos == null || providerInfos.length == 0)
                return new ArrayList<>(0);
            return newArrayList(providerInfos);
        } catch (Exception e) {
            LOGGER.e("getProviders: " + Log.getStackTraceString(e));
            return new ArrayList<>(0);
        }
    }

    public static ComponentName getComponentName(ActivityInfo activityInfo) {
        return new ComponentName(activityInfo.packageName, activityInfo.name);
    }

    public static ComponentName getComponentName(ServiceInfo serviceInfo) {
        return new ComponentName(serviceInfo.packageName, serviceInfo.name);
    }

    private static <E> ArrayList<E> newArrayList(E[] elements) {
        // Avoid integer overflow when a large array is passed in
        int capacity = computeArrayListCapacity(elements.length);
        ArrayList<E> list = new ArrayList<E>(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    private static int computeArrayListCapacity(int arraySize) {
        return saturatedCast(5L + arraySize + (arraySize / 10));
    }

    private static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }
}
