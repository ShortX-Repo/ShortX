package f;

import cc.xposed.R;

/* JADX INFO: loaded from: classes.dex */
public interface l {
    public static final String j;
    public static final String k;
    public static final String l;
    public static final String m;
    public static final String n;
    public static final String o;
    public static final String p;
    public static final String q;
    public static final boolean r;
    public static final String s;
    public static final String t;
    public static final String u;
    public static final String v;
    public static final String w;
    public static final String x;
    public static final int y;

    static {
        String strD = k.d();
        j = strD;
        k = strD + '.';
        String strC = k.c();
        l = strC;
        m = strC + "prefs/";
        n = strC + "gestures/";
        o = strC + "icons/";
        p = strC + "local_prefs";
        q = strC + "widgets";
        boolean zContains = strD.contains("pro");
        r = zContains;
        s = k.a();
        String strF = k.f();
        t = strF;
        StringBuilder sb = new StringBuilder();
        sb.append(zContains ? "Xposed-edge-pro-" : "Xposed-edge-");
        sb.append(strF);
        u = sb.toString();
        v = k.b();
        w = k.e();
        x = zContains ? "XEdgePro" : "XEdge";
        y = zContains ? R.string.app_name_pro : R.string.app_name;
    }
}
