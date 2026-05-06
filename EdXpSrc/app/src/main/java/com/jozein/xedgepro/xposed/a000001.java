package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class a000001 extends BaseAdapter {
    private Context context;
    private List<String> data;
    private r rr;

    public a000001(Context context, List<String> list, r rVar) {
        this.context = context;
        this.data = list;
        this.rr = rVar;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.data.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.data.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        LinearLayout linearLayout = new LinearLayout(this.context);
        TextView textView = new TextView(this.context);
        textView.setTextSize(20);
        textView.setBackgroundColor(0);
        textView.setText(this.data.get(i));
        linearLayout.setBackgroundColor(0);
        linearLayout.addView(textView);
        textView.setMaxLines(2);
        textView.setTextColor(-1);
        textView.setOnClickListener(new AnonymousClass100000001(this));
        return linearLayout;
    }

    /* JADX INFO: renamed from: com.jozein.xedgepro.xposed.a000001$100000000, reason: invalid class name */
    class AnonymousClass100000000 implements View.OnClickListener {
        private final a000001 this$0;

        AnonymousClass100000000(a000001 a000001Var) {
            this.this$0 = a000001Var;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Toast.makeText(view.getContext(), ((TextView) view).getText(), 0).show();
        }
    }

    /* JADX INFO: renamed from: com.jozein.xedgepro.xposed.a000001$100000001, reason: invalid class name */
    class AnonymousClass100000001 implements View.OnClickListener {
        private final a000001 this$0;

        AnonymousClass100000001(a000001 a000001Var) {
            this.this$0 = a000001Var;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            this.this$0.rr.M.postDelayed(new Runnable(this, view) { // from class: com.jozein.xedgepro.xposed.a000001.100000001.100000000
                private final AnonymousClass100000001 this$0;
                private final View val$p1;

                {
                    this.this$0 = this;
                    this.val$p1 = view;
                }

                @Override // java.lang.Runnable
                public void run() {
                    String string = ((TextView) this.val$p1).getText().toString();
                    if (string == null || string.length() <= 0) {
                        return;
                    }
                    xx(string);
                }

                private void xx(String str) {
                    this.this$0.this$0.rr.I.o2(str, false);
                }
            }, 200);
            t3.n().u(20, 0);
        }
    }
}
