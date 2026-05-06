package com.jozein.xedgepro.ui;

import d.p0;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class ActivityMain extends j0 {
    @Override // e.j0
    protected j0.c v() {
        p0 p0Var = new p0();
        int intExtra = getIntent().getIntExtra("clicked", -1);
        if (intExtra >= 0) {
            p0Var.X1(intExtra);
        }
        return p0Var;
    }
}
