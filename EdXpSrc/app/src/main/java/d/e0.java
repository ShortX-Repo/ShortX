package d;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import cc.xposed.R;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class e0 extends e.j {
    private void B1(String str) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str));
        intent.addFlags(1208483840);
        try {
            startActivity(intent);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private void C1() {
        s1(new j.e[]{new j.e(0, 0, u(R.string.help_item_0).toString()), new j.e(0, 5, u(R.string.help_item_1).toString()), new j.e(0, 1, u(R.string.help_item_2).toString()), new j.e(1, 5, u(R.string.help_item_3).toString()), new j.e(1, 1, u(R.string.help_item_4).toString()), new j.e(4, 3, u(R.string.help_item_5).toString()), new j.e(3, 1, u(R.string.help_item_6).toString())});
        X("result", true);
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.help_and_support);
    }

    @Override // e.j
    protected int B0() {
        return 5;
    }

    @Override // e.j
    protected View h1(int i) {
        if (i == 0) {
            return new j.g(C0(R.drawable.ic_help), u(R.string.help_how_to_set), (CharSequence) null);
        }
        if (i != 1) {
            if (i == 2) {
                return new j.g(C0(R.drawable.ic_support), u(R.string.visit_xda_thread), (CharSequence) null);
            }
            if (i == 3) {
                return new j.g(C0(R.drawable.ic_tutorial_online), u(R.string.tutorial_online), (CharSequence) null);
            }
            if (i != 4) {
                return null;
            }
            return new j.g(C0(R.drawable.ic_translate), u(R.string.add_translation_or_fix), (CharSequence) null);
        }
        return c1(((Object) u(R.string.help_item_0)) + "\n\n" + ((Object) u(R.string.help_item_1)) + "\n\n" + ((Object) u(R.string.help_item_2)) + "\n\n" + ((Object) u(R.string.help_item_3)) + "\n\n" + ((Object) u(R.string.help_item_4)) + "\n\n" + ((Object) u(R.string.help_item_5)) + "\n\n" + ((Object) u(R.string.help_item_6)));
    }

    @Override // e.j
    protected void k1(int i) {
        String str;
        if (i == 0) {
            C1();
            L();
            return;
        }
        if (i == 2) {
            str = "https://forum.xda-developers.com/t/xposed-edge-pro.3525566/";
        } else if (i == 3) {
            str = "https://sites.google.com/view/tutorial-for-xposed-edge/main";
        } else if (i != 4) {
            return;
        } else {
            str = "https://github.com/jozein/Xposed-edge-translation";
        }
        B1(str);
    }
}
