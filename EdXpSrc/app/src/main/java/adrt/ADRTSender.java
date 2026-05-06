package adrt;

import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ADRTSender {
    private static Context context;
    private static String debuggerPackageName;

    public static void onContext(Context context2, String str) {
        context = context2;
        debuggerPackageName = str;
    }

    public static void sendConnect(String str) {
        Intent intent = new Intent();
        intent.setPackage(debuggerPackageName);
        intent.setAction("com.adrt.CONNECT");
        intent.putExtra("package", str);
        context.sendBroadcast(intent);
    }

    public static void sendStop(String str) {
        Intent intent = new Intent();
        intent.setPackage(debuggerPackageName);
        intent.setAction("com.adrt.STOP");
        intent.putExtra("package", str);
        context.sendBroadcast(intent);
    }

    public static void sendBreakpointHit(String str, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<String> arrayList3, ArrayList<String> arrayList4, ArrayList<String> arrayList5, ArrayList<String> arrayList6) {
        Intent intent = new Intent();
        intent.setPackage(debuggerPackageName);
        intent.setAction("com.adrt.BREAKPOINT_HIT");
        intent.putExtra("package", str);
        intent.putExtra("variables", arrayList);
        intent.putExtra("variableValues", arrayList2);
        intent.putExtra("variableKinds", arrayList3);
        intent.putExtra("stackMethods", arrayList4);
        intent.putExtra("stackLocations", arrayList5);
        intent.putExtra("stackLocationKinds", arrayList6);
        context.sendBroadcast(intent);
    }

    public static void sendFields(String str, String str2, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<String> arrayList3) {
        Intent intent = new Intent();
        intent.setPackage(debuggerPackageName);
        intent.setAction("com.adrt.FIELDS");
        intent.putExtra("package", str);
        intent.putExtra("path", str2);
        intent.putExtra("fields", arrayList);
        intent.putExtra("fieldValues", arrayList2);
        intent.putExtra("fieldKinds", arrayList3);
        context.sendBroadcast(intent);
    }

    public static void sendLogcatLines(String[] strArr) {
        Intent intent = new Intent();
        intent.setPackage(debuggerPackageName);
        intent.setAction("com.adrt.LOGCAT_ENTRIES");
        intent.putExtra("lines", strArr);
        context.sendBroadcast(intent);
    }
}
