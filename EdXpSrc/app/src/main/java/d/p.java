package d;

import a.h;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;
import com.jozein.xedgepro.ApplicationMain;
import e.j;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class p extends e.j implements j.d {
    private static final a.h[] N = a.h.a();
    private a.y M = null;

    class a extends a.y {
        a(Context context) {
            super(context);
        }

        @Override // a.y
        protected void a(String[] strArr) {
            p.this.M.b();
            p.this.M = null;
            if (strArr == null || strArr.length == 0) {
                p.this.N(new e.y().J(p.this.u(R.string.wifi_ssid), null, null, 3, 32), 13);
            } else {
                p.this.h().putStringArray("ssids", strArr);
                p.this.N(new e.z().u(strArr), 12);
            }
        }
    }

    class b implements Runnable {
        final /* synthetic */ int z;

        b(int i) {
            this.z = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            p.this.P(new p(), this.z);
            p.this.e0(R.string.select_second_condition);
        }
    }

    private void I1(a.h hVar) {
        W("result", hVar);
    }

    private void J1(a.h hVar, int i) {
        h().putParcelable("first", hVar);
        View view = getView();
        if (view != null) {
            view.post(new b(i));
        }
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.select_condition);
    }

    @Override // e.j
    protected int B0() {
        return N.length;
    }

    @Override // e.j0.c
    protected void J(Bundle bundle, int i) {
        a.h lVar;
        int i2;
        if (bundle == null) {
            return;
        }
        if (i != 1) {
            switch (i) {
                case 7:
                    int i3 = bundle.getInt("hour", -1);
                    int i4 = bundle.getInt("minute", 0);
                    if (i3 != -1) {
                        Bundle bundleH = h();
                        bundleH.putInt("hour", i3);
                        bundleH.putInt("minute", i4);
                        int i5 = i3 + 12;
                        if (i5 >= 24) {
                            i5 -= 24;
                        }
                        N(new e.n0().w(u(R.string.time_to), i5, i4), 8);
                    }
                    break;
                case 8:
                    int i6 = bundle.getInt("hour", -1);
                    int i7 = bundle.getInt("minute", 0);
                    if (i6 != -1) {
                        Bundle bundleH2 = h();
                        int i8 = bundleH2.getInt("hour", 0);
                        int i9 = bundleH2.getInt("minute", 0);
                        if (i8 == i6 && i9 == i7) {
                            e0(R.string.condition_invalid);
                        } else {
                            I1(new h.n(i8, i9, i6, i7));
                        }
                    }
                    break;
                case 9:
                    lVar = new h.l(bundle.getInt("result", 0));
                    I1(lVar);
                    break;
                case 10:
                    lVar = new h.k(bundle.getInt("result", 0));
                    I1(lVar);
                    break;
                case 11:
                    lVar = new h.m(bundle.getInt("result", 0));
                    I1(lVar);
                    break;
                case 12:
                    String[] stringArray = h().getStringArray("ssids");
                    if (stringArray != null && (i2 = bundle.getInt("result", -1)) >= 0 && i2 < stringArray.length) {
                        I1(new h.l0(stringArray[i2]));
                    }
                    break;
                case 13:
                    CharSequence charSequence = bundle.getCharSequence("result");
                    if (charSequence != null) {
                        lVar = new h.l0(charSequence.toString());
                        I1(lVar);
                    }
                    break;
                case 14:
                    String string = bundle.getString("result");
                    if (string != null) {
                        lVar = new h.d(string);
                        I1(lVar);
                    }
                    break;
                case 15:
                    int i10 = bundle.getInt("result", -1);
                    if (i10 == 0 || i10 == 1) {
                        lVar = new h.a0(i10 == 1);
                        I1(lVar);
                    }
                    break;
                default:
                    a.h hVar = (a.h) bundle.getParcelable("result");
                    if (hVar != null) {
                        if (i == 2) {
                            J1(hVar, 3);
                        } else {
                            if (i == 3) {
                                lVar = new h.c((a.h) h().getParcelable("first"), hVar);
                            } else if (i == 4) {
                                J1(hVar, 5);
                                break;
                            } else if (i == 5) {
                                lVar = new h.z((a.h) h().getParcelable("first"), hVar);
                            } else if (i == 6) {
                                lVar = new h.x(hVar);
                            }
                            I1(lVar);
                        }
                    }
                    break;
            }
            return;
        }
        U(bundle);
        L();
    }

    @Override // e.j.d
    public String[] a() {
        Context contextM0 = M0();
        int length = N.length;
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = N[i].c(contextM0).toString();
        }
        return strArr;
    }

    @Override // e.j
    protected View h1(int i) {
        return new j.k(this, N[i].c(M0()));
    }

    @Override // e.j
    protected void k1(int i) {
        j0.c pVar;
        int i2;
        j0.b bVarU;
        int i3;
        j0.b dVar;
        a.h[] hVarArr = N;
        int i4 = hVarArr[i].z;
        if (i4 == 1) {
            P(new p(), 2);
        } else {
            if (i4 != 2) {
                if (i4 == 3) {
                    pVar = new p();
                    i2 = 6;
                } else {
                    if (i4 != 32) {
                        if (i4 != 33) {
                            if (i4 != 37) {
                                if (i4 == 38) {
                                    if (ApplicationMain.isModuleActivated()) {
                                        this.M = new a(f());
                                        return;
                                    } else {
                                        e0(R.string.module_not_activated);
                                        return;
                                    }
                                }
                                switch (i4) {
                                    case 26:
                                        dVar = new c.e();
                                        break;
                                    case 27:
                                        f.j jVar = new f.j();
                                        bVarU = new e.n0().w(u(R.string.time_from), jVar.g(), jVar.h());
                                        i3 = 7;
                                        break;
                                    case 28:
                                        bVarU = new e.b0().v(f.j.o(), 0);
                                        i3 = 9;
                                        break;
                                    case 29:
                                        bVarU = new e.b0().v(f.j.e(f()), 0);
                                        i3 = 10;
                                        break;
                                    case 30:
                                        bVarU = new e.b0().v(f.j.j(), 0);
                                        i3 = 11;
                                        break;
                                    default:
                                        I1(hVarArr[i]);
                                        L();
                                        break;
                                }
                            }
                            dVar = new c.d();
                            N(dVar, 1);
                            return;
                        }
                        bVarU = new e.z().u(new CharSequence[]{u(R.string.orientation_portrait), u(R.string.orientation_landscape)});
                        i3 = 15;
                        N(bVarU, i3);
                        return;
                    }
                    pVar = new u0().C1(u(R.string.select_focused_app));
                    i2 = 14;
                }
                P(pVar, i2);
                return;
            }
            P(new p(), 4);
        }
        e0(R.string.select_first_condition);
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        a.y yVar = this.M;
        if (yVar != null) {
            yVar.b();
            this.M = null;
        }
    }
}
