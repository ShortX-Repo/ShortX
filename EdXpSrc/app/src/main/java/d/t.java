package d;

import a.b;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import cc.xposed.R;
import e.j;
import e.j0;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class t extends c {
    private b.d M = null;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void J1(View view) {
        b.d dVar = this.M;
        b.d dVar2 = dVar;
        if (dVar == null) {
            dVar2 = (b.d) h().getParcelable("action");
        }
        if (dVar2 != null) {
            boolean zB = dVar2.B();
            Parcelable parcelable = dVar2;
            if (!zB) {
                boolean zC = dVar2.C();
                parcelable = dVar2;
                if (!zC) {
                    parcelable = dVar2.K;
                }
            }
            W("result", parcelable);
        }
        L();
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.action_editable_action);
        S(R.drawable.ic_ok, new View.OnClickListener() { // from class: d.s
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.z.J1(view);
            }
        });
    }

    @Override // e.j
    protected int B0() {
        if (this.M == null) {
            this.M = (b.d) h().getParcelable("action");
        }
        if (this.M != null) {
            return 3;
        }
        this.M = new b.d(a.b.e());
        return 3;
    }

    @Override // d.c
    protected void C1(Bundle bundle, int i) {
        CharSequence charSequenceN;
        j.k kVar;
        j.g gVar;
        Drawable drawableD0;
        String string;
        if (i == 0) {
            a.b bVar = (a.b) bundle.getParcelable("result");
            if (bVar == null) {
                return;
            }
            this.M = this.M.D(bVar);
            h().putParcelable("action", this.M);
            j.g gVar2 = (j.g) L0(0);
            gVar2.setImageDrawable(D0(bVar));
            charSequenceN = bVar.n(M0());
            kVar = gVar2;
        } else {
            if (i != 1) {
                if (i == 3) {
                    String string2 = bundle.getString("result");
                    if (string2 == null) {
                        return;
                    }
                    this.M = this.M.H(string2);
                    h().putParcelable("action", this.M);
                    gVar = (j.g) L0(2);
                    drawableD0 = D0(this.M);
                } else {
                    if (i == 5) {
                        String string3 = bundle.getString("result");
                        if (string3 != null) {
                            Context contextF = f();
                            try {
                                Drawable drawableC = f.z.K(contextF).C(string3);
                                drawableC.clearColorFilter();
                                this.M = this.M.F(g.a0.h(drawableC), contextF);
                                h().putParcelable("action", this.M);
                                ((j.g) L0(2)).setImageDrawable(D0(this.M));
                                ((j.k) K0(0)).c();
                                return;
                            } catch (Throwable th) {
                                g0(th);
                                return;
                            }
                        }
                        return;
                    }
                    if (i != 6 || (string = bundle.getString("result")) == null) {
                        return;
                    }
                    drawableD0 = b.j2.c(string);
                    this.M = this.M.G(string);
                    h().putParcelable("action", this.M);
                    gVar = (j.g) L0(2);
                }
                gVar.setImageDrawable(drawableD0);
                ((j.k) K0(0)).c();
                return;
            }
            CharSequence charSequence = bundle.getCharSequence("result");
            if (charSequence == null) {
                return;
            }
            j.k kVar2 = (j.k) L0(1);
            this.M = charSequence.length() == 0 ? this.M.I(null) : this.M.I(charSequence.toString());
            h().putParcelable("action", this.M);
            if (this.M.C()) {
                charSequenceN = this.M.n(M0());
                kVar = kVar2;
            } else {
                charSequenceN = "-";
                kVar = kVar2;
            }
        }
        kVar.setSubText(charSequenceN);
    }

    public t K1(b.d dVar, int i) {
        Bundle bundleH = h();
        bundleH.putParcelable("action", dVar);
        bundleH.putInt("flag", i);
        return this;
    }

    @Override // e.j
    protected boolean N0(int i) {
        return i == 2;
    }

    @Override // e.j
    protected View h1(int i) {
        if (i == 0) {
            a.b bVar = this.M.K;
            return new j.g(u(R.string.action), bVar.n(M0()), D0(bVar));
        }
        if (i == 1) {
            return new j.k(u(R.string.label), this.M.C() ? this.M.n(M0()) : "-");
        }
        if (i != 2) {
            return null;
        }
        return new j.g(u(R.string.icon), (CharSequence) null, this.M.B() ? D0(this.M) : null);
    }

    @Override // e.j
    protected View i1(int i) {
        if (i == 0) {
            j.k kVar = new j.k(u(R.string.delete), (CharSequence) null);
            if (!this.M.B()) {
                kVar.e();
            }
            return kVar;
        }
        if (i == 1) {
            return new j.k(u(R.string.built_in), (CharSequence) null);
        }
        if (i == 2) {
            return new j.k(u(R.string.gallery_apps), (CharSequence) null);
        }
        if (i == 3) {
            return new j.k(u(R.string.app_icons), (CharSequence) null);
        }
        if (i != 4) {
            return null;
        }
        return new j.k(u(R.string.saved_icons), (CharSequence) null);
    }

    @Override // e.j
    protected void k1(int i) {
        if (i == 0) {
            P(new d().H1(h().getInt("flag", 1), o()), 0);
            return;
        }
        if (i != 1) {
            if (i != 2) {
                return;
            }
            y1(2, 5);
        } else {
            e.y yVar = new e.y();
            yVar.K(u(R.string.enter_name), u(R.string.empty_as_default), this.M.C() ? this.M.n(M0()) : "");
            N(yVar, 1);
        }
    }

    @Override // e.j
    protected boolean l1(int i) {
        if (i != 0) {
            return super.l1(i);
        }
        F1(this.M.K, 0, 1);
        return true;
    }

    @Override // e.j
    protected void o1(int i) {
        j0.c hVar;
        int i2;
        if (i == 0) {
            this.M = this.M.E();
            h().putParcelable("action", this.M);
            ((j.g) L0(2)).setImageDrawable(null);
            ((j.k) K0(0)).e();
            return;
        }
        if (i == 1) {
            P(new n(), 3);
            return;
        }
        if (i == 2) {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            try {
                startActivityForResult(intent, 4);
                return;
            } catch (Throwable th) {
                g0(th);
                return;
            }
        }
        if (i == 3) {
            hVar = new h();
            i2 = 5;
        } else {
            if (i != 4) {
                return;
            }
            hVar = new q1();
            i2 = 6;
        }
        P(hVar, i2);
    }

    @Override // android.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        Uri data;
        if (i == 4 && i2 == -1 && (data = intent.getData()) != null) {
            try {
                InputStream inputStreamOpenInputStream = getActivity().getContentResolver().openInputStream(data);
                if (inputStreamOpenInputStream == null) {
                    throw new IOException("Failed to open input stream!");
                }
                Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpenInputStream);
                inputStreamOpenInputStream.close();
                if (bitmapDecodeStream != null && bitmapDecodeStream.getByteCount() != 0) {
                    int iG = g.a0.g(72);
                    if (iG > 192) {
                        iG = 192;
                    }
                    if (bitmapDecodeStream.getWidth() > 192 || bitmapDecodeStream.getHeight() > 192) {
                        bitmapDecodeStream = g.a0.u(bitmapDecodeStream, iG, iG);
                    }
                    this.M = this.M.F(bitmapDecodeStream, f());
                    h().putParcelable("action", this.M);
                    ((j.g) L0(2)).setImageDrawable(D0(this.M));
                    ((j.k) K0(0)).c();
                    return;
                }
                e0(R.string.invalid_file);
            } catch (Throwable th) {
                g0(th);
            }
        }
    }
}
