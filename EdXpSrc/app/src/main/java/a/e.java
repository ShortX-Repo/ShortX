package a;

import android.content.Context;
import f.h0;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class e extends l<b> {
    private static final String G = f.l.m + "collection";
    private static volatile e H = null;
    private boolean E;
    private List<b> F;

    private e() {
        super(null, G);
        this.E = false;
    }

    public static e A() {
        e eVar;
        synchronized (e.class) {
            if (H == null) {
                H = new e();
            }
            eVar = H;
        }
        return eVar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void B(Context context) {
        List<b> list;
        if (this.E || (list = this.F) == null) {
            return;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            try {
                e(context, this.F.get(i));
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        this.F = null;
    }

    public static void D() {
        synchronized (e.class) {
            H = null;
        }
    }

    public void C() {
        this.E = true;
    }

    public void E(Context context, b bVar) {
        if (!this.E) {
            e(context, bVar);
            return;
        }
        if (this.F == null) {
            this.F = new ArrayList();
        }
        this.F.add(bVar);
    }

    public void F(final Context context) {
        this.E = false;
        if (this.F == null) {
            return;
        }
        h0.a().post(new Runnable() { // from class: a.d
            @Override // java.lang.Runnable
            public final void run() {
                this.z.B(context);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // a.l
    /* JADX INFO: renamed from: G, reason: merged with bridge method [inline-methods] */
    public void x(f.s sVar, b bVar) {
        sVar.f(bVar);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // a.l
    /* JADX INFO: renamed from: z, reason: merged with bridge method [inline-methods] */
    public b g(f.s sVar) {
        return sVar.g();
    }
}
