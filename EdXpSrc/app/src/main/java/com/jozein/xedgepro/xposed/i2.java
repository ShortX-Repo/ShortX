package com.jozein.xedgepro.xposed;

import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ScrollView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/* JADX INFO: loaded from: classes.dex */
class i2 extends p2 {

    class a extends XC_MethodHook {
        a() {
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            float axisValue = ((MotionEvent) methodHookParam.args[0]).getAxisValue(9);
            if (axisValue == 100000.0f || axisValue == -100000.0f) {
                ScrollView scrollView = (ScrollView) methodHookParam.thisObject;
                if (axisValue == 100000.0f) {
                    scrollView.smoothScrollTo(0, 0);
                } else {
                    scrollView.smoothScrollTo(0, ((Integer) XposedHelpers.findMethodBestMatch(ScrollView.class, "computeVerticalScrollRange", new Class[0]).invoke(scrollView, new Object[0])).intValue());
                }
                methodHookParam.setResult(Boolean.TRUE);
            }
        }
    }

    class b extends XC_MethodHook {
        b() {
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            float axisValue = ((MotionEvent) methodHookParam.args[0]).getAxisValue(9);
            if (axisValue == 100000.0f || axisValue == -100000.0f) {
                AbsListView absListView = (AbsListView) methodHookParam.thisObject;
                if (axisValue != 100000.0f) {
                    absListView.smoothScrollToPosition(absListView.getCount() - 1);
                } else if (absListView instanceof GridView) {
                    absListView.smoothScrollToPositionFromTop(0, 0);
                } else {
                    absListView.smoothScrollToPosition(0);
                }
                methodHookParam.setResult(Boolean.TRUE);
            }
        }
    }

    class c extends XC_MethodHook {
        c() {
        }

        int a(int i) {
            if (i != 0) {
                return (i * 200) / ((int) Math.sqrt(i));
            }
            return 0;
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            float axisValue = ((MotionEvent) methodHookParam.args[0]).getAxisValue(9);
            if (axisValue == 100000.0f || axisValue == -100000.0f) {
                WebView webView = (WebView) methodHookParam.thisObject;
                int iIntValue = ((Integer) XposedHelpers.findMethodBestMatch(WebView.class, "computeVerticalScrollOffset", new Class[0]).invoke(webView, new Object[0])).intValue();
                int iA = axisValue == 100000.0f ? -a(iIntValue) : a(((Integer) XposedHelpers.findMethodBestMatch(WebView.class, "computeVerticalScrollRange", new Class[0]).invoke(webView, new Object[0])).intValue() - iIntValue);
                if (iA != 0) {
                    webView.flingScroll(0, iA);
                }
                methodHookParam.setResult(Boolean.TRUE);
            }
        }
    }

    i2() {
        try {
            XposedHelpers.findAndHookMethod(ScrollView.class, "onGenericMotionEvent", new Object[]{MotionEvent.class, new a()});
            XposedHelpers.findAndHookMethod(AbsListView.class, "onGenericMotionEvent", new Object[]{MotionEvent.class, new b()});
            XposedHelpers.findAndHookMethod(WebView.class, "onGenericMotionEvent", new Object[]{MotionEvent.class, new c()});
        } catch (Throwable th) {
            p2.g(th);
        }
    }
}
