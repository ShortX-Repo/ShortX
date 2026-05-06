package com.jozein.xedgepro.xposed;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.Settings;
import de.robv.android.xposed.XposedHelpers;

/* JADX INFO: loaded from: classes.dex */
class h3 {

    static class a {
        static int a(ContentResolver contentResolver, String str, int i) {
            int iD = f.o0.d();
            if (iD != 0) {
                try {
                    return ((Integer) XposedHelpers.callStaticMethod(Settings.Global.class, "getIntForUser", new Object[]{contentResolver, str, Integer.valueOf(i), Integer.valueOf(iD)})).intValue();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            return Settings.Global.getInt(contentResolver, str, i);
        }

        static boolean b(ContentResolver contentResolver, String str, int i) {
            int iD = f.o0.d();
            if (iD != 0) {
                try {
                    return ((Boolean) XposedHelpers.callStaticMethod(Settings.Global.class, "putIntForUser", new Object[]{contentResolver, str, Integer.valueOf(i), Integer.valueOf(iD)})).booleanValue();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            return Settings.Global.putInt(contentResolver, str, i);
        }
    }

    static class b {
        static int a(ContentResolver contentResolver, String str, int i) {
            int iD = f.o0.d();
            if (iD != 0) {
                try {
                    return ((Integer) XposedHelpers.callStaticMethod(Settings.Secure.class, "getIntForUser", new Object[]{contentResolver, str, Integer.valueOf(i), Integer.valueOf(iD)})).intValue();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            return Settings.Secure.getInt(contentResolver, str, i);
        }

        static boolean b(ContentResolver contentResolver, String str, int i) {
            int iD = f.o0.d();
            if (iD != 0) {
                try {
                    return ((Boolean) XposedHelpers.callStaticMethod(Settings.Secure.class, "putIntForUser", new Object[]{contentResolver, str, Integer.valueOf(i), Integer.valueOf(iD)})).booleanValue();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            return Settings.Secure.putInt(contentResolver, str, i);
        }
    }

    static class c {
        static int a(ContentResolver contentResolver, String str, int i) {
            int iD = f.o0.d();
            if (iD != 0) {
                try {
                    return ((Integer) XposedHelpers.callStaticMethod(Settings.System.class, "getIntForUser", new Object[]{contentResolver, str, Integer.valueOf(i), Integer.valueOf(iD)})).intValue();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            return Settings.System.getInt(contentResolver, str, i);
        }

        static boolean b(ContentResolver contentResolver, String str, int i) {
            int iD = f.o0.d();
            if (iD != 0) {
                try {
                    return ((Boolean) XposedHelpers.callStaticMethod(Settings.System.class, "putIntForUser", new Object[]{contentResolver, str, Integer.valueOf(i), Integer.valueOf(iD)})).booleanValue();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            return Settings.System.putInt(contentResolver, str, i);
        }
    }

    static void a(ContentResolver contentResolver, Uri uri, ContentObserver contentObserver) {
        Uri uri2;
        Throwable th;
        if (f.o0.d() != 0) {
            try {
                uri2 = (Uri) XposedHelpers.callStaticMethod(ContentProvider.class, "getUriWithoutUserId", new Object[]{uri});
            } catch (Throwable th2) {
                uri2 = uri;
                th = th2;
            }
            try {
                XposedHelpers.callMethod(contentResolver, "notifyChange", new Object[]{uri2, contentObserver, Boolean.TRUE, -2});
                return;
            } catch (Throwable th3) {
                th = th3;
                f.v.d(th);
                uri = uri2;
                contentResolver.notifyChange(uri, contentObserver);
            }
        }
        contentResolver.notifyChange(uri, contentObserver);
    }
}
