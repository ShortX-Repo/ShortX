package androidx.test.uiautomator.adapter;

import android.content.Context;

public class InstrumentationAdapter {

    private final Context context;
    private final IUiAutomation automation;

    public InstrumentationAdapter(Context context, IUiAutomation automation) {
        this.context = context;
        this.automation = automation;
    }

    public IUiAutomation getUiAutomation(int flags) {
        return automation;
    }

    public IUiAutomation getUiAutomation() {
        return automation;
    }

    public Context getContext() {
        return context;
    }
}
