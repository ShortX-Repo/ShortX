package com.jozein.xedgepro.xposed;

import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.util.SparseLongArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class q3 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static int f217b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static long f218c;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final Rect f216a = new Rect();

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static Method f219d = null;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static Method f220e = null;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static int f221f = 0;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static int f222g = 0;

    static class a implements Comparable<a> {
        final CharSequence A;
        final String B;
        final View C;
        final int z;

        a(int i, CharSequence charSequence, String str) {
            this.z = i;
            this.A = charSequence;
            this.B = str;
            this.C = null;
        }

        a(ViewGroup viewGroup) {
            this.z = ((viewGroup instanceof WebView) || viewGroup.getClass().getName().contains("WebView")) ? 1 : 8;
            this.A = null;
            this.B = viewGroup.getClass().getName();
            this.C = viewGroup;
        }

        @Override // java.lang.Comparable
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public int compareTo(a aVar) {
            return this.z - aVar.z;
        }

        String b() {
            return q3.c(this.A);
        }

        public String toString() {
            return "Node{" + q3.b(this.z) + ", " + this.B + ", [" + ((Object) this.A) + "]}";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String b(int i) {
        if (i == 0) {
            return "T";
        }
        if (i == 1) {
            return "W";
        }
        if (i == 8) {
            return "V";
        }
        StringBuilder sb = new StringBuilder();
        if ((i & 16) != 0) {
            sb.append('o');
        }
        if ((i & 4) != 0) {
            sb.append('u');
        }
        if ((i & 2) != 0) {
            sb.append('d');
        }
        return sb.toString();
    }

    static String c(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        if (charSequence.length() > 65536) {
            charSequence = charSequence.subSequence(0, 65536);
        }
        return charSequence.toString();
    }

    private static void d(AccessibilityNodeInfo accessibilityNodeInfo, List<a> list) {
        if (accessibilityNodeInfo == null) {
            f.v.c("node == null");
            return;
        }
        int i = 0;
        String string = accessibilityNodeInfo.getClassName().toString();
        if (string.contains("Image")) {
            return;
        }
        if (!string.contains("Text") && !accessibilityNodeInfo.isEditable()) {
            i = 4;
        }
        CharSequence text = accessibilityNodeInfo.getText();
        if (!l(text)) {
            text = accessibilityNodeInfo.getContentDescription();
            if (!l(text)) {
                return;
            } else {
                i |= 2;
            }
        }
        list.add(new a(i, text, string));
    }

    static a e(View view, int i, int i2) {
        if (view == null) {
            return null;
        }
        f217b = 0;
        f218c = SystemClock.uptimeMillis();
        ArrayList arrayList = new ArrayList();
        h(view, i, i2, arrayList);
        return m(arrayList);
    }

    static a f(AccessibilityNodeInfo accessibilityNodeInfo, int i, int i2) {
        if (accessibilityNodeInfo == null) {
            return null;
        }
        f217b = 0;
        f218c = SystemClock.uptimeMillis();
        ArrayList arrayList = new ArrayList();
        i(accessibilityNodeInfo, i, i2, arrayList);
        return m(arrayList);
    }

    static a g(a aVar, int i, int i2) {
        AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo;
        if (aVar.C == null) {
            return aVar;
        }
        f217b = 0;
        f218c = SystemClock.uptimeMillis();
        AccessibilityNodeProvider accessibilityNodeProvider = aVar.C.getAccessibilityNodeProvider();
        if (accessibilityNodeProvider == null || (accessibilityNodeInfoCreateAccessibilityNodeInfo = accessibilityNodeProvider.createAccessibilityNodeInfo(-1)) == null) {
            return aVar;
        }
        ArrayList arrayList = new ArrayList();
        j(accessibilityNodeProvider, accessibilityNodeInfoCreateAccessibilityNodeInfo, i, i2, arrayList);
        return m(arrayList);
    }

    private static void h(View view, int i, int i2, List<a> list) {
        if (view == null) {
            f.v.c("view == null");
            return;
        }
        f217b++;
        if (view.getVisibility() != 0 || view.getAlpha() <= 0.0f) {
            return;
        }
        if (Build.VERSION.SDK_INT < 29 || view.getTransitionAlpha() > 0.0f) {
            Rect rect = f216a;
            view.getGlobalVisibleRect(rect);
            if (k(i, i2, rect)) {
                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    int childCount = viewGroup.getChildCount();
                    for (int i3 = childCount - 1; i3 >= 0; i3--) {
                        h(viewGroup.getChildAt(i3), i, i2, list);
                    }
                    if (childCount == 0) {
                        list.add(new a(viewGroup));
                        return;
                    }
                    return;
                }
                if (view instanceof ImageView) {
                    return;
                }
                if (view instanceof TextView) {
                    CharSequence text = ((TextView) view).getText();
                    if (l(text)) {
                        list.add(new a(0, text, view.getClass().getName()));
                        return;
                    }
                    return;
                }
                if (view.onCheckIsTextEditor() || view.getClass().getName().contains("Text")) {
                    try {
                        CharSequence charSequence = (CharSequence) XposedHelpers.callMethod(view, "getText", new Object[0]);
                        if (charSequence != null) {
                            list.add(new a(0, charSequence, view.getClass().getName()));
                            return;
                        }
                    } catch (Throwable th) {
                        f.v.c(th.toString());
                    }
                }
                d(view.createAccessibilityNodeInfo(), list);
            }
        }
    }

    private static void i(AccessibilityNodeInfo accessibilityNodeInfo, int i, int i2, List<a> list) {
        if (accessibilityNodeInfo == null) {
            return;
        }
        f217b++;
        if (accessibilityNodeInfo.isVisibleToUser()) {
            Rect rect = f216a;
            accessibilityNodeInfo.getBoundsInScreen(rect);
            if (k(i, i2, rect)) {
                int childCount = accessibilityNodeInfo.getChildCount();
                if (childCount <= 0) {
                    d(accessibilityNodeInfo, list);
                    return;
                }
                for (int i3 = childCount - 1; i3 >= 0; i3--) {
                    i(accessibilityNodeInfo.getChild(i3), i, i2, list);
                }
            }
        }
    }

    private static void j(AccessibilityNodeProvider accessibilityNodeProvider, AccessibilityNodeInfo accessibilityNodeInfo, int i, int i2, List<a> list) {
        long jLongValue;
        if (accessibilityNodeInfo == null) {
            f.v.c("node == null");
            return;
        }
        f217b++;
        if (accessibilityNodeInfo.isVisibleToUser()) {
            Rect rect = f216a;
            accessibilityNodeInfo.getBoundsInScreen(rect);
            if (k(i, i2, rect)) {
                int childCount = accessibilityNodeInfo.getChildCount();
                if (childCount <= 0) {
                    d(accessibilityNodeInfo, list);
                    return;
                }
                SparseLongArray sparseLongArray = null;
                for (int i3 = childCount - 1; i3 >= 0; i3--) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        if (f219d == null) {
                            f219d = XposedHelpers.findMethodExact(AccessibilityNodeInfo.class, "getChildId", new Class[]{Integer.TYPE});
                        }
                        jLongValue = ((Long) f219d.invoke(accessibilityNodeInfo, Integer.valueOf(i3))).longValue();
                    } else {
                        if (sparseLongArray == null) {
                            if (f220e == null) {
                                f220e = XposedHelpers.findMethodExact(AccessibilityNodeInfo.class, "getChildNodeIds", new Class[0]);
                            }
                            sparseLongArray = (SparseLongArray) f220e.invoke(accessibilityNodeInfo, new Object[0]);
                        }
                        jLongValue = sparseLongArray.get(i3);
                    }
                    j(accessibilityNodeProvider, accessibilityNodeProvider.createAccessibilityNodeInfo((int) (jLongValue >> 32)), i, i2, list);
                }
            }
        }
    }

    private static boolean k(int i, int i2, Rect rect) {
        if (f221f <= 0) {
            float fJ = g.a0.j();
            f222g = (int) (fJ / 10.0f);
            f221f = (int) (fJ / 80.0f);
        }
        int i3 = rect.top;
        int i4 = rect.bottom;
        if (i3 > i4 && i4 - i3 < f222g) {
            int i5 = f221f;
            i3 -= i5;
            i4 += i5;
        }
        return i >= rect.left && i <= rect.right && i2 >= i3 && i2 <= i4;
    }

    static boolean l(CharSequence charSequence) {
        int length;
        if (charSequence == null || (length = charSequence.length()) == 0) {
            return false;
        }
        if (length != 1) {
            return (length == 2 && charSequence.charAt(0) == ' ' && charSequence.charAt(1) == ' ') ? false : true;
        }
        char cCharAt = charSequence.charAt(0);
        return (cCharAt > ' ' && cCharAt <= '~') || cCharAt > 160;
    }

    private static a m(List<a> list) {
        int size = list.size();
        if (size <= 0) {
            return null;
        }
        if (size > 1) {
            Collections.sort(list);
        }
        return list.get(0);
    }
}
