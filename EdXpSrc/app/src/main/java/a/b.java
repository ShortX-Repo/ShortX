package a;

import a.b;
import a.h;
import a.p;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.util.Pair;
import android.view.KeyEvent;
import cc.xposed.R;
import f.i;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes.dex */
public abstract class b implements f.l, Parcelable {
    public static final String C;
    public static final Parcelable.Creator<b> CREATOR;
    public static final String D;
    private static q E;
    private static t1 F;
    private static final b[] G;
    private static b[] H;
    final int A;
    public final int B;
    public final int z;

    class a implements Parcelable.Creator<b> {
        a() {
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public b createFromParcel(Parcel parcel) {
            f.d0 d0Var = new f.d0(parcel);
            b bVarS = d0Var.i() ? b.s(d0Var) : b.e();
            d0Var.k();
            return bVarS;
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
        public b[] newArray(int i) {
            return new b[i];
        }
    }

    public static class a0 extends c0 {
        public final boolean J;
        public final String K;
        private String[] L;

        private a0() {
            this(false, false, null);
        }

        private a0(int i, boolean z, boolean z2, String str) {
            super(i, z);
            this.L = null;
            this.J = z2;
            this.K = str;
        }

        /* synthetic */ a0(a aVar) {
            this();
        }

        private a0(f.s sVar) {
            this(sVar.h() != 0, sVar.h() != 0, sVar.e());
        }

        /* synthetic */ a0(f.s sVar, a aVar) {
            this(sVar);
        }

        public a0(boolean z, boolean z2, String str) {
            this(100, z, z2, str);
        }

        public String[] A() {
            if (this.K != null && this.L == null) {
                StringTokenizer stringTokenizer = new StringTokenizer(this.K, "\r\n");
                int iCountTokens = stringTokenizer.countTokens();
                this.L = new String[iCountTokens];
                for (int i = 0; i < iCountTokens; i++) {
                    this.L[i] = stringTokenizer.nextToken();
                }
            }
            return this.L;
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            String[] strArrA = A();
            sb.append(str);
            sb.append(context.getText(this.A));
            if (strArrA != null && strArrA.length > 0) {
                sb.append(this.J ? " #: " : " $: ");
                sb.append(strArrA[0]);
                if (strArrA.length > 1) {
                    for (int i = 1; i < strArrA.length; i++) {
                        sb.append('\n');
                        sb.append(str);
                        sb.append("    ");
                        sb.append(strArrA[i]);
                    }
                }
            }
            return sb;
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.K == null) {
                return super.n(context);
            }
            return ((Object) super.n(context)) + ": " + this.K;
        }

        @Override // a.b.c0, a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.J ? 1 : 0).b(this.K);
        }

        public String z() {
            return this.K;
        }
    }

    public static final class a1 extends b {
        public final int I;
        public final int J;

        public a1(int i, int i2) {
            super(52, R.string.action_inject_key_event, R.drawable.ic_inject_key_event);
            this.I = i;
            this.J = i2;
        }

        private a1(f.s sVar) {
            this(sVar.h(), sVar.h());
        }

        /* synthetic */ a1(f.s sVar, a aVar) {
            this(sVar);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            int i = this.I;
            if (i != 0) {
                String strSubstring = KeyEvent.keyCodeToString(i).substring(8);
                int i2 = this.J;
                if (i2 == 0) {
                    return context.getString(R.string.key_click_f, strSubstring);
                }
                if (i2 == 1) {
                    return context.getString(R.string.key_double_click_f, strSubstring);
                }
                if (i2 == 2) {
                    return context.getString(R.string.key_long_press_f, strSubstring);
                }
            }
            return super.n(context);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I).d(this.J);
        }

        z0 z() {
            return new z0(this.I, this.J, 0);
        }
    }

    public static final class a2 extends b {
        private a2() {
            super(66, R.string.action_previous_app, R.drawable.ic_previous_app);
        }

        /* synthetic */ a2(a aVar) {
            this();
        }
    }

    public static final class a3 extends b implements u0 {
        public final int I;

        public a3(int i) {
            super(85, R.string.action_soft_keyboard, R.drawable.ic_soft_keyboard);
            this.I = i;
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            return context.getText(i != 1 ? i != 2 ? i != 3 ? i != 4 ? R.string.toggle : R.string.last_input_method : R.string.input_method_menu : R.string.hide_soft_keyboard : R.string.show_soft_keyboard);
        }

        @Override // a.b.u0
        public int b() {
            return 5;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new a3(i);
        }

        @Override // a.b
        public Drawable m(Context context) {
            Resources resources;
            int i;
            int i2 = this.I;
            if (i2 == 1) {
                resources = context.getResources();
                i = R.drawable.ic_show_soft_keyboard;
            } else if (i2 == 2) {
                resources = context.getResources();
                i = R.drawable.ic_hide_soft_keyboard;
            } else if (i2 == 3) {
                resources = context.getResources();
                i = R.drawable.ic_input_method_menu;
            } else {
                if (i2 != 4) {
                    return super.m(context);
                }
                resources = context.getResources();
                i = R.drawable.ic_last_input_method;
            }
            return resources.getDrawable(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            int i;
            int i2 = this.I;
            if (i2 == 1) {
                i = R.string.show_soft_keyboard;
            } else if (i2 == 2) {
                i = R.string.hide_soft_keyboard;
            } else if (i2 == 3) {
                i = R.string.input_method_menu;
            } else {
                if (i2 != 4) {
                    return super.n(context);
                }
                i = R.string.last_input_method;
            }
            return context.getText(i);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    /* JADX INFO: renamed from: a.b$b, reason: collision with other inner class name */
    public static final class C0000b extends b implements f.e0 {
        private C0000b() {
            super(102, R.string.action_abort, R.drawable.ic_abort);
        }

        /* synthetic */ C0000b(a aVar) {
            this();
        }
    }

    public static final class b0 extends c0 {
        public final String J;
        private String[] K;

        private b0(f.s sVar) {
            this(sVar.h() != 0, Uri.decode(sVar.a()));
        }

        /* synthetic */ b0(f.s sVar, a aVar) {
            this(sVar);
        }

        private b0(boolean z, String str) {
            super(61, z);
            this.K = null;
            this.J = str;
        }

        public a0 A() {
            return new a0(this.I, false, this.J);
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            String[] strArrZ = z();
            sb.append(str);
            sb.append(context.getText(this.A));
            if (strArrZ != null && strArrZ.length > 0) {
                sb.append(": ");
                sb.append(strArrZ[0]);
                if (strArrZ.length > 1) {
                    for (int i = 1; i < strArrZ.length; i++) {
                        sb.append('\n');
                        sb.append(str);
                        sb.append("    ");
                        sb.append(strArrZ[i]);
                    }
                }
            }
            return sb;
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.J == null) {
                return super.n(context);
            }
            return ((Object) super.n(context)) + ": " + this.J;
        }

        @Override // a.b.c0, a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.c(Uri.encode(this.J));
        }

        String[] z() {
            if (this.J != null && this.K == null) {
                StringTokenizer stringTokenizer = new StringTokenizer(this.J, "\r\n");
                int iCountTokens = stringTokenizer.countTokens();
                this.K = new String[iCountTokens];
                for (int i = 0; i < iCountTokens; i++) {
                    this.K[i] = stringTokenizer.nextToken().trim();
                }
            }
            return this.K;
        }
    }

    public static final class b1 extends b {
        public final String I;

        private b1() {
            this((String) null);
        }

        /* synthetic */ b1(a aVar) {
            this();
        }

        private b1(f.s sVar) {
            this(sVar.e());
        }

        /* synthetic */ b1(f.s sVar, a aVar) {
            this(sVar);
        }

        public b1(String str) {
            super(108, R.string.action_inject_text, R.drawable.ic_inject_text);
            this.I = str;
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I == null) {
                return super.n(context);
            }
            return ((Object) super.n(context)) + ": " + this.I;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.b(this.I);
        }
    }

    public static final class b2 extends b {
        public b2() {
            super(59, R.string.action_read_clipboard, R.drawable.ic_read_clipboard);
        }
    }

    public static final class b3 extends b {
        private b3() {
            super(45, R.string.action_soft_reboot, R.drawable.ic_soft_reboot);
        }

        /* synthetic */ b3(a aVar) {
            this();
        }
    }

    private static final class c extends b {
        private c() {
            super(79, R.string.action_action_collection, R.drawable.ic_action_collection);
        }

        /* synthetic */ c(a aVar) {
            this();
        }
    }

    public static abstract class c0 extends b implements f.e0 {
        public final boolean I;

        protected c0(int i, boolean z) {
            super(i, R.string.action_command, R.drawable.ic_command);
            this.I = z;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I ? 1 : 0);
        }
    }

    private static final class c1 extends b {
        public final String I;

        private c1(f.s sVar) {
            this(Uri.decode(sVar.a()));
        }

        /* synthetic */ c1(f.s sVar, a aVar) {
            this(sVar);
        }

        private c1(String str) {
            super(56, R.string.action_inject_text, R.drawable.ic_inject_text);
            this.I = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public b1 A() {
            return new b1(this.I);
        }
    }

    public static final class c2 extends b {
        private c2() {
            super(44, R.string.action_reboot, R.drawable.ic_reboot);
        }

        /* synthetic */ c2(a aVar) {
            this();
        }
    }

    public static final class c3 extends b {
        public final String I;

        private c3() {
            this((String) null);
        }

        /* synthetic */ c3(a aVar) {
            this();
        }

        private c3(f.s sVar) {
            this(sVar.e());
        }

        /* synthetic */ c3(f.s sVar, a aVar) {
            this(sVar);
        }

        public c3(String str) {
            super(110, R.string.action_speech, R.drawable.ic_speech);
            this.I = str;
        }

        @Override // a.b
        public Drawable m(Context context) {
            String str = this.I;
            return (str == null || str.length() != 0) ? super.m(context) : context.getResources().getDrawable(R.drawable.ic_stop_speech);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            String str = this.I;
            if (str == null) {
                return super.n(context);
            }
            if (str.length() == 0) {
                return context.getText(R.string.stop_speech);
            }
            return ((Object) super.n(context)) + ": " + this.I;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.b(this.I);
        }
    }

    public static final class d extends x implements f.e0 {
        private static final String P = v0.f5a;
        public final b K;
        private final String L;
        private final String M;
        private int N;
        private final String O;

        private d() {
            this(null, null, null, null);
        }

        /* synthetic */ d(a aVar) {
            this();
        }

        public d(b bVar) {
            this(bVar, null, null, null);
        }

        private d(b bVar, String str, String str2, String str3) {
            super(72, R.string.action_editable_action, R.drawable.ic_edit);
            this.N = 0;
            this.K = bVar;
            this.L = str;
            this.M = str2;
            this.O = str3;
            v0.b(str3);
        }

        private d(f.s sVar) {
            this(sVar.g(), Uri.decode(sVar.a()), sVar.a(), sVar.a());
        }

        /* synthetic */ d(f.s sVar, a aVar) {
            this(sVar);
        }

        @Override // a.b.x
        protected CharSequence A(Context context) {
            b bVar = this.K;
            return bVar != null ? bVar.n(context) : context.getText(this.A);
        }

        public boolean B() {
            return (this.M == null && this.O == null) ? false : true;
        }

        public boolean C() {
            return this.L != null;
        }

        public d D(b bVar) {
            if (bVar.z != 72) {
                return new d(bVar, this.L, this.M, this.O);
            }
            d dVar = (d) bVar;
            String str = this.M;
            if (str == null && this.O == null) {
                b bVar2 = dVar.K;
                String str2 = this.L;
                if (str2 == null) {
                    str2 = dVar.L;
                }
                return new d(bVar2, str2, dVar.M, dVar.O);
            }
            b bVar3 = dVar.K;
            String str3 = this.L;
            if (str3 == null) {
                str3 = dVar.L;
            }
            return new d(bVar3, str3, str, this.O);
        }

        public d E() {
            return new d(this.K, this.L, null, null);
        }

        public d F(Bitmap bitmap, Context context) {
            d dVar = new d(this.K, this.L, null, v0.e(bitmap));
            if (dVar.O != null) {
                a.j.k(context, P + dVar.O);
            }
            return dVar;
        }

        public d G(String str) {
            return new d(this.K, this.L, null, str);
        }

        public d H(String str) {
            return new d(this.K, this.L, str, null);
        }

        public d I(String str) {
            return new d(this.K, str, this.M, this.O);
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            b bVar = this.K;
            return bVar != null ? bVar.l(sb, context, str) : super.l(sb, context, str);
        }

        @Override // a.b.x, a.b
        public CharSequence n(Context context) {
            String str = this.L;
            return str != null ? str : A(context);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.f(this.K).c(Uri.encode(this.L)).c(this.M).c(this.O);
        }

        @Override // a.b.x
        protected Drawable z(Context context) {
            Drawable drawable;
            try {
                if (this.M != null) {
                    Resources resources = context.getResources();
                    int identifier = this.N;
                    if (identifier == 0) {
                        identifier = resources.getIdentifier(this.M, "drawable", f.l.j);
                        this.N = identifier;
                    }
                    if (identifier > 0 && (drawable = resources.getDrawable(identifier)) != null) {
                        return drawable;
                    }
                }
                if (this.O != null) {
                    Drawable drawableH = f.q.h(P + this.O);
                    if (drawableH != null) {
                        return drawableH;
                    }
                }
            } catch (Throwable unused) {
            }
            b bVar = this.K;
            if (bVar != null) {
                return bVar.m(context);
            }
            return null;
        }
    }

    public static final class d0 extends b implements f.e0 {
        private static final h.o L = new h.o();
        public final a.h I;
        public final b J;
        public final b K;

        private d0() {
            this(null, null, null);
        }

        /* synthetic */ d0(a aVar) {
            this();
        }

        public d0(a.h hVar, b bVar, b bVar2) {
            super(62, R.string.action_conditional, R.drawable.ic_conditional);
            this.I = hVar == null ? L : hVar;
            this.J = bVar == null ? b.r() : bVar;
            this.K = bVar2 == null ? b.r() : bVar2;
        }

        private d0(f.s sVar) {
            this(a.h.d(sVar), sVar.g(), sVar.g());
        }

        /* synthetic */ d0(f.s sVar, a aVar) {
            this(sVar);
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            sb.append(str);
            sb.append("if ");
            sb.append(this.I.b(context));
            sb.append('\n');
            sb.append(str);
            sb.append("{\n");
            this.J.l(sb, context, str + "    ").append('\n');
            sb.append(str);
            sb.append('}');
            if (this.K.z != 1) {
                sb.append('\n');
                sb.append(str);
                sb.append("else\n");
                sb.append(str);
                sb.append("{\n");
                this.K.l(sb, context, str + "    ").append('\n');
                sb.append(str);
                sb.append('}');
            }
            return sb;
        }

        @Override // a.b
        public Drawable m(Context context) {
            return this.J.z != 1 ? this.K.z != 1 ? new g.n(null, this.J.o(context), null, this.K.o(context), context.getResources().getDrawable(R.drawable.ic_conditional_half)) : new g.n(null, this.J.o(context), null, null, context.getResources().getDrawable(R.drawable.ic_conditional_half)) : this.K.z != 1 ? new g.n(null, null, null, this.K.o(context), context.getResources().getDrawable(R.drawable.ic_conditional_half)) : super.m(context);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            StringBuilder sb;
            b bVar;
            if (this.I.z == 0 || (this.J.z == 1 && this.K.z == 1)) {
                return super.n(context);
            }
            if (this.K.z == 1) {
                sb = new StringBuilder();
                sb.append("if ");
                sb.append(this.I.b(context));
                sb.append(" { ");
                bVar = this.J;
            } else {
                sb = new StringBuilder();
                sb.append("if ");
                sb.append(this.I.b(context));
                sb.append(" { ");
                sb.append((Object) this.J.n(context));
                sb.append(" } else { ");
                bVar = this.K;
            }
            sb.append((Object) bVar.n(context));
            sb.append(" }");
            return sb.toString();
        }

        @Override // a.b
        public Drawable o(Context context) {
            return super.m(context);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            this.I.e(sVar);
            sVar.f(this.J).f(this.K);
        }
    }

    public static final class d1 extends k3 {
        public d1(int i) {
            super(30, R.string.action_keep_screen_on, R.drawable.ic_keep_screen_on, i);
        }
    }

    public static final class d2 extends b {
        private d2() {
            super(4, R.string.action_recent_apps, R.drawable.ic_recent_apps);
        }

        /* synthetic */ d2(a aVar) {
            this();
        }
    }

    private static final class d3 extends b {
        public final String I;

        private d3(f.s sVar) {
            this(A(sVar));
        }

        /* synthetic */ d3(f.s sVar, a aVar) {
            this(sVar);
        }

        private d3(String str) {
            super(58, R.string.action_speech, R.drawable.ic_speech);
            this.I = str;
        }

        private static String A(f.s sVar) {
            String strA = sVar.a();
            return strA != null ? Uri.decode(strA) : "";
        }

        /* JADX INFO: Access modifiers changed from: private */
        public c3 B() {
            return new c3(this.I);
        }
    }

    public static class e extends x {
        public final String K;
        public final String L;

        private e() {
            this((String) null, (String) null);
        }

        /* synthetic */ e(a aVar) {
            this();
        }

        private e(f.s sVar) {
            this(sVar.a(), sVar.a());
        }

        /* synthetic */ e(f.s sVar, a aVar) {
            this(sVar);
        }

        public e(String str, String str2) {
            super(48, R.string.action_activity, R.drawable.ic_activity);
            this.K = str;
            this.L = str2;
        }

        @Override // a.b.x
        protected CharSequence A(Context context) {
            if (this.L == null) {
                return null;
            }
            try {
                return f.z.K(context).z(this.K, this.L);
            } catch (Throwable unused) {
                return f.c0.C(this.K, this.L);
            }
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            String str2 = this.L;
            if (str2 == null) {
                return super.l(sb, context, str);
            }
            String strA = f.c0.A(this.K, str2);
            sb.append(str);
            sb.append(n(context));
            sb.append('\n');
            sb.append(str);
            sb.append("    ");
            sb.append(this.K);
            sb.append('/');
            sb.append(strA);
            return sb;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.c(this.K).c(this.L);
        }

        @Override // a.b.x
        protected Drawable z(Context context) {
            if (this.L != null) {
                return f.z.K(context).s(this.K, this.L);
            }
            return null;
        }
    }

    public static final class e0 extends b implements u0 {
        private static final int J;
        public final int I;

        static {
            J = Build.VERSION.SDK_INT >= 23 ? 6 : 4;
        }

        private e0(int i) {
            super(87, R.string.action_content, R.drawable.ic_content);
            this.I = i;
        }

        /* synthetic */ e0(int i, a aVar) {
            this(i);
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            int i2;
            if (i == 0) {
                i2 = android.R.string.cut;
            } else if (i == 1) {
                i2 = android.R.string.copy;
            } else if (i == 2) {
                i2 = android.R.string.paste;
            } else if (i == 3) {
                i2 = android.R.string.selectAll;
            } else if (i == 4) {
                i2 = R.string.undo;
            } else {
                if (i != 5) {
                    return super.n(context);
                }
                i2 = R.string.redo;
            }
            return context.getText(i2);
        }

        @Override // a.b.u0
        public int b() {
            return J;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new e0(i);
        }

        @Override // a.b
        public Drawable m(Context context) {
            Resources resources;
            int i;
            int i2 = this.I;
            if (i2 == 0) {
                resources = context.getResources();
                i = R.drawable.ic_content_cut;
            } else if (i2 == 1) {
                resources = context.getResources();
                i = R.drawable.ic_content_copy;
            } else if (i2 == 2) {
                resources = context.getResources();
                i = R.drawable.ic_content_paste;
            } else if (i2 == 3) {
                resources = context.getResources();
                i = R.drawable.ic_content_select_all;
            } else if (i2 == 4) {
                resources = context.getResources();
                i = R.drawable.ic_content_undo;
            } else {
                if (i2 != 5) {
                    return super.m(context);
                }
                resources = context.getResources();
                i = R.drawable.ic_content_redo;
            }
            return resources.getDrawable(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            return a(context, this.I);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class e1 extends k3 {
        public e1(int i) {
            super(35, R.string.action_key_enabled, R.drawable.ic_key_enabled, i);
        }
    }

    public static final class e2 extends b {
        private e2() {
            super(81, R.string.action_refreeze, R.drawable.ic_refreeze);
        }

        /* synthetic */ e2(a aVar) {
            this();
        }
    }

    public static final class e3 extends b {
        public e3() {
            super(96, R.string.action_split_screen, R.drawable.ic_split_screen);
        }
    }

    public static final class f extends x {
        public final String K;
        public final String L;
        public final int M;

        private f(f.s sVar) {
            this(sVar.a(), sVar.a(), sVar.h());
        }

        /* synthetic */ f(f.s sVar, a aVar) {
            this(sVar);
        }

        public f(String str, String str2, int i) {
            super(113, R.string.action_activity, R.drawable.ic_activity);
            this.K = str;
            this.L = str2;
            this.M = i;
        }

        @Override // a.b.x
        protected CharSequence A(Context context) {
            if (this.L == null) {
                return null;
            }
            try {
                return f.z.K(context).A(this.K, this.L, this.M);
            } catch (Throwable unused) {
                return this.K + '/' + f.c0.A(this.K, this.L) + f.c0.z + this.M + ")";
            }
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            String str2 = this.L;
            if (str2 == null) {
                return super.l(sb, context, str);
            }
            String strA = f.c0.A(this.K, str2);
            sb.append(str);
            sb.append(n(context));
            sb.append('\n');
            sb.append(str);
            sb.append("    ");
            sb.append(this.K);
            sb.append('/');
            sb.append(strA);
            sb.append(f.c0.z);
            sb.append(this.M);
            sb.append(')');
            return sb;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.c(this.K).c(this.L).d(this.M);
        }

        @Override // a.b.x
        protected Drawable z(Context context) {
            if (this.L != null) {
                return f.z.K(context).t(this.K, this.L, this.M);
            }
            return null;
        }
    }

    public static final class f0 extends b {
        private static a.n J;
        public final int I;

        private f0() {
            this(-1);
        }

        public f0(int i) {
            super(83, R.string.action_custom_panel, R.drawable.ic_custom_panel);
            this.I = i;
        }

        /* synthetic */ f0(a aVar) {
            this();
        }

        public static a.n A() {
            a.n nVar;
            synchronized (f0.class) {
                if (J == null) {
                    J = new a.n(f.l.k + "CUSTOM_PANELS", f.l.m + "panels");
                }
                nVar = J;
            }
            return nVar;
        }

        public static void B() {
            synchronized (f0.class) {
                J = null;
            }
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            if (this.I < 0) {
                return super.l(sb, context, str);
            }
            a.o oVarH = A().h(this.I);
            if (oVarH != null) {
                sb.append(str);
                sb.append(n(context));
                sb.append('(');
                sb.append(super.n(context));
                sb.append("): ");
                if (str.length() > q1.J) {
                    sb.append('\n');
                    sb.append(str);
                    sb.append("    ");
                    sb.append("...");
                    return sb;
                }
                b[] bVarArr = oVarH.f12b;
                if (bVarArr != null) {
                    String str2 = str + "    ";
                    for (b bVar : bVarArr) {
                        sb.append('\n');
                        bVar.l(sb, context, str2);
                    }
                }
            }
            return sb;
        }

        @Override // a.b
        public Drawable m(Context context) {
            a.o oVarH;
            return (this.I < 0 || (oVarH = A().h(this.I)) == null || oVarH.f12b == null) ? super.m(context) : new g.n(context, oVarH.f12b, null);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            CharSequence charSequenceN = super.n(context);
            if (this.I >= 0) {
                a.o oVarH = A().h(this.I);
                if (oVarH != null) {
                    return oVarH.f11a;
                }
                f.v.c("Panel id not found: " + this.I);
            }
            return charSequenceN;
        }

        @Override // a.b
        public Drawable o(Context context) {
            return super.m(context);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }

        public b[] z() {
            a.o oVarH = A().h(this.I);
            if (oVarH != null) {
                return oVarH.f12b;
            }
            throw new RuntimeException("Panel not exists!");
        }
    }

    public static final class f1 extends b {
        public final String I;

        public f1(String str) {
            super(77, R.string.action_kill_app, R.drawable.ic_kill_custom_app);
            this.I = str;
        }

        @Override // a.b
        public Drawable m(Context context) {
            if (this.I != null) {
                try {
                    return new g.t(f.z.K(context).C(this.I), context.getResources().getDrawable(R.drawable.ic_kill), 0.7f, true);
                } catch (Throwable unused) {
                }
            }
            return super.m(context);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I == null) {
                return super.n(context);
            }
            try {
                return ((Object) super.n(context)) + ": " + ((Object) f.z.K(context).G(this.I));
            } catch (Throwable unused) {
                return ((Object) super.n(context)) + ": " + this.I;
            }
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.c(this.I);
        }
    }

    public static final class f2 extends b implements f.e0 {
        public final int I;
        public final b J;

        private f2() {
            this(-1, b.r());
        }

        public f2(int i, b bVar) {
            super(101, R.string.action_repeat, R.drawable.ic_repeat);
            this.I = i;
            this.J = bVar;
        }

        /* synthetic */ f2(a aVar) {
            this();
        }

        private f2(f.s sVar) {
            this(sVar.h(), sVar.g());
        }

        /* synthetic */ f2(f.s sVar, a aVar) {
            this(sVar);
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            sb.append(str);
            sb.append(super.n(context));
            sb.append('(');
            sb.append(this.I);
            sb.append(")\n");
            sb.append(str);
            sb.append("{\n");
            this.J.l(sb, context, str + "    ").append('\n');
            sb.append(str);
            sb.append('}');
            return sb;
        }

        @Override // a.b
        public Drawable m(Context context) {
            return this.I <= 0 ? super.m(context) : new g.t(this.J.m(context), context.getResources().getDrawable(R.drawable.ic_repeat_corner), 0.8f, true);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I == -1) {
                return super.n(context);
            }
            return ((Object) super.n(context)) + ": " + ((Object) this.J.n(context));
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I).f(this.J);
        }
    }

    public static final class f3 extends b {
        public f3() {
            super(65537, 0, 0);
        }
    }

    public static class g extends x {
        public final Intent K;

        public g(Intent intent) {
            super(76, R.string.action_activity, R.drawable.ic_activity);
            this.K = intent;
        }

        private g(f.s sVar) {
            this(r2.E(sVar));
        }

        /* synthetic */ g(f.s sVar, a aVar) {
            this(sVar);
        }

        @Override // a.b.x
        protected CharSequence A(Context context) {
            if (this.K == null) {
                return null;
            }
            try {
                return f.z.K(context).x(this.K);
            } catch (Throwable unused) {
                return f.c0.B(this.K);
            }
        }

        public b B() {
            ComponentName component;
            Intent intent = this.K;
            if (intent == null || (component = intent.getComponent()) == null) {
                return null;
            }
            return new e(component.getPackageName(), component.getClassName());
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            if (this.K == null) {
                return super.l(sb, context, str);
            }
            sb.append(str);
            sb.append(n(context));
            sb.append('\n');
            sb.append(str);
            sb.append("    ");
            sb.append(this.K);
            return sb;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            Intent intent = this.K;
            sVar.b(intent != null ? intent.toUri(0) : null);
        }

        @Override // a.b.x
        protected Drawable z(Context context) {
            if (this.K != null) {
                return f.z.K(context).q(this.K);
            }
            return null;
        }
    }

    public static final class g0 extends b implements f.e0 {
        public final int I;

        private g0() {
            this(-1);
        }

        public g0(int i) {
            super(54, R.string.action_delay, R.drawable.ic_delay);
            this.I = i;
        }

        /* synthetic */ g0(a aVar) {
            this();
        }

        private g0(f.s sVar) {
            this(sVar.h());
        }

        /* synthetic */ g0(f.s sVar, a aVar) {
            this(sVar);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I == -1) {
                return super.n(context);
            }
            return ((Object) super.n(context)) + ": " + this.I + "ms";
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class g1 extends b {
        private g1() {
            super(37, R.string.action_kill_foreground_app, R.drawable.ic_kill_app);
        }

        /* synthetic */ g1(a aVar) {
            this();
        }
    }

    public static final class g2 extends b implements u0 {
        public final int I;

        private g2() {
            this(0);
        }

        private g2(int i) {
            super(70, R.string.action_ringer_mode, R.drawable.ic_ringer_mode);
            this.I = i;
        }

        /* synthetic */ g2(int i, a aVar) {
            this(i);
        }

        /* synthetic */ g2(a aVar) {
            this();
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            int i2;
            if (i == 0) {
                i2 = R.string.sub_action_switch;
            } else if (i == 1) {
                i2 = R.string.ringer_mode_silent;
            } else if (i == 2) {
                i2 = R.string.ringer_mode_vibrate;
            } else {
                if (i != 3) {
                    return null;
                }
                i2 = R.string.ringer_mode_normal;
            }
            return context.getText(i2);
        }

        @Override // a.b.u0
        public int b() {
            return 4;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new g2(i);
        }

        @Override // a.b
        public Drawable m(Context context) {
            Resources resources;
            int i;
            int i2 = this.I;
            if (i2 == 1) {
                resources = context.getResources();
                i = R.drawable.ic_ringer_mode_silent;
            } else {
                if (i2 != 2) {
                    return super.m(context);
                }
                resources = context.getResources();
                i = R.drawable.ic_ringer_mode_vibrate;
            }
            return resources.getDrawable(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I == 0) {
                return super.n(context);
            }
            return ((Object) super.n(context)) + ": " + ((Object) a(context, this.I));
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class g3 extends b {
        private g3() {
            super(112, R.string.action_stop_all, R.drawable.ic_stop_all);
        }

        /* synthetic */ g3(a aVar) {
            this();
        }
    }

    public static final class h extends x {
        public final Intent K;
        public final int L;

        public h(Intent intent, int i) {
            super(114, R.string.action_activity, R.drawable.ic_activity);
            this.K = intent;
            this.L = i;
        }

        private h(f.s sVar) {
            this(r2.E(sVar), sVar.h());
        }

        /* synthetic */ h(f.s sVar, a aVar) {
            this(sVar);
        }

        @Override // a.b.x
        protected CharSequence A(Context context) {
            if (this.K == null) {
                return null;
            }
            try {
                return f.z.K(context).y(this.K, this.L);
            } catch (Throwable unused) {
                return f.c0.B(this.K) + f.c0.z + this.L + ")";
            }
        }

        public b B() {
            ComponentName component;
            Intent intent = this.K;
            if (intent == null || (component = intent.getComponent()) == null) {
                return null;
            }
            return new f(component.getPackageName(), component.getClassName(), this.L);
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            if (this.K == null) {
                return super.l(sb, context, str);
            }
            sb.append(str);
            sb.append(n(context));
            sb.append('\n');
            sb.append(str);
            sb.append("    ");
            sb.append(this.K);
            sb.append(f.c0.z);
            sb.append(this.L);
            sb.append(')');
            return sb;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            Intent intent = this.K;
            sVar.b(intent != null ? intent.toUri(0) : null).d(this.L);
        }

        @Override // a.b.x
        protected Drawable z(Context context) {
            if (this.K != null) {
                return f.z.K(context).r(this.K, this.L);
            }
            return null;
        }
    }

    public static final class h0 extends b {
        public h0() {
            super(75, R.string.action_dismiss_keyguard, R.drawable.ic_dismiss_keyguard);
        }
    }

    public static final class h1 extends b {
        private h1() {
            super(36, R.string.action_last_app, R.drawable.ic_last_app);
        }

        /* synthetic */ h1(a aVar) {
            this();
        }
    }

    public static final class h2 extends b implements u0 {
        public final int I;

        private h2() {
            this(0);
        }

        private h2(int i) {
            super(74, R.string.action_rotate_screen, R.drawable.ic_rotate_screen);
            this.I = i;
        }

        /* synthetic */ h2(int i, a aVar) {
            this(i);
        }

        /* synthetic */ h2(a aVar) {
            this();
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            int i2;
            switch (i) {
                case 1:
                    i2 = R.string.rotate_portrait;
                    break;
                case 2:
                    i2 = R.string.rotate_landscape;
                    break;
                case 3:
                    i2 = R.string.rotate_upside_down;
                    break;
                case 4:
                    i2 = R.string.rotate_seascape;
                    break;
                case 5:
                    i2 = R.string.rotate_90;
                    break;
                case 6:
                    i2 = R.string.rotate_180;
                    break;
                case 7:
                    i2 = R.string.rotate_270;
                    break;
                default:
                    return super.n(context);
            }
            return context.getText(i2);
        }

        @Override // a.b.u0
        public int b() {
            return 8;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new h2(i);
        }

        @Override // a.b
        public Drawable m(Context context) {
            Resources resources;
            int i;
            int i2 = this.I;
            if (i2 == 1) {
                resources = context.getResources();
                i = R.drawable.ic_rotate_portrait;
            } else {
                if (i2 != 2) {
                    if (i2 != 3) {
                        if (i2 != 4) {
                            return super.m(context);
                        }
                    }
                    resources = context.getResources();
                    i = R.drawable.ic_rotate_portrait;
                }
                resources = context.getResources();
                i = R.drawable.ic_rotate_landscape;
            }
            return resources.getDrawable(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            return a(context, this.I);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class h3 extends b implements f.e0 {
        public static final int[] J = {R.string.gesture_hold, R.string.gesture_swipe_left, R.string.gesture_swipe_right, R.string.gesture_swipe_up, R.string.gesture_swipe_down};
        public static final b[] K = {b.r(), b.r(), b.r(), b.r(), b.r()};
        public final b[] I;

        public h3() {
            this(K);
        }

        private h3(f.s sVar) {
            this(new b[]{sVar.g(), sVar.g(), sVar.g(), sVar.g(), sVar.g()});
        }

        /* synthetic */ h3(f.s sVar, a aVar) {
            this(sVar);
        }

        public h3(b[] bVarArr) {
            super(107, R.string.action_sub_gestures, R.drawable.ic_sub_gesture);
            this.I = bVarArr;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            for (b bVar : this.I) {
                bVar.y(sVar);
            }
        }
    }

    public static final class i extends b {
        public final int I;
        public final int J;

        private i() {
            this(-1, 0);
        }

        public i(int i, int i2) {
            super(89, R.string.action_adjust, R.drawable.ic_adjust);
            this.I = i;
            this.J = i2;
        }

        /* synthetic */ i(a aVar) {
            this();
        }

        public static int A(Context context, int i) {
            if (i == 0) {
                return 15;
            }
            if (i == 1) {
                return f.g.f438b;
            }
            if (i != 2) {
                return f.q0.a(context, i - 3);
            }
            return 0;
        }

        public static int B(int i) {
            if (i == 0) {
                return 1;
            }
            if (i != 1) {
                return 0;
            }
            return f.g.f437a;
        }

        public static CharSequence C(Context context, int i) {
            return i != 0 ? i != 1 ? f.q0.d(context, i - 3) : context.getText(R.string.adjust_brightness) : context.getText(R.string.adjust_screen_filter_alpha);
        }

        public static int D(int i) {
            return i != 2 ? 3 : 2;
        }

        public static CharSequence a(Context context, int i) {
            return context.getText(i != 0 ? i != 1 ? R.string.set_level : R.string.adjust_down : R.string.adjust_up);
        }

        public static int z(Context context, int i) {
            if (i == 0) {
                return -1;
            }
            if (i == 1) {
                return f.g.a(context);
            }
            if (i != 2) {
                return f.q0.b(context, i - 3);
            }
            return -1;
        }

        @Override // a.b
        public Drawable m(Context context) {
            Resources resources;
            int i;
            int i2 = this.I;
            if (i2 < 0) {
                return super.m(context);
            }
            switch (i2) {
                case 0:
                    resources = context.getResources();
                    i = R.drawable.ic_screen_filter;
                    break;
                case 1:
                    return context.getResources().getDrawable(this.J == -2 ? R.drawable.ic_brightness_down : R.drawable.ic_brightness_up);
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                    int i3 = this.J;
                    resources = context.getResources();
                    if (i3 == -2) {
                        i = R.drawable.ic_volume_down;
                    } else if (i3 == 0) {
                        i = R.drawable.ic_volume_mute;
                    } else {
                        i = R.drawable.ic_volume_up;
                    }
                    break;
                default:
                    return super.m(context);
            }
            return resources.getDrawable(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            int i = this.I;
            if (i < 0) {
                return super.n(context);
            }
            int i2 = this.J;
            if (i2 == -2) {
                return context.getString(R.string.down_f, C(context, i));
            }
            if (i2 == -1) {
                return context.getString(R.string.up_f, C(context, i));
            }
            return ((Object) C(context, this.I)) + ": " + this.J;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I).d(this.J);
        }
    }

    public static final class i0 extends b {
        private i0() {
            super(15, R.string.action_expand_notifications_panel, R.drawable.ic_notifications_panel);
        }

        /* synthetic */ i0(a aVar) {
            this();
        }
    }

    public static final class i1 extends k3 {
        public i1(int i) {
            super(23, R.string.action_location, R.drawable.ic_location, i);
        }
    }

    public static final class i2 extends b {
        private i2() {
            super(55, R.string.action_running_apps_drawer, R.drawable.ic_running_apps_panel);
        }

        /* synthetic */ i2(a aVar) {
            this();
        }
    }

    public static final class i3 extends b {
        public final int I;
        public final boolean J;

        private i3() {
            this(-1, false);
        }

        public i3(int i, boolean z) {
            super(86, R.string.action_successive_adjust, R.drawable.ic_successive_adjust);
            this.I = i;
            this.J = z;
        }

        /* synthetic */ i3(a aVar) {
            this();
        }

        public static CharSequence A(Context context, int i, boolean z) {
            if (i == 13) {
                return context.getText(z ? R.string.scroll_up : R.string.scroll_down);
            }
            if (i != 14) {
                return context.getText(z ? R.string.adjust_up : R.string.adjust_down);
            }
            return context.getText(z ? R.string.caret_left : R.string.caret_right);
        }

        public static CharSequence z(Context context, int i) {
            return i != 0 ? i != 1 ? i != 13 ? i != 14 ? f.q0.d(context, i - 3) : context.getText(R.string.adjust_caret) : context.getText(R.string.adjust_scroll) : context.getText(R.string.adjust_brightness) : context.getText(R.string.adjust_screen_filter_alpha);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            int i = this.I;
            switch (i) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                    return this.J ? context.getString(R.string.up_f, z(context, i)) : context.getString(R.string.down_f, z(context, i));
                case 13:
                    return context.getText(this.J ? R.string.scroll_up : R.string.scroll_down);
                case 14:
                    return context.getText(this.J ? R.string.caret_left : R.string.caret_right);
                default:
                    return super.n(context);
            }
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I).d(this.J ? 1 : 0);
        }
    }

    public static final class j extends b {
        public final int I;
        public final int J;

        public j(int i, int i2) {
            super(60, R.string.action_adjust_volume, R.drawable.ic_volume_up);
            this.I = i;
            this.J = i2;
        }

        private j(f.s sVar) {
            this(sVar.h(), sVar.h());
        }

        /* synthetic */ j(f.s sVar, a aVar) {
            this(sVar);
        }

        public static CharSequence z(Context context, int i) {
            int i2;
            if (i == -2) {
                i2 = R.string.adjust_down;
            } else if (i == -1) {
                i2 = R.string.adjust_up;
            } else {
                if (i != 0) {
                    return Integer.toString(i);
                }
                i2 = R.string.mute;
            }
            return context.getString(i2);
        }

        public i A() {
            return new i(this.I + 2, this.J);
        }

        @Override // a.b
        public Drawable m(Context context) {
            Resources resources;
            int i;
            if (this.I != -2) {
                int i2 = this.J;
                if (i2 == -2) {
                    resources = context.getResources();
                    i = R.drawable.ic_volume_down;
                } else if (i2 == 0) {
                    resources = context.getResources();
                    i = R.drawable.ic_volume_mute;
                }
                return resources.getDrawable(i);
            }
            return super.m(context);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I == -2) {
                return super.n(context);
            }
            return ((Object) f.q0.d(context, this.I)) + " " + ((Object) z(context, this.J));
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I).d(this.J);
        }
    }

    public static final class j0 extends b {
        private j0() {
            super(16, R.string.action_expand_quick_settings_panel, R.drawable.ic_quick_settings_panel);
        }

        /* synthetic */ j0(a aVar) {
            this();
        }
    }

    public static final class j1 extends b implements u0 {
        public final int I;

        private j1(int i) {
            super(104, R.string.action_media_control, R.drawable.ic_media_play_pause);
            this.I = i;
        }

        /* synthetic */ j1(int i, a aVar) {
            this(i);
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            int i2;
            if (i == 0) {
                i2 = R.string.action_media_play;
            } else if (i == 1) {
                i2 = R.string.action_media_play_pause;
            } else if (i == 2) {
                i2 = R.string.action_media_stop;
            } else if (i == 3) {
                i2 = R.string.action_media_previous;
            } else {
                if (i != 4) {
                    return super.n(context);
                }
                i2 = R.string.action_media_next;
            }
            return context.getText(i2);
        }

        @Override // a.b.u0
        public int b() {
            return 5;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new j1(i);
        }

        @Override // a.b
        public Drawable m(Context context) {
            int i;
            int i2 = this.I;
            if (i2 == 0) {
                i = R.drawable.ic_media_play;
            } else if (i2 == 1) {
                i = R.drawable.ic_media_play_pause;
            } else if (i2 == 2) {
                i = R.drawable.ic_media_stop;
            } else if (i2 == 3) {
                i = R.drawable.ic_media_previous;
            } else {
                if (i2 != 4) {
                    return super.m(context);
                }
                i = R.drawable.ic_media_next;
            }
            return context.getResources().getDrawable(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            return a(context, this.I);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class j2 {

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private static final String f3b = v0.f5a;

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final String[] f4a;

        public j2() {
            File file = new File(f3b);
            if (file.exists()) {
                this.f4a = file.list();
            } else {
                this.f4a = new String[0];
            }
        }

        public static Drawable c(String str) {
            return f.q.h(f3b + str);
        }

        public int a() {
            return this.f4a.length;
        }

        public Drawable b(int i) {
            return f.q.h(f3b + this.f4a[i]);
        }

        public String d(int i) {
            return this.f4a[i];
        }
    }

    public static final class j3 extends k3 {
        public j3(int i) {
            super(26, R.string.action_sync, R.drawable.ic_sync, i);
        }
    }

    public static final class k extends k3 {
        public k(int i) {
            super(28, R.string.action_airplane_mode, R.drawable.ic_airplane_mode, i);
        }
    }

    public static final class k0 extends b implements u0 {
        public final int I;

        private k0() {
            this(-1);
        }

        public k0(int i) {
            super(64, R.string.action_fast_scroll, R.drawable.ic_fast_scroll);
            this.I = i;
        }

        /* synthetic */ k0(a aVar) {
            this();
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            return context.getText(i == 0 ? R.string.scroll_to_top : R.string.scroll_to_bottom);
        }

        @Override // a.b.u0
        public int b() {
            return 2;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new k0(i == 0 ? 0 : 1);
        }

        @Override // a.b
        public Drawable m(Context context) {
            Resources resources;
            int i;
            int i2 = this.I;
            if (i2 == 0) {
                resources = context.getResources();
                i = R.drawable.ic_scroll_to_top;
            } else {
                if (i2 != 1) {
                    return super.m(context);
                }
                resources = context.getResources();
                i = R.drawable.ic_scroll_to_bottom;
            }
            return resources.getDrawable(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            int i;
            int i2 = this.I;
            if (i2 == 0) {
                i = R.string.scroll_to_top;
            } else {
                if (i2 != 1) {
                    return super.n(context);
                }
                i = R.string.scroll_to_bottom;
            }
            return context.getText(i);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class k1 extends b {
        private k1() {
            super(12, R.string.action_media_next, R.drawable.ic_media_next);
        }

        /* synthetic */ k1(a aVar) {
            this();
        }
    }

    public static final class k2 extends b implements f.e0 {
        private static a.n J;
        public final int I;

        private k2() {
            this(-1);
        }

        public k2(int i) {
            super(84, R.string.action_saved_multi_action, R.drawable.ic_saved_multi_action);
            this.I = i;
        }

        /* synthetic */ k2(a aVar) {
            this();
        }

        public static a.n A() {
            a.n nVar;
            synchronized (k2.class) {
                if (J == null) {
                    J = new a.n(f.l.k + "MULTI_ACTION", f.l.m + "multi_actions");
                }
                nVar = J;
            }
            return nVar;
        }

        public static void B() {
            synchronized (k2.class) {
                J = null;
            }
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            a.o oVarH;
            if (this.I < 0 || (oVarH = A().h(this.I)) == null) {
                return super.l(sb, context, str);
            }
            sb.append(str);
            sb.append(n(context));
            sb.append('(');
            sb.append(super.n(context));
            sb.append("): ");
            if (str.length() > q1.J) {
                sb.append('\n');
                sb.append(str);
                sb.append("    ");
                sb.append("...");
                return sb;
            }
            b[] bVarArr = oVarH.f12b;
            if (bVarArr != null) {
                String str2 = str + "    ";
                for (b bVar : bVarArr) {
                    sb.append('\n');
                    bVar.l(sb, context, str2);
                }
            }
            return sb;
        }

        @Override // a.b
        public Drawable m(Context context) {
            a.o oVarH;
            return (this.I < 0 || (oVarH = A().h(this.I)) == null || oVarH.f12b == null) ? super.m(context) : new g.w(context, oVarH.f12b, context.getResources().getDrawable(R.drawable.ic_save_small));
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I >= 0) {
                a.o oVarH = A().h(this.I);
                if (oVarH != null) {
                    return oVarH.f11a;
                }
                f.v.c("Saved multi-action id not found: " + this.I);
            }
            return super.n(context);
        }

        @Override // a.b
        public Drawable o(Context context) {
            return super.m(context);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }

        public b[] z() {
            a.o oVarH = A().h(this.I);
            if (oVarH != null) {
                return oVarH.f12b;
            }
            throw new RuntimeException("Multi-action not exists!");
        }
    }

    public static abstract class k3 extends b implements Cloneable, u0 {
        public final int I;

        protected k3(int i, int i2, int i3, int i4) {
            super(i, i2, i3);
            this.I = i4;
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            return context.getText(i == 1 ? R.string.turn_on : i == 2 ? R.string.turn_off : R.string.toggle);
        }

        @Override // a.b.u0
        public int b() {
            return 3;
        }

        @Override // a.b.u0
        public b c(int i) {
            return z(i);
        }

        @Override // a.b
        public Drawable m(Context context) {
            Drawable drawableM = super.m(context);
            int i = this.I;
            return i == 1 ? new g.t(drawableM, context.getResources().getDrawable(R.drawable.ic_on_corner), 0.8f, true) : i == 2 ? new g.t(drawableM, context.getResources().getDrawable(R.drawable.ic_off_corner), 0.8f, true) : drawableM;
        }

        @Override // a.b
        public CharSequence n(Context context) {
            int i = this.I;
            return i != 1 ? i != 2 ? super.n(context) : context.getString(R.string.turn_off_f, super.n(context)) : context.getString(R.string.turn_on_f, super.n(context));
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }

        public k3 z(int i) {
            try {
                return (k3) getClass().getConstructor(Integer.TYPE).newInstance(Integer.valueOf(i));
            } catch (Throwable th) {
                f.v.d(th);
                return null;
            }
        }
    }

    public static final class l extends x {
        public final String K;
        public final String L;
        private final int M;

        private l() {
            this(null, null, 0);
        }

        /* synthetic */ l(a aVar) {
            this();
        }

        private l(f.s sVar) {
            this(sVar.a(), sVar.a(), sVar.h());
        }

        /* synthetic */ l(f.s sVar, a aVar) {
            this(sVar);
        }

        public l(String str, String str2, int i) {
            super(115, R.string.action_app, R.drawable.ic_app);
            this.K = str;
            this.L = str2;
            this.M = i;
        }

        @Override // a.b.x
        protected CharSequence A(Context context) {
            if (this.K == null) {
                return null;
            }
            int iB = B();
            try {
                return this.L != null ? f.z.K(context).A(this.K, this.L, iB) : f.z.K(context).H(this.K, iB);
            } catch (Throwable unused) {
                if (iB == 0) {
                    return this.K;
                }
                return this.K + f.c0.z + iB + ")";
            }
        }

        public int B() {
            int iD = f.o0.d();
            int i = this.M;
            return i != 0 ? i : iD;
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            sb.append(str);
            sb.append(n(context));
            sb.append('\n');
            sb.append(str);
            sb.append("    ");
            sb.append(this.K);
            if (this.M != 0) {
                sb.append(f.c0.z);
                sb.append(this.M);
                sb.append(')');
            }
            return sb;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.c(this.K);
            sVar.c(this.L);
            sVar.d(this.M);
        }

        @Override // a.b.x
        protected Drawable z(Context context) {
            if (this.K == null) {
                return null;
            }
            int iB = B();
            String str = this.L;
            f.z zVarK = f.z.K(context);
            return str != null ? zVarK.t(this.K, this.L, iB) : zVarK.D(this.K, iB);
        }
    }

    public static final class l0 extends b {
        private l0() {
            super(99, R.string.action_finish_activity, R.drawable.ic_finish_activity);
        }

        /* synthetic */ l0(a aVar) {
            this();
        }
    }

    public static final class l1 extends b {
        private l1() {
            super(9, R.string.action_media_play_pause, R.drawable.ic_media_play_pause);
        }

        /* synthetic */ l1(a aVar) {
            this();
        }
    }

    public static final class l2 extends b implements f.e0 {
        private static final String J = f.l.l + "lists/";
        private final String I;

        private l2(String str) {
            super(50, R.string.action_saved_multi_action, R.drawable.ic_saved_multi_action);
            this.I = str;
        }

        /* synthetic */ l2(String str, a aVar) {
            this(str);
        }

        b z() {
            String str = this.I;
            return str != null ? new k2(Integer.parseInt(str, 16)) : b.r();
        }
    }

    public static final class l3 extends k3 {
        public l3(int i) {
            super(32, R.string.action_torch, R.drawable.ic_torch, i);
        }
    }

    public static final class m extends x {
        public final String K;

        private m() {
            this((String) null);
        }

        private m(f.s sVar) {
            this(sVar.a());
        }

        /* synthetic */ m(f.s sVar, a aVar) {
            this(sVar);
        }

        public m(String str) {
            super(47, R.string.action_app, R.drawable.ic_app);
            this.K = str;
        }

        @Override // a.b.x
        protected CharSequence A(Context context) {
            if (this.K == null) {
                return null;
            }
            try {
                return f.z.K(context).G(this.K);
            } catch (Throwable unused) {
                return this.K;
            }
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            sb.append(str);
            sb.append(n(context));
            sb.append('\n');
            sb.append(str);
            sb.append("    ");
            sb.append(this.K);
            return sb;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.c(this.K);
        }

        @Override // a.b.x
        protected Drawable z(Context context) {
            if (this.K != null) {
                return f.z.K(context).C(this.K);
            }
            return null;
        }
    }

    public static final class m0 extends b {
        public final int I;

        public m0() {
            this(0);
        }

        public m0(int i) {
            super(94, R.string.action_floating_widget, R.drawable.ic_floating_widget);
            this.I = i;
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I != 0) {
                try {
                    AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(context).getAppWidgetInfo(this.I);
                    if (appWidgetInfo != null) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            return ((Object) super.n(context)) + ": " + appWidgetInfo.loadLabel(context.getPackageManager());
                        }
                        return ((Object) super.n(context)) + ": " + appWidgetInfo.label;
                    }
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            return super.n(context);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class m1 extends b {
        private m1() {
            super(11, R.string.action_media_previous, R.drawable.ic_media_previous);
        }

        /* synthetic */ m1(a aVar) {
            this();
        }
    }

    public static final class m2 extends k3 {
        public m2(int i) {
            super(33, R.string.action_screen_filter, R.drawable.ic_screen_filter, i);
        }
    }

    public static final class m3 extends b {
        public m3() {
            super(98, R.string.action_universal_copy, R.drawable.ic_universal_copy);
        }
    }

    public static final class n extends b {
        private n() {
            super(71, R.string.action_app_option_menu, R.drawable.ic_app_option_menu);
        }

        /* synthetic */ n(a aVar) {
            this();
        }
    }

    public static final class n0 extends b {
        private n0() {
            super(42, R.string.action_freezer_drawer, R.drawable.ic_freezer_panel);
        }

        /* synthetic */ n0(a aVar) {
            this();
        }
    }

    public static final class n1 extends b {
        private n1() {
            super(10, R.string.action_media_stop, R.drawable.ic_media_stop);
        }

        /* synthetic */ n1(a aVar) {
            this();
        }
    }

    public static final class n2 extends b implements u0 {
        public final int I;

        public n2(int i) {
            super(95, R.string.action_screenshot, R.drawable.ic_screenshot);
            this.I = i;
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            return context.getText(i == 1 ? R.string.screenshot_selected_region : R.string.screenshot_fullscreen);
        }

        @Override // a.b.u0
        public int b() {
            return 2;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new n2(i == 0 ? 1 : 2);
        }

        @Override // a.b
        public Drawable m(Context context) {
            return this.I == 2 ? context.getResources().getDrawable(R.drawable.ic_screenshot_selected_region) : super.m(context);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            return this.I == 2 ? context.getText(R.string.screenshot_selected_region) : super.n(context);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class n3 extends b {
        public final int I;

        private n3() {
            this(0);
        }

        public n3(int i) {
            super(92, R.string.action_vibrate, R.drawable.ic_vibrate);
            this.I = i;
        }

        /* synthetic */ n3(a aVar) {
            this();
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            if (this.I <= 0) {
                return super.l(sb, context, str);
            }
            sb.append(str);
            sb.append(n(context));
            sb.append(": ");
            sb.append(this.I);
            return sb;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class o extends b implements u0 {
        public final int I;

        private o() {
            this(0);
        }

        private o(int i) {
            super(82, R.string.action_apps_drawer, R.drawable.ic_apps_panel);
            this.I = i;
        }

        /* synthetic */ o(int i, a aVar) {
            this(i);
        }

        /* synthetic */ o(a aVar) {
            this();
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            int i2;
            if (i == 1) {
                i2 = R.string.system_apps_drawer;
            } else {
                if (i != 2) {
                    return super.n(context);
                }
                i2 = R.string.download_apps_drawer;
            }
            return context.getText(i2);
        }

        @Override // a.b.u0
        public int b() {
            return 3;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new o(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            return a(context, this.I);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class o0 extends k3 {
        public o0(int i) {
            super(97, R.string.action_game_mode, R.drawable.ic_game_mode, i);
        }
    }

    public static final class o1 extends b {
        private o1() {
            super(5, R.string.action_menu, R.drawable.ic_menu);
        }

        /* synthetic */ o1(a aVar) {
            this();
        }
    }

    public static final class o2 extends b {
        public o2() {
            super(17, R.string.action_screenshot, R.drawable.ic_screenshot);
        }
    }

    public static final class o3 extends k3 {
        public o3(int i) {
            super(21, R.string.action_wifi, R.drawable.ic_wifi, i);
        }
    }

    private static final class p extends b {
        private p() {
            super(41, R.string.action_apps_drawer, R.drawable.ic_apps_panel);
        }

        /* synthetic */ p(a aVar) {
            this();
        }

        public o z() {
            return new o((a) null);
        }
    }

    public static final class p0 extends k3 {
        public p0(int i) {
            super(34, R.string.action_gesture_enabled, R.drawable.ic_gesture_enabled, i);
        }
    }

    public static final class p1 extends k3 {
        public p1(int i) {
            super(22, R.string.action_mobile_data, R.drawable.ic_mobile_data, i);
        }
    }

    public static final class p2 extends b implements f.e0 {
        public final String I;
        public final String J;
        public final String K;
        private f.i L;

        private p2() {
            this(null, null, null);
        }

        /* synthetic */ p2(a aVar) {
            this();
        }

        private p2(f.s sVar) {
            this(sVar.a(), sVar.a(), sVar.e());
        }

        /* synthetic */ p2(f.s sVar, a aVar) {
            this(sVar);
        }

        public p2(String str, String str2, String str3) {
            super(88, R.string.action_set_variable, R.drawable.ic_set_variable);
            this.L = null;
            this.I = str;
            this.J = str2;
            this.K = str3;
        }

        private int z(i.f fVar) {
            if (this.L == null) {
                this.L = f.i.d(this.K);
            }
            return this.L.a(fVar);
        }

        public int A(i.f fVar) {
            int iZ;
            f.t tVarB = fVar.b(this.I);
            if (tVarB == null) {
                throw new RuntimeException("Variable not exists: " + this.I);
            }
            String str = this.J;
            str.hashCode();
            switch (str) {
                case "=":
                    int iZ2 = z(fVar);
                    tVarB.f488a = iZ2;
                    return iZ2;
                case "+=":
                    iZ = tVarB.f488a + z(fVar);
                    break;
                case "-=":
                    iZ = tVarB.f488a - z(fVar);
                    break;
                default:
                    throw new RuntimeException("Invalid operator: " + this.J);
            }
            tVarB.f488a = iZ;
            return iZ;
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I == null) {
                return super.n(context);
            }
            return this.I + ' ' + this.J + ' ' + this.K;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.c(this.I).c(this.J).b(this.K);
        }
    }

    public static final class p3 extends k3 {
        public p3(int i) {
            super(27, R.string.action_wifi_ap, R.drawable.ic_wifi_ap, i);
        }
    }

    public static final class q extends b {
        public q() {
            super(0, R.string.action_as_default, R.drawable.ic_as_default);
        }
    }

    public static final class q0 extends b implements u0 {
        public final int I;

        private q0(int i) {
            super(106, R.string.action_gesture_pointer, R.drawable.ic_gesture_pointer);
            this.I = i;
        }

        /* synthetic */ q0(int i, a aVar) {
            this(i);
        }

        public static CharSequence z(Context context, int i) {
            int[] iArr = p.f.i;
            if (i > iArr.length) {
                i = 0;
            }
            return context.getText(iArr[i]);
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            return z(context, i);
        }

        @Override // a.b.u0
        public int b() {
            return p.f.i.length;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new q0(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I == 0) {
                return super.n(context);
            }
            return ((Object) super.n(context)) + ": " + ((Object) z(context, this.I));
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class q1 extends b implements f.e0 {
        static final int J = 40;
        public final b[] I;

        private q1() {
            this((b[]) null);
        }

        /* synthetic */ q1(a aVar) {
            this();
        }

        private q1(f.s sVar) {
            this(z(sVar));
        }

        /* synthetic */ q1(f.s sVar, a aVar) {
            this(sVar);
        }

        public q1(List<b> list) {
            this((b[]) list.toArray(new b[list.size()]));
        }

        public q1(b[] bVarArr) {
            super(63, R.string.action_multi_action, R.drawable.ic_multi_action);
            this.I = bVarArr;
        }

        private static b[] z(f.s sVar) {
            int iH = sVar.h();
            b[] bVarArr = new b[iH];
            for (int i = 0; i < iH; i++) {
                bVarArr[i] = sVar.g();
            }
            return bVarArr;
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            if (this.I != null) {
                if (str.length() > J) {
                    sb.append(str);
                    sb.append(super.n(context));
                    return sb;
                }
                int length = this.I.length;
                for (int i = 0; i < length; i++) {
                    if (i != 0) {
                        sb.append('\n');
                    }
                    this.I[i].l(sb, context, str);
                }
            }
            return sb;
        }

        @Override // a.b
        public Drawable m(Context context) {
            b[] bVarArr = this.I;
            return (bVarArr == null || bVarArr.length == 0) ? super.m(context) : new g.w(context, this.I);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            b[] bVarArr = this.I;
            if (bVarArr == null || bVarArr.length == 0) {
                return super.n(context);
            }
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (b bVar : this.I) {
                sb.append(' ');
                sb.append(bVar.n(context));
            }
            sb.append(" ]");
            return sb;
        }

        @Override // a.b
        public Drawable o(Context context) {
            return super.m(context);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            b[] bVarArr = this.I;
            if (bVarArr == null) {
                sVar.d(0);
                return;
            }
            sVar.d(bVarArr.length);
            for (b bVar : this.I) {
                sVar.f(bVar);
            }
        }
    }

    public static final class q2 extends s2 {
        private q2(Intent intent, String str, String str2, String str3) {
            super(69, intent, str, str2, str3);
        }

        private q2(f.s sVar) {
            this(D(sVar), Uri.decode(sVar.a()), sVar.a(), sVar.a());
        }

        /* synthetic */ q2(f.s sVar, a aVar) {
            this(sVar);
        }

        private static Intent D(f.s sVar) {
            try {
                if (!(sVar instanceof f.h)) {
                    return Intent.parseUri(sVar.a(), 0);
                }
                int iH = sVar.h();
                StringBuilder sb = new StringBuilder(sVar.a());
                for (int i = 0; i < iH; i++) {
                    sb.append(' ');
                    sb.append(sVar.a());
                }
                return Intent.parseUri(sb.toString(), 0);
            } catch (Throwable th) {
                f.v.d(th);
                return null;
            }
        }

        @Override // a.b.s2, a.b
        public void y(f.s sVar) {
            f.s sVarC;
            String str;
            try {
                int i = 0;
                String uri = this.K.toUri(0);
                if (sVar instanceof f.h) {
                    int i2 = 0;
                    while (true) {
                        int iIndexOf = uri.indexOf(32, i);
                        if (iIndexOf == -1) {
                            break;
                        }
                        i2++;
                        i = iIndexOf + 1;
                    }
                    sVarC = sVar.d(this.z).d(i2).c(uri).c(Uri.encode(this.L)).c(this.M);
                    str = this.N;
                } else {
                    sVarC = sVar.d(this.z).c(uri).c(Uri.encode(this.L)).c(this.M);
                    str = this.N;
                }
                sVarC.c(str);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    public static final class r extends k3 {
        public r(int i) {
            super(19, R.string.action_auto_brightness, R.drawable.ic_auto_brightness, i);
        }
    }

    public static final class r0 extends k3 {
        public r0(int i) {
            super(31, R.string.action_gesture_record, R.drawable.ic_gesture_record, i);
        }
    }

    public static final class r1 extends b {
        private r1() {
            super(67, R.string.action_next_app, R.drawable.ic_next_app);
        }

        /* synthetic */ r1(a aVar) {
            this();
        }
    }

    public static final class r2 extends s2 {
        private r2() {
            this((Intent) null, (String) null, (String) null, (String) null);
        }

        /* synthetic */ r2(a aVar) {
            this();
        }

        public r2(Intent intent, String str, Intent.ShortcutIconResource shortcutIconResource) {
            this(intent, str, shortcutIconResource.resourceName, shortcutIconResource.packageName);
        }

        public r2(Intent intent, String str, Bitmap bitmap, Context context) {
            this(intent, str, v0.e(bitmap), (String) null);
            if (this.M != null) {
                a.j.k(context, s2.O + this.M);
            }
        }

        public r2(Intent intent, String str, String str2) {
            this(intent, str, (String) null, str2);
        }

        private r2(Intent intent, String str, String str2, String str3) {
            super(78, intent, str, str2, str3);
        }

        /* synthetic */ r2(Intent intent, String str, String str2, String str3, a aVar) {
            this(intent, str, str2, str3);
        }

        private r2(f.s sVar) {
            this(E(sVar), Uri.decode(sVar.a()), sVar.a(), sVar.a());
        }

        /* synthetic */ r2(f.s sVar, a aVar) {
            this(sVar);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Intent E(f.s sVar) {
            try {
                return Intent.parseUri(sVar.e(), 0);
            } catch (Throwable th) {
                f.v.d(th);
                return null;
            }
        }

        @Override // a.b.s2, a.b
        public void y(f.s sVar) {
            try {
                f.s sVarD = sVar.d(this.z);
                Intent intent = this.K;
                sVarD.b(intent != null ? intent.toUri(0) : null).c(Uri.encode(this.L)).c(this.M).c(this.N);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    public static final class s extends k3 {
        public s(int i) {
            super(20, R.string.action_auto_rotation, R.drawable.ic_auto_rotation, i);
        }
    }

    public static final class s0 extends b {
        public static final String L;
        public static final String M;
        public static final String N;
        public static final String O;
        public final int I;
        public final String J;
        public final int K;

        static {
            StringBuilder sb = new StringBuilder();
            String str = f.l.k;
            sb.append(str);
            sb.append("KEYCODE");
            L = sb.toString();
            M = str + "WIFI_LIST";
            N = str + "VOLUME_STREAM_ALIAS";
            O = str + "LOGS";
        }

        private s0(int i, String str, int i2) {
            super(65535, 0, 0);
            this.I = i;
            this.J = str;
            this.K = i2;
        }

        private s0(f.s sVar) {
            this(sVar.h(), sVar.a(), sVar.h());
        }

        /* synthetic */ s0(f.s sVar, a aVar) {
            this(sVar);
        }

        public static void A(Context context, String str) {
            E(context, 27, "[u" + Process.myUid() + "]" + str, 0);
        }

        public static void B(Context context, int i) {
            E(context, i, null, 0);
        }

        public static void C(Context context, int i, int i2) {
            E(context, i, null, i2);
        }

        public static void D(Context context, int i, String str) {
            E(context, i, str, 0);
        }

        public static void E(Context context, int i, String str, int i2) {
            b.t(context, new s0(i, str, i2));
        }

        public static void F(Context context, int i, boolean z) {
            E(context, i, null, z ? 1 : 0);
        }

        public static void G(Context context, int i, String str) {
            b.u(context, new s0(i, str, 0), f.o0.m());
        }

        @Override // a.b
        public CharSequence n(Context context) {
            return "";
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I).c(this.J).d(this.K);
        }

        public boolean z() {
            return this.K != 0;
        }
    }

    public static final class s1 extends k3 {
        public s1(int i) {
            super(25, R.string.action_nfc, R.drawable.ic_nfc, i);
        }
    }

    public static class s2 extends x {
        protected static final String O = v0.f5a;
        public final Intent K;
        protected final String L;
        protected String M;
        protected String N;

        protected s2(int i, Intent intent, String str, String str2, String str3) {
            super(i, R.string.action_shortcut, R.drawable.ic_shortcut);
            this.K = intent;
            this.L = str;
            this.M = str2;
            this.N = str3;
            if (str3 != null || str2 == null) {
                return;
            }
            v0.b(str2);
        }

        private s2(Intent intent, String str, String str2, String str3) {
            this(49, intent, str, str2, str3);
        }

        private s2(f.s sVar) {
            this(B(sVar), Uri.decode(sVar.a()), sVar.a(), sVar.a());
        }

        /* synthetic */ s2(f.s sVar, a aVar) {
            this(sVar);
        }

        protected static Intent B(f.s sVar) {
            try {
                return Intent.parseUri(sVar.a(), 0);
            } catch (Throwable th) {
                f.v.d(th);
                return null;
            }
        }

        @Override // a.b.x
        protected CharSequence A(Context context) {
            ComponentName component;
            Intent intent = this.K;
            if (intent != null && (component = intent.getComponent()) != null) {
                try {
                    return f.z.K(context).u(component);
                } catch (Throwable unused) {
                    return component.flattenToShortString();
                }
            }
            String str = this.N;
            if (str == null) {
                Intent intent2 = this.K;
                str = intent2 != null ? intent2.getPackage() : null;
            }
            if (str != null) {
                try {
                    return f.z.K(context).G(str);
                } catch (Throwable unused2) {
                }
            }
            return str;
        }

        public r2 C() {
            return new r2(this.K, this.L, this.M, this.N, null);
        }

        @Override // a.b
        protected StringBuilder l(StringBuilder sb, Context context, String str) {
            if (this.K == null) {
                return super.l(sb, context, str);
            }
            sb.append(str);
            sb.append(n(context));
            sb.append('\n');
            sb.append(str);
            sb.append("    ");
            sb.append(this.K);
            return sb;
        }

        @Override // a.b.x, a.b
        public CharSequence n(Context context) {
            String str = this.L;
            return str != null ? str : super.n(context);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            try {
                sVar.c(this.K.toUri(0)).c(Uri.encode(this.L)).c(this.M).c(this.N);
            } catch (Throwable th) {
                Intent intent = this.K;
                if (intent != null) {
                    f.v.e(intent);
                }
                f.v.d(th);
            }
        }

        @Override // a.b.x
        protected Drawable z(Context context) {
            Drawable drawable;
            if (this.N != null) {
                if (this.M != null) {
                    try {
                        Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(this.N);
                        int identifier = resourcesForApplication.getIdentifier(this.M, "drawable", null);
                        if (identifier != 0 && (drawable = resourcesForApplication.getDrawable(identifier)) != null) {
                            return new g.g(drawable);
                        }
                    } catch (Throwable unused) {
                    }
                }
                return f.z.K(context).C(this.N);
            }
            if (this.M != null) {
                Drawable drawableH = f.q.h(O + this.M);
                if (drawableH != null) {
                    return drawableH;
                }
            }
            if (this.K != null) {
                return f.z.K(context).q(this.K);
            }
            return null;
        }
    }

    public static final class t extends b {
        private t() {
            super(2, R.string.action_back, R.drawable.ic_back);
        }

        /* synthetic */ t(a aVar) {
            this();
        }
    }

    public static final class t0 extends b {
        private t0() {
            super(3, R.string.action_home, R.drawable.ic_home);
        }

        /* synthetic */ t0(a aVar) {
            this();
        }
    }

    public static final class t1 extends b {
        public t1() {
            super(1, R.string.action_none, R.drawable.ic_none);
        }
    }

    public static final class t2 extends b {
        private t2() {
            super(40, R.string.action_shortcuts_panel, R.drawable.ic_shortcuts_panel);
        }

        /* synthetic */ t2(a aVar) {
            this();
        }
    }

    public static final class u extends k3 {
        public u(int i) {
            super(24, R.string.action_bluetooth, R.drawable.ic_bluetooth, i);
        }
    }

    public interface u0 {
        CharSequence a(Context context, int i);

        int b();

        b c(int i);
    }

    public static final class u1 extends b {
        public final int I;
        public final String J;

        private u1() {
            this(0, (String) null);
        }

        public u1(int i, String str) {
            super(109, R.string.action_notify, R.drawable.ic_notify);
            this.I = i;
            this.J = str;
        }

        /* synthetic */ u1(a aVar) {
            this();
        }

        private u1(f.s sVar) {
            this(sVar.h(), sVar.e());
        }

        /* synthetic */ u1(f.s sVar, a aVar) {
            this(sVar);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.J == null) {
                return super.n(context);
            }
            return ((Object) super.n(context)) + ": " + this.J;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I).b(this.J);
        }
    }

    public static final class u2 extends b {
        public final String I;

        private u2() {
            this((String) null);
        }

        /* synthetic */ u2(a aVar) {
            this();
        }

        private u2(f.s sVar) {
            this(sVar.e());
        }

        /* synthetic */ u2(f.s sVar, a aVar) {
            this(sVar);
        }

        public u2(String str) {
            super(111, R.string.action_toast, R.drawable.ic_toast);
            this.I = str;
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I == null) {
                return super.n(context);
            }
            return ((Object) super.n(context)) + ": " + this.I;
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.b(this.I);
        }
    }

    public static final class v extends b {
        private v() {
            super(14, R.string.action_brightness_down, R.drawable.ic_brightness_down);
        }

        /* synthetic */ v(a aVar) {
            this();
        }
    }

    public static class v0 {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        static final String f5a = f.l.l + "icons/";

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private static String[] f6b = null;

        public static void a() {
            String[] list = new File(f5a).list();
            if (list == null || list.length <= 0) {
                return;
            }
            f6b = list;
        }

        static void b(String str) {
            String[] strArr;
            if (str == null || (strArr = f6b) == null) {
                return;
            }
            for (int i = 0; i < strArr.length; i++) {
                String str2 = strArr[i];
                if (str2 != null && str2.equals(str)) {
                    strArr[i] = null;
                    return;
                }
            }
        }

        private static String c() {
            String str;
            do {
                str = Integer.toHexString(f.p0.r()) + ".png";
            } while (new File(f5a + str).exists());
            return str;
        }

        public static void d() {
            String[] strArr = f6b;
            if (strArr != null) {
                f6b = null;
                for (String str : strArr) {
                    if (str != null) {
                        new File(f5a + str).delete();
                    }
                }
            }
        }

        static String e(Bitmap bitmap) {
            String str = f5a;
            File file = new File(str);
            if (!file.exists() && (!file.mkdir() || !file.setExecutable(true, false))) {
                return null;
            }
            String strC = c();
            File file2 = new File(str + strC);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.close();
                    file2.setReadable(true, false);
                    return strC;
                } finally {
                }
            } catch (Throwable th) {
                f.v.d(th);
                return null;
            }
        }

        public static void f() {
            f6b = null;
        }
    }

    private static final class v1 extends b {
        public final int I;
        public final String J;

        private v1(int i, String str) {
            super(57, R.string.action_notify, R.drawable.ic_notify);
            this.I = i;
            this.J = str;
        }

        private v1(f.s sVar) {
            this(sVar.h(), A(sVar));
        }

        /* synthetic */ v1(f.s sVar, a aVar) {
            this(sVar);
        }

        private static String A(f.s sVar) {
            String strA = sVar.a();
            return strA != null ? Uri.decode(strA) : "";
        }

        /* JADX INFO: Access modifiers changed from: private */
        public u1 B() {
            return new u1(this.I, this.J);
        }
    }

    private static final class v2 extends b {
        public final String I;

        private v2(f.s sVar) {
            this(A(sVar));
        }

        /* synthetic */ v2(f.s sVar, a aVar) {
            this(sVar);
        }

        private v2(String str) {
            super(91, R.string.action_toast, R.drawable.ic_toast);
            this.I = str;
        }

        private static String A(f.s sVar) {
            String strA = sVar.a();
            return strA != null ? Uri.decode(strA) : "";
        }

        /* JADX INFO: Access modifiers changed from: private */
        public u2 B() {
            return new u2(this.I);
        }
    }

    public static final class w extends b {
        private w() {
            super(13, R.string.action_brightness_up, R.drawable.ic_brightness_up);
        }

        /* synthetic */ w(a aVar) {
            this();
        }
    }

    public static final class w0 extends k3 {
        public w0(int i) {
            super(29, R.string.action_immersive_nav_bar, R.drawable.ic_immersive_nav_bar, i);
        }
    }

    public static final class w1 extends b {
        public w1() {
            super(39, R.string.action_pen, R.drawable.ic_pen);
        }
    }

    public static final class w2 extends b {
        private w2() {
            super(46, R.string.action_shutdown, R.drawable.ic_shut_down);
        }

        /* synthetic */ w2(a aVar) {
            this();
        }
    }

    public static abstract class x extends b {
        private CharSequence I;
        private Locale J;

        x(int i, int i2, int i3) {
            super(i, i2, i3);
        }

        protected abstract CharSequence A(Context context);

        @Override // a.b
        public Drawable m(Context context) {
            try {
                Drawable drawableZ = z(context);
                if (drawableZ != null) {
                    return drawableZ;
                }
            } catch (Throwable unused) {
            }
            return super.m(context);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            CharSequence charSequenceA = this.I;
            Locale locale = this.J;
            Locale locale2 = Locale.getDefault();
            if (charSequenceA != null && locale2.equals(locale)) {
                return charSequenceA;
            }
            try {
                charSequenceA = A(context);
                this.I = charSequenceA;
                this.J = locale2;
            } catch (Throwable unused) {
            }
            return charSequenceA != null ? charSequenceA : super.n(context);
        }

        protected abstract Drawable z(Context context);
    }

    public static final class x0 extends k3 {
        public x0(int i) {
            super(73, R.string.action_immersive_status_bar, R.drawable.ic_immersive_status_bar, i);
        }
    }

    public static final class x1 extends b {
        private x1() {
            super(51, R.string.action_pie, R.drawable.ic_pie);
        }

        /* synthetic */ x1(a aVar) {
            this();
        }
    }

    public static final class x2 extends b implements u0 {
        public final int I;

        private x2(int i) {
            super(65, R.string.action_side_bar, R.drawable.ic_left_side_bar);
            this.I = i;
        }

        /* synthetic */ x2(int i, a aVar) {
            this(i);
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            return context.getText(i == 1 ? R.string.side_right : R.string.side_left);
        }

        @Override // a.b.u0
        public int b() {
            return 2;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new x2(i);
        }

        @Override // a.b
        public Drawable m(Context context) {
            Resources resources;
            int i;
            int i2 = this.I;
            if (i2 == 0) {
                resources = context.getResources();
                i = R.drawable.ic_left_side_bar;
            } else {
                if (i2 != 1) {
                    return super.m(context);
                }
                resources = context.getResources();
                i = R.drawable.ic_right_side_bar;
            }
            return resources.getDrawable(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            int i;
            int i2 = this.I;
            if (i2 == 0) {
                i = R.string.left_side_bar;
            } else {
                if (i2 != 1) {
                    return super.n(context);
                }
                i = R.string.right_side_bar;
            }
            return context.getText(i);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class y extends b {
        private y() {
            super(38, R.string.action_clear_background_apps, R.drawable.ic_clear_background_apps);
        }

        /* synthetic */ y(a aVar) {
            this();
        }
    }

    public static final class y0 extends b {
        public static final String K = f.l.j + ".GESTURE_RECORDS_CHANGES";
        public static final String L = f.l.n;
        private static f.n M = null;
        private static boolean N = false;
        private static List<Pair<List<g.q>, Boolean>> O;
        private final String I;
        private final boolean J;

        private y0() {
            this((String) null, false);
        }

        /* synthetic */ y0(a aVar) {
            this();
        }

        public y0(Context context, List<g.q> list, boolean z) {
            this(N(context, list, z), z);
        }

        private y0(f.s sVar) {
            this(sVar.a(), (sVar.h() & 1) != 0);
        }

        /* synthetic */ y0(f.s sVar, a aVar) {
            this(sVar);
        }

        private y0(String str, boolean z) {
            super(53, R.string.action_inject_gesture, R.drawable.ic_gesture);
            this.J = z;
            this.I = str;
        }

        private static f.n B() {
            f.n nVar;
            synchronized (y0.class) {
                if (M == null) {
                    M = new f.n(L);
                }
                nVar = M;
            }
            return nVar;
        }

        public static List<y0> E() {
            f.n nVarB = B();
            Set<String> setC = nVarB.c();
            ArrayList arrayList = new ArrayList(setC.size());
            for (String str : setC) {
                boolean z = false;
                if (nVarB.b(str).charAt(0) == 'l') {
                    z = true;
                }
                arrayList.add(new y0(str, z));
            }
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void G(Context context) {
            List<Pair<List<g.q>, Boolean>> list;
            if (N || (list = O) == null) {
                return;
            }
            int size = list.size();
            for (int i = 0; i < size; i++) {
                try {
                    Pair<List<g.q>, Boolean> pair = O.get(i);
                    N(context, (List) pair.first, ((Boolean) pair.second).booleanValue());
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            O = null;
        }

        public static void H() {
            N = true;
        }

        public static void I(y0 y0Var) {
            if (y0Var == null || y0Var.I == null) {
                return;
            }
            B().d(y0Var.I);
        }

        public static void J(y0 y0Var) {
            if (y0Var == null || y0Var.I == null) {
                return;
            }
            B().e(y0Var.I);
        }

        public static void K() {
            synchronized (y0.class) {
                M = null;
            }
        }

        public static void L() {
            B().f();
        }

        public static void M(y0 y0Var) {
            if (y0Var == null || y0Var.I == null) {
                return;
            }
            B().g(y0Var.I);
        }

        private static String N(Context context, List<g.q> list, boolean z) {
            StringBuilder sb = new StringBuilder();
            sb.append(z ? 'l' : 'p');
            sb.append(f.p0.f());
            String strA = B().a(sb.toString());
            String str = L + strA;
            f.p pVar = new f.p(str);
            pVar.j();
            Iterator<g.q> it = list.iterator();
            while (it.hasNext()) {
                it.next().b(pVar);
                pVar.C();
            }
            pVar.n();
            a.j.k(context, str);
            return strA;
        }

        public static void O(Context context, List<g.q> list, boolean z) {
            if (!N) {
                N(context, list, z);
                return;
            }
            if (O == null) {
                O = new ArrayList();
            }
            O.add(new Pair<>(list, Boolean.valueOf(z)));
        }

        public static void Q(final Context context) {
            N = false;
            if (O == null) {
                return;
            }
            f.h0.a().post(new Runnable() { // from class: a.c
                @Override // java.lang.Runnable
                public final void run() {
                    b.y0.G(context);
                }
            });
        }

        public String A() {
            String strB = B().b(this.I);
            return strB != null ? strB.substring(1) : "-";
        }

        public int C() {
            List<g.q> listD = D();
            if (listD == null) {
                return 0;
            }
            return listD.get(listD.size() - 1).z;
        }

        public List<g.q> D() {
            if (this.I == null) {
                f.v.c("File of gesture: null");
                return null;
            }
            return f.q.g(L + this.I);
        }

        public boolean F() {
            return this.J;
        }

        public void P(String str) {
            f.n nVarB = B();
            String str2 = this.I;
            StringBuilder sb = new StringBuilder();
            sb.append(this.J ? 'l' : 'p');
            sb.append(str);
            nVarB.i(str2, sb.toString());
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I == null) {
                return super.n(context);
            }
            return ((Object) super.n(context)) + ": " + A();
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.c(this.I).d(this.J ? 1 : 0);
        }
    }

    public static final class y1 extends b {
        public final String I;
        public final int J;
        public final boolean K;
        private CharSequence L;

        private y1() {
            this(null, 0, false);
        }

        /* synthetic */ y1(a aVar) {
            this();
        }

        private y1(f.s sVar) {
            this(sVar.e(), sVar.h(), sVar.h() == 1);
        }

        /* synthetic */ y1(f.s sVar, a aVar) {
            this(sVar);
        }

        public y1(String str, int i, boolean z) {
            super(93, R.string.action_play_sound, R.drawable.ic_play_sound);
            this.L = null;
            this.I = str;
            this.J = i;
            this.K = z;
        }

        @Override // a.b
        public Drawable m(Context context) {
            Resources resources;
            int i;
            String str = this.I;
            if (str != null && str.length() == 0) {
                resources = context.getResources();
                i = R.drawable.ic_stop_play_sound;
            } else {
                if (!this.K) {
                    return super.m(context);
                }
                resources = context.getResources();
                i = R.drawable.ic_play_sound_repeat;
            }
            return resources.getDrawable(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            String str = this.I;
            if (str == null) {
                return super.n(context);
            }
            if (str.length() == 0) {
                return context.getText(R.string.stop_play_sound);
            }
            if (this.L == null) {
                try {
                    this.L = RingtoneManager.getRingtone(context, Uri.parse(this.I)).getTitle(context);
                } catch (Throwable th) {
                    f.v.d(th);
                    this.L = "-";
                }
            }
            return ((Object) super.n(context)) + ": " + ((Object) this.L);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.b(this.I).d(this.J).d(this.K ? 1 : 0);
        }
    }

    public static final class y2 extends b implements u0 {
        public final int I;

        private y2() {
            this(0);
        }

        public y2(int i) {
            super(6, R.string.action_sleep_wake, R.drawable.ic_sleep_wake);
            this.I = i;
        }

        /* synthetic */ y2(a aVar) {
            this();
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            return context.getText(i == 1 ? R.string.sub_action_sleep : i == 2 ? R.string.sub_action_wake_up : R.string.sub_action_switch);
        }

        @Override // a.b.u0
        public int b() {
            return 3;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new y2(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            int i = this.I;
            return context.getText(i == 1 ? R.string.sub_action_sleep : i == 2 ? R.string.sub_action_wake_up : R.string.action_sleep_wake);
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    public static final class z extends b {
        private z() {
            super(90, R.string.action_clipboard, R.drawable.ic_clipboard);
        }

        /* synthetic */ z(a aVar) {
            this();
        }
    }

    public static final class z0 extends b {
        public final int I;
        public final int J;
        public final int K;

        private z0() {
            this(0, 0, 0);
        }

        public z0(int i, int i2, int i3) {
            super(105, R.string.action_inject_key_event, R.drawable.ic_inject_key_event);
            this.I = i;
            this.J = i2;
            this.K = i3;
        }

        /* synthetic */ z0(a aVar) {
            this();
        }

        private z0(f.s sVar) {
            this(sVar.h(), sVar.h(), sVar.h());
        }

        /* synthetic */ z0(f.s sVar, a aVar) {
            this(sVar);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            String string;
            int i = this.I;
            if (i == 0) {
                return super.n(context);
            }
            String strSubstring = KeyEvent.keyCodeToString(i).substring(8);
            int i2 = this.J;
            if (i2 == 0) {
                string = context.getString(R.string.key_click_f, strSubstring);
            } else if (i2 == 1) {
                string = context.getString(R.string.key_double_click_f, strSubstring);
            } else {
                if (i2 != 2) {
                    return super.n(context);
                }
                string = context.getString(R.string.key_long_press_f, strSubstring);
            }
            if (this.K == 0) {
                return string;
            }
            StringBuilder sb = new StringBuilder((CharSequence) string);
            if ((this.K & 1) != 0) {
                sb.append(" + Shift");
            }
            if ((this.K & 2) != 0) {
                sb.append(" + Alt");
            }
            if ((this.K & 4096) != 0) {
                sb.append(" + Ctrl");
            }
            if ((this.K & 65536) != 0) {
                sb.append(" + Meta");
            }
            if ((this.K & 8) != 0) {
                sb.append(" + Fn");
            }
            if ((this.K & 4) != 0) {
                sb.append(" + Sym");
            }
            return sb.toString();
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I).d(this.J).d(this.K);
        }
    }

    public static final class z1 extends b {
        private z1() {
            super(43, R.string.action_power_menu, R.drawable.ic_power_menu);
        }

        /* synthetic */ z1(a aVar) {
            this();
        }
    }

    public static final class z2 extends b implements u0 {
        public final int I;

        private z2(int i) {
            super(68, R.string.action_slide_adjust, R.drawable.ic_swipe_adjust);
            this.I = i;
        }

        /* synthetic */ z2(int i, a aVar) {
            this(i);
        }

        @Override // a.b.u0
        public CharSequence a(Context context, int i) {
            return i != 0 ? i != 1 ? i != 13 ? f.q0.d(context, i - 3) : context.getText(R.string.adjust_caret) : context.getText(R.string.adjust_brightness) : context.getText(R.string.adjust_screen_filter_alpha);
        }

        @Override // a.b.u0
        public int b() {
            return 14;
        }

        @Override // a.b.u0
        public b c(int i) {
            return new z2(i);
        }

        @Override // a.b
        public Drawable m(Context context) {
            int i;
            switch (this.I) {
                case 0:
                    i = R.drawable.ic_swipe_adjust_screen_filter;
                    break;
                case 1:
                    i = R.drawable.ic_swipe_adjust_brightness;
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                    i = R.drawable.ic_swipe_adjust_volume;
                    break;
                case 13:
                    i = R.drawable.ic_caret;
                    break;
                default:
                    return super.m(context);
            }
            return context.getResources().getDrawable(i);
        }

        @Override // a.b
        public CharSequence n(Context context) {
            if (this.I == -1) {
                return super.n(context);
            }
            return ((Object) super.n(context)) + " " + ((Object) a(context, this.I));
        }

        @Override // a.b
        public void y(f.s sVar) {
            super.y(sVar);
            sVar.d(this.I);
        }
    }

    static {
        StringBuilder sb = new StringBuilder();
        String str = f.l.k;
        sb.append(str);
        sb.append("PERFORM_ACTION");
        C = sb.toString();
        D = str + "PERFORM";
        G = new b[0];
        H = null;
        CREATOR = new a();
    }

    protected b(int i4, int i5, int i6) {
        this.z = i4;
        this.A = i5;
        this.B = i6;
    }

    public static q e() {
        if (E == null) {
            E = new q();
        }
        return E;
    }

    public static b f(String str) {
        if (str == null) {
            return e();
        }
        byte[] bArrG = f.p0.g(str);
        f.h hVar = new f.h();
        hVar.i(bArrG);
        b bVarG = hVar.g();
        hVar.m();
        return bVarG;
    }

    public static String g(b bVar) {
        return "am broadcast -a " + D + " -e data " + h(bVar);
    }

    public static String h(b bVar) {
        return f.p0.i(new f.h().j().f(bVar).o());
    }

    public static b[] i(int i4) {
        switch (i4) {
            case 0:
            case 5:
                return w(null);
            case 1:
                return w(new int[]{51, 68, 106, 107, 54, 102});
            case 2:
                return w(new int[]{54, 102});
            case 3:
                return w(new int[]{0, 1, 51, 68, 106, 107, 86, 78, 54, 102});
            case 4:
                return w(new int[]{0, 1, 51, 68, 106, 107, 86});
            case 6:
                return w(new int[]{0, 51, 68, 106, 107, 54, 102});
            case 7:
                return w(new int[]{0, 1, 79});
            default:
                f.v.c("Unknown flag!");
                return G;
        }
    }

    private static b[] j() {
        if (H == null) {
            b[] bVarArr = new b[89];
            int i4 = 0;
            bVarArr[0] = e();
            bVarArr[1] = r();
            bVarArr[2] = new h3();
            a aVar = null;
            bVarArr[3] = new t(aVar);
            bVarArr[4] = new t0(aVar);
            bVarArr[5] = new d2(aVar);
            bVarArr[6] = new o1(aVar);
            bVarArr[7] = new y2(aVar);
            bVarArr[8] = new h0();
            int i5 = -1;
            bVarArr[9] = new j1(i5, aVar);
            bVarArr[10] = new i0(aVar);
            bVarArr[11] = new j0(aVar);
            int i6 = Build.VERSION.SDK_INT;
            bVarArr[12] = i6 >= 24 ? new e3() : null;
            bVarArr[13] = i6 >= 24 ? new n2(0) : new o2();
            bVarArr[14] = new k0(aVar);
            bVarArr[15] = new h2(aVar);
            bVarArr[16] = new a3(0);
            bVarArr[17] = new e0(i5, aVar);
            bVarArr[18] = new z(aVar);
            bVarArr[19] = new m3();
            bVarArr[20] = new g2(aVar);
            bVarArr[21] = new r(0);
            bVarArr[22] = new s(0);
            bVarArr[23] = new o3(0);
            bVarArr[24] = new p1(0);
            bVarArr[25] = new i1(0);
            bVarArr[26] = new u(0);
            bVarArr[27] = new s1(0);
            bVarArr[28] = new j3(0);
            bVarArr[29] = new p3(0);
            bVarArr[30] = new k(0);
            bVarArr[31] = new x0(0);
            bVarArr[32] = new w0(0);
            bVarArr[33] = new d1(0);
            bVarArr[34] = new l3(0);
            bVarArr[35] = new m2(0);
            bVarArr[36] = new r0(0);
            bVarArr[37] = new p0(0);
            bVarArr[38] = new e1(0);
            bVarArr[39] = new o0(0);
            bVarArr[40] = new h1(aVar);
            bVarArr[41] = new a2(aVar);
            bVarArr[42] = new r1(aVar);
            bVarArr[43] = new l0(aVar);
            bVarArr[44] = new g1(aVar);
            bVarArr[45] = new f1(null);
            bVarArr[46] = new n(aVar);
            bVarArr[47] = new y(aVar);
            bVarArr[48] = new e2(aVar);
            bVarArr[49] = new z1(aVar);
            bVarArr[50] = new c2(aVar);
            bVarArr[51] = new b3(aVar);
            bVarArr[52] = new w2(aVar);
            bVarArr[53] = new t2(aVar);
            bVarArr[54] = new f0(aVar);
            bVarArr[55] = new o(aVar);
            bVarArr[56] = new n0(aVar);
            bVarArr[57] = new i2(aVar);
            bVarArr[58] = new x2(i5, aVar);
            bVarArr[59] = new m0();
            bVarArr[60] = new z0(aVar);
            bVarArr[61] = new y0(aVar);
            bVarArr[62] = new b1(aVar);
            bVarArr[63] = new n3(aVar);
            bVarArr[64] = new y1(aVar);
            bVarArr[65] = new u1(aVar);
            bVarArr[66] = new u2(aVar);
            bVarArr[67] = new c3(aVar);
            bVarArr[68] = new b2();
            bVarArr[69] = new w1();
            bVarArr[70] = new x1(aVar);
            bVarArr[71] = new z2(i5, aVar);
            bVarArr[72] = new q0(i4, aVar);
            bVarArr[73] = new i3(aVar);
            bVarArr[74] = new i(aVar);
            bVarArr[75] = new l(aVar);
            bVarArr[76] = new e(aVar);
            bVarArr[77] = new r2(aVar);
            bVarArr[78] = new g0(aVar);
            bVarArr[79] = new a0(aVar);
            bVarArr[80] = new p2(aVar);
            bVarArr[81] = new C0000b(aVar);
            bVarArr[82] = new g3(aVar);
            bVarArr[83] = new f2(aVar);
            bVarArr[84] = new d0(aVar);
            bVarArr[85] = new q1(aVar);
            bVarArr[86] = new k2(aVar);
            bVarArr[87] = new c(aVar);
            bVarArr[88] = new d(aVar);
            H = bVarArr;
        }
        return H;
    }

    public static t1 r() {
        if (F == null) {
            F = new t1();
        }
        return F;
    }

    public static b s(f.s sVar) {
        int iH = sVar.h();
        a aVar = null;
        if (iH == 65535) {
            return new s0(sVar, aVar);
        }
        switch (iH) {
            case 0:
                return e();
            case 1:
                return r();
            case 2:
                return new t(aVar);
            case 3:
                return new t0(aVar);
            case 4:
                return new d2(aVar);
            case 5:
                return new o1(aVar);
            case 6:
                return new y2(sVar.h());
            case 7:
                return new j(-1, -1);
            case 8:
                return new j(-1, -2);
            case 9:
                return new l1(aVar);
            case 10:
                return new n1(aVar);
            case 11:
                return new m1(aVar);
            case 12:
                return new k1(aVar);
            case 13:
                return new w(aVar);
            case 14:
                return new v(aVar);
            case 15:
                return new i0(aVar);
            case 16:
                return new j0(aVar);
            case 17:
                return new o2();
            case 18:
                return new g2(aVar);
            case 19:
                return new r(sVar.h());
            case 20:
                return new s(sVar.h());
            case 21:
                return new o3(sVar.h());
            case 22:
                return new p1(sVar.h());
            case 23:
                return new i1(sVar.h());
            case 24:
                return new u(sVar.h());
            case 25:
                return new s1(sVar.h());
            case 26:
                return new j3(sVar.h());
            case 27:
                return new p3(sVar.h());
            case 28:
                return new k(sVar.h());
            case 29:
                return new w0(sVar.h());
            case 30:
                return new d1(sVar.h());
            case 31:
                return new r0(sVar.h());
            case 32:
                return new l3(sVar.h());
            case 33:
                return new m2(sVar.h());
            case 34:
                return new p0(sVar.h());
            case 35:
                return new e1(sVar.h());
            case 36:
                return new h1(aVar);
            case 37:
                return new g1(aVar);
            case 38:
                return new y(aVar);
            case 39:
                return new w1();
            case 40:
                return new t2(aVar);
            case 41:
                return new p(aVar).z();
            case 42:
                return new n0(aVar);
            case 43:
                return new z1(aVar);
            case 44:
                return new c2(aVar);
            case 45:
                return new b3(aVar);
            case 46:
                return new w2(aVar);
            case 47:
                return new m(sVar, aVar);
            case 48:
                return new e(sVar, aVar);
            case 49:
                return new s2(sVar, aVar).C();
            case 50:
                return new l2(sVar.a(), aVar).z();
            case 51:
                return new x1(aVar);
            case 52:
                return new a1(sVar, aVar).z();
            case 53:
                return new y0(sVar, aVar);
            case 54:
                return new g0(sVar, aVar);
            case 55:
                return new i2(aVar);
            case 56:
                return new c1(sVar, aVar).A();
            case 57:
                return new v1(sVar, aVar).B();
            case 58:
                return new d3(sVar, aVar).B();
            case 59:
                return new b2();
            case 60:
                return new j(sVar, aVar).A();
            case 61:
                return new b0(sVar, aVar);
            case 62:
                return new d0(sVar, aVar);
            case 63:
                return new q1(sVar, aVar);
            case 64:
                return new k0(sVar.h());
            case 65:
                return new x2(sVar.h(), aVar);
            case 66:
                return new a2(aVar);
            case 67:
                return new r1(aVar);
            case 68:
                return new z2(sVar.h(), aVar);
            case 69:
                return new q2(sVar, aVar).C();
            case 70:
                return new g2(sVar.h(), aVar);
            case 71:
                return new n(aVar);
            case 72:
                return new d(sVar, aVar);
            case 73:
                return new x0(sVar.h());
            case 74:
                return new h2(sVar.h(), aVar);
            case 75:
                return new h0();
            case 76:
                return new g(sVar, aVar);
            case 77:
                return new f1(sVar.a());
            case 78:
                return new r2(sVar, aVar);
            default:
                byte b4 = 0;
                switch (iH) {
                    case 80:
                        return new q0(b4 == true ? 1 : 0, aVar);
                    case 81:
                        return new e2(aVar);
                    case 82:
                        return new o(sVar.h(), aVar);
                    case 83:
                        return new f0(sVar.h());
                    case 84:
                        return new k2(sVar.h());
                    case 85:
                        return new a3(sVar.h());
                    case 86:
                        return new i3(sVar.h(), sVar.h() != 0);
                    case 87:
                        return new e0(sVar.h(), aVar);
                    case 88:
                        return new p2(sVar, aVar);
                    case 89:
                        return new i(sVar.h(), sVar.h());
                    case 90:
                        return new z(aVar);
                    case 91:
                        return new v2(sVar, aVar).B();
                    case 92:
                        return new n3(sVar.h());
                    case 93:
                        return new y1(sVar, aVar);
                    case 94:
                        return new m0(sVar.h());
                    case 95:
                        return new n2(sVar.h());
                    case 96:
                        return new e3();
                    case 97:
                        return new o0(sVar.h());
                    case 98:
                        return new m3();
                    case 99:
                        return new l0(aVar);
                    case 100:
                        return new a0(sVar, aVar);
                    case 101:
                        return new f2(sVar, aVar);
                    case 102:
                        return new C0000b(aVar);
                    default:
                        switch (iH) {
                            case 104:
                                return new j1(sVar.h(), aVar);
                            case 105:
                                return new z0(sVar, aVar);
                            case 106:
                                return new q0(sVar.h(), aVar);
                            case 107:
                                return new h3(sVar, aVar);
                            case 108:
                                return new b1(sVar, aVar);
                            case 109:
                                return new u1(sVar, aVar);
                            case 110:
                                return new c3(sVar, aVar);
                            case 111:
                                return new u2(sVar, aVar);
                            case 112:
                                return new g3(aVar);
                            case 113:
                                return new f(sVar, aVar);
                            case 114:
                                return new h(sVar, aVar);
                            case 115:
                                return new l(sVar, aVar);
                            default:
                                f.v.c("Unknown action id: " + iH);
                                return e();
                        }
                }
        }
    }

    public static void t(Context context, b bVar) {
        u(context, bVar, 0);
    }

    static void u(Context context, b bVar, int i4) {
        int i5 = bVar.z;
        if (i5 == 0 || i5 == 1 || i5 == 51 || i5 == 54 || i5 == 68 || i5 == 106 || i5 == 107) {
            return;
        }
        Intent intent = new Intent(C).setPackage("android");
        new f.m(intent).j().f(bVar).l();
        if (i4 != 0) {
            try {
                context.sendBroadcastAsUser(intent, f.o0.r());
                return;
            } catch (Throwable th) {
                f.v.c(th.toString());
            }
        }
        context.sendBroadcast(intent);
    }

    public static void v(final Context context, final b bVar, long j4) {
        f.h0.a().postDelayed(new Runnable() { // from class: a.a
            @Override // java.lang.Runnable
            public final void run() {
                b.t(context, bVar);
            }
        }, j4);
    }

    private static b[] w(int[] iArr) {
        b[] bVarArrJ = j();
        ArrayList arrayList = new ArrayList(bVarArrJ.length);
        int i4 = 0;
        if (iArr == null || iArr.length == 0) {
            int length = bVarArrJ.length;
            while (i4 < length) {
                b bVar = bVarArrJ[i4];
                if (bVar != null) {
                    arrayList.add(bVar);
                }
                i4++;
            }
        } else {
            int length2 = bVarArrJ.length;
            while (i4 < length2) {
                b bVar2 = bVarArrJ[i4];
                if (bVar2 != null && f.p0.a(iArr, bVar2.z) == -1) {
                    arrayList.add(bVar2);
                }
                i4++;
            }
        }
        return (b[]) arrayList.toArray(new b[arrayList.size()]);
    }

    public static b x(b bVar) {
        while (bVar.z == 72) {
            bVar = ((d) bVar).K;
        }
        return bVar;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public final StringBuilder k(Context context) {
        return l(new StringBuilder(), context, "");
    }

    protected StringBuilder l(StringBuilder sb, Context context, String str) {
        sb.append(str);
        sb.append(n(context));
        return sb;
    }

    public Drawable m(Context context) {
        return context.getResources().getDrawable(this.B);
    }

    public CharSequence n(Context context) {
        return context.getText(this.A);
    }

    public Drawable o(Context context) {
        return m(context);
    }

    public CharSequence p(Context context) {
        return context.getText(this.A);
    }

    public String toString() {
        return "Action<" + this.z + ">";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i4) {
        f.d0 d0VarJ = new f.d0(parcel).j();
        y(d0VarJ);
        d0VarJ.l();
    }

    public void y(f.s sVar) {
        sVar.d(this.z);
    }
}
