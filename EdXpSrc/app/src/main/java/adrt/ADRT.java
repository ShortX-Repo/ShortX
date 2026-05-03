package adrt;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Process;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ADRT {
    private static final String AIDE_PACKAGE_NAME = "com.aide.ui";
    private static Context context;
    private static ADRTThread currentBreakpointThread;
    private static boolean enableAllClasses;
    private static boolean isDebuggingDisabled;
    private static String packageName;
    private static String processName;
    private static ADRTThreadLocal threadlocal = new ADRTThreadLocal();
    private static Map<Long, String> metadataFileNames = new HashMap();
    private static Map<String, Long> metadataFileIds = new HashMap();
    private static ADRTLongMap metadataCallGraph = new ADRTLongMap();
    private static ArrayList<String> currentBreakpoints = new ArrayList<>();
    private static Map<Long, boolean[]> fileIdBreakpoints = new HashMap();
    private static Map<Long, List<String>> fileIdClasses = new HashMap();
    private static Object suspendLock = new Object();
    private static boolean isSuspended = false;
    private static Object enabledLock = new Object();
    private static Map<String, Field> toggleFields = new HashMap();
    private static ADRTLongSet enabledFileIds = new ADRTLongSet();
    private static Object contextLock = new Object();

    public static void connectDebugger(Context context2, String str, boolean z, boolean z2) {
        boolean z3 = true;
        context = context2;
        if ((context2.getApplicationInfo().flags & 2) == 0 && !AIDE_PACKAGE_NAME.equals(context2.getPackageName())) {
            z3 = false;
        }
        if (!z3) {
            disableDebugging();
            return;
        }
        try {
            context2.getPackageManager().getPackageInfo(str, 128);
            packageName = context2.getPackageName();
            processName = "";
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context2.getSystemService("activity")).getRunningAppProcesses();
            int iMyPid = Process.myPid();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (iMyPid == runningAppProcessInfo.pid) {
                    processName = runningAppProcessInfo.processName;
                }
            }
            synchronized (suspendLock) {
                isSuspended = true;
            }
            ADRTSender.onContext(context2, str);
            ADRTReceiver.onContext(context2);
            if (z) {
                ADRTLogCatReader.onContext(context2, str);
            }
            if (z2) {
                try {
                    if (getDisabledVersion() >= getCurrentVersion()) {
                        disableDebugging();
                        return;
                    }
                } catch (Throwable th) {
                }
            }
            loadMetadata();
            synchronized (suspendLock) {
                while (isSuspended) {
                    ADRTSender.sendConnect(packageName);
                    try {
                        suspendLock.wait(5000L);
                    } catch (InterruptedException e2) {
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e3) {
            disableDebugging();
        }
    }

    private static void disableVersion() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("adrt", 0);
        sharedPreferences.edit().putLong("disabledVersion", getCurrentVersion()).commit();
    }

    private static long getCurrentVersion() {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0).lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e2) {
            return 0L;
        }
    }

    private static long getDisabledVersion() {
        return context.getSharedPreferences("adrt", 0).getLong("disabledVersion", 0L);
    }

    public static void disconnectDebugger() {
        ADRTSender.sendStop(packageName);
        disconnect();
    }

    private static void loadMetadata() {
        try {
            int[] iArr = ADRTMetadata.FILE_IDS;
            String[] strArr = ADRTMetadata.FILE_NAMES;
            int[][] iArr2 = ADRTMetadata.FILE_CALLED_FILES;
            int[][] iArr3 = ADRTMetadata.FILE_CALLING_FILES;
            Map<Long, String> map = metadataFileNames;
            Map<String, Long> map2 = metadataFileIds;
            ADRTLongMap aDRTLongMap = metadataCallGraph;
            for (int i = 0; i < iArr.length; i++) {
                long j = iArr[i];
                map2.put(strArr[i], Long.valueOf(j));
                map.put(Long.valueOf(j), strArr[i]);
            }
            for (int i2 = 0; i2 < iArr.length; i2++) {
                long j2 = iArr[i2];
                int[] iArr4 = iArr2[i2];
                if (iArr4 != null) {
                    for (int i3 : iArr4) {
                        aDRTLongMap.insert(i3, j2);
                    }
                }
                int[] iArr5 = iArr3[i2];
                if (iArr5 != null) {
                    for (int i4 : iArr5) {
                        aDRTLongMap.insert(j2, i4);
                    }
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static void disableDebugging() {
        isDebuggingDisabled = true;
    }

    static String getPackageName() {
        return packageName;
    }

    static String getFileName(long j) {
        return metadataFileNames.get(Long.valueOf(j));
    }

    private static long getFileId(String str) {
        return metadataFileIds.get(str).longValue();
    }

    static boolean[] getBreakpointLines(long j) {
        return fileIdBreakpoints.get(Long.valueOf(j));
    }

    static boolean onBreakpointHit(ADRTThread aDRTThread) {
        boolean z;
        synchronized (suspendLock) {
            enableDebugging(false);
            z = currentBreakpointThread == null || currentBreakpointThread == aDRTThread;
            if (currentBreakpointThread == null) {
                currentBreakpointThread = aDRTThread;
            } else {
                currentBreakpointThread.clearStep();
            }
        }
        return z;
    }

    static void suspendThread() {
        synchronized (suspendLock) {
            if (!isDebuggingDisabled) {
                isSuspended = true;
                while (isSuspended) {
                    try {
                        suspendLock.wait(1000L);
                    } catch (InterruptedException e2) {
                    }
                }
            }
        }
    }

    private static void enableDebugging(boolean z) {
        synchronized (enabledLock) {
            if (enableAllClasses != z) {
                enableAllClasses = z;
                enableDebugging();
            }
        }
    }

    private static void enableDebugging() {
        synchronized (enabledLock) {
            for (Map.Entry<Long, List<String>> entry : fileIdClasses.entrySet()) {
                boolean z = enableAllClasses || enabledFileIds.contains(entry.getKey().longValue());
                Iterator<String> it = entry.getValue().iterator();
                while (it.hasNext()) {
                    enableDebugging(it.next(), z);
                }
            }
        }
    }

    private static void enableDebugging(String str, boolean z) {
        synchronized (enabledLock) {
            try {
                Field declaredField = toggleFields.get(str);
                if (declaredField == null) {
                    declaredField = Class.forName(str).getDeclaredField("adrt$enabled");
                    declaredField.setAccessible(true);
                    toggleFields.put(str, declaredField);
                }
                declaredField.set(null, Boolean.valueOf(z));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    static void setBreakpoints(ArrayList<String> arrayList) {
        boolean[] zArr;
        boolean[] zArr2;
        boolean z;
        if (!isDebuggingDisabled && context != null) {
            for (String str : currentBreakpoints) {
                if (!arrayList.contains(str)) {
                    try {
                        String[] strArrSplit = str.split(":");
                        long fileId = getFileId(strArrSplit[0]);
                        int i = Integer.parseInt(strArrSplit[1]);
                        boolean[] zArr3 = fileIdBreakpoints.get(Long.valueOf(fileId));
                        if (zArr3 != null) {
                            zArr3[i] = false;
                            int i2 = 0;
                            while (true) {
                                if (i2 >= zArr3.length) {
                                    z = false;
                                    break;
                                } else {
                                    if (zArr3[i2]) {
                                        z = true;
                                        break;
                                    }
                                    i2++;
                                }
                            }
                            if (!z) {
                                fileIdBreakpoints.remove(Long.valueOf(fileId));
                            }
                        }
                    } catch (Throwable th) {
                    }
                }
            }
            for (String str2 : arrayList) {
                if (!currentBreakpoints.contains(str2)) {
                    try {
                        String[] strArrSplit2 = str2.split(":");
                        long fileId2 = getFileId(strArrSplit2[0]);
                        int i3 = Integer.parseInt(strArrSplit2[1]);
                        boolean[] zArr4 = fileIdBreakpoints.get(Long.valueOf(fileId2));
                        if (zArr4 == null) {
                            Map<Long, boolean[]> map = fileIdBreakpoints;
                            Long lValueOf = Long.valueOf(fileId2);
                            zArr = new boolean[Math.max(i3 * 2, 1000)];
                            map.put(lValueOf, zArr);
                        } else {
                            zArr = zArr4;
                        }
                        if (zArr.length <= i3) {
                            zArr2 = new boolean[zArr.length * 2];
                            System.arraycopy(zArr, 0, zArr2, 0, zArr.length);
                            fileIdBreakpoints.put(Long.valueOf(fileId2), zArr2);
                        } else {
                            zArr2 = zArr;
                        }
                        zArr2[i3] = true;
                    } catch (Throwable th2) {
                    }
                }
            }
            currentBreakpoints = arrayList;
            ADRTLongSet aDRTLongSet = new ADRTLongSet();
            Iterator<Long> it = fileIdBreakpoints.keySet().iterator();
            while (it.hasNext()) {
                aDRTLongSet.put(it.next().longValue());
            }
            metadataCallGraph.closure(aDRTLongSet);
            synchronized (enabledLock) {
                enabledFileIds.clear();
                enabledFileIds.put(aDRTLongSet);
                enableDebugging();
            }
        }
    }

    static void stepOver() {
        synchronized (suspendLock) {
            if (!isDebuggingDisabled && context != null && currentBreakpointThread != null) {
                currentBreakpointThread.setStepOver();
                currentBreakpointThread = null;
                isSuspended = false;
                suspendLock.notifyAll();
            }
        }
    }

    static void stepOut() {
        synchronized (suspendLock) {
            if (!isDebuggingDisabled && context != null && currentBreakpointThread != null) {
                currentBreakpointThread.setStepOut();
                currentBreakpointThread = null;
                isSuspended = false;
                suspendLock.notifyAll();
            }
        }
    }

    static void stepIn() {
        synchronized (suspendLock) {
            if (!isDebuggingDisabled && context != null && currentBreakpointThread != null) {
                currentBreakpointThread.setStepIn();
                currentBreakpointThread = null;
                enableDebugging(true);
                isSuspended = false;
                suspendLock.notifyAll();
            }
        }
    }

    static void stop() {
        disableVersion();
        Process.killProcess(Process.myPid());
    }

    static void disconnect() {
        synchronized (suspendLock) {
            if (!isDebuggingDisabled && context != null) {
                resume(new ArrayList());
                disableDebugging();
                disableVersion();
            }
        }
    }

    static void resume(ArrayList<String> arrayList) {
        if (arrayList != null) {
            setBreakpoints(arrayList);
        }
        synchronized (suspendLock) {
            if (!isDebuggingDisabled && context != null) {
                if (currentBreakpointThread != null) {
                    currentBreakpointThread.clearStep();
                }
                currentBreakpointThread = null;
                isSuspended = false;
                suspendLock.notifyAll();
            }
        }
    }

    static void sendFields(String str) {
        synchronized (suspendLock) {
            if (currentBreakpointThread != null && context != null) {
                currentBreakpointThread.sendFields(str);
            }
        }
    }

    public static void onContext(Context context2, String str) {
        if (context2 != context2.getApplicationContext()) {
            synchronized (contextLock) {
                if (context == null) {
                    connectDebugger(context2.getApplicationContext(), str, true, true);
                }
            }
        }
    }

    public static void onClassLoad(long j, String str) {
        if (!isDebuggingDisabled) {
            synchronized (enabledLock) {
                List<String> arrayList = fileIdClasses.get(Long.valueOf(j));
                if (arrayList == null) {
                    Map<Long, List<String>> map = fileIdClasses;
                    Long lValueOf = Long.valueOf(j);
                    arrayList = new ArrayList<>();
                    map.put(lValueOf, arrayList);
                }
                arrayList.add(str);
                if (enableAllClasses || enabledFileIds.contains(j)) {
                    enableDebugging(str, true);
                }
            }
        }
    }

    public static ADRTThread onMethodEnter(long j) {
        ADRTThread aDRTThread = threadlocal.get();
        aDRTThread.onMethodEnter(j);
        return aDRTThread;
    }
}
