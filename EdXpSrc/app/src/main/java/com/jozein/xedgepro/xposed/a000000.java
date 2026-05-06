package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.robv.android.xposed.XposedBridge;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class a000000 implements View.OnClickListener {
    private List<String> G;
    private Context context;
    private r rr;

    /* JADX INFO: renamed from: 列表, reason: contains not printable characters */
    private ListView f2;

    /* JADX INFO: renamed from: com.jozein.xedgepro.xposed.a000000$100000001, reason: invalid class name */
    class AnonymousClass100000001 implements DialogInterface.OnClickListener {
        private final a000000 this$0;

        AnonymousClass100000001(a000000 a000000Var) {
            this.this$0 = a000000Var;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    }

    public a000000(Context context, r rVar, List<String> list) {
        this.context = context;
        this.rr = rVar;
        this.G = list;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        this.context = view.getContext();
        ViewGroup viewGroup = (ViewGroup) view.getParent().getParent();
        View view2 = (View) null;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            XposedBridge.log(childAt.toString());
            if (childAt.toString().contains("com.jozein.xedgepro.xposed.r$e")) {
                childAt.setVisibility(8);
                view2 = childAt;
            }
        }
        if (view2 != null) {
            TextView textView = (TextView) view;
            if (textView.getText().toString().contains("条历史记录")) {
                this.rr.M.postDelayed(new RunnableC0004(this.rr), 200);
                t3.n().u(20, 0);
                return;
            }
            if (textView.getText().equals("搜索")) {
                textView.setText(new StringBuffer().append(new StringBuffer().append("共").append(this.G.size()).toString()).append("条历史记录").toString());
                EditText editText = new EditText(this.context);
                LinearLayout linearLayout = new LinearLayout(this.context);
                this.f2 = new ListView(this.context);
                linearLayout.setOrientation(1);
                editText.setHint("搜索历史剪切记录");
                linearLayout.addView(editText, new LinearLayout.LayoutParams(-1, -2));
                linearLayout.addView(this.f2, new LinearLayout.LayoutParams(-1, -2));
                editText.addTextChangedListener(new TextWatcher(this, editText) { // from class: com.jozein.xedgepro.xposed.a000000.100000000
                    private final a000000 this$0;
                    private final EditText val$input;

                    {
                        this.this$0 = this;
                        this.val$input = editText;
                    }

                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable editable) {
                        sss(editable);
                    }

                    public void sss(Editable editable) {
                        ArrayList arrayList = new ArrayList();
                        String string = this.val$input.getText().toString();
                        if (!string.equals("")) {
                            for (String str : this.this$0.G) {
                                if (str.contains(string)) {
                                    arrayList.add(str);
                                }
                            }
                            if (arrayList.size() == 0) {
                                arrayList.add(m2());
                                arrayList.add(new StringBuffer().append(new StringBuffer().append("共").append(this.this$0.G.size()).toString()).append("个，没有查询到").toString());
                            }
                            this.this$0.f2.setAdapter((ListAdapter) new a000001(this.this$0.context, arrayList, this.this$0.rr));
                        }
                    }

                    /* JADX INFO: renamed from: 获取当前时间, reason: contains not printable characters */
                    private String m2() {
                        return new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss", Locale.getDefault()).format(new Date());
                    }
                });
                viewGroup.addView(linearLayout, view2.getLayoutParams());
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
                ((ViewGroup.LayoutParams) layoutParams).height *= 2;
                viewGroup.setLayoutParams(layoutParams);
                editText.requestFocus();
                ((InputMethodManager) this.context.getSystemService("input_method")).showSoftInput(editText, 1);
                return;
            }
            return;
        }
        Toast.makeText(this.context, "未知错误", 0).show();
    }
}
