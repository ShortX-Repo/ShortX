package tornaco.apps.shortx.services.rule.actor.mvel.handle.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;

import tornaco.apps.shortx.core.util.PkgUtils;
import tornaco.apps.shortx.services.ShortXContext;
import tornaco.apps.shortx.services.rule.actor.mvel.handle.IActivity;

public class ActivityImpl implements IActivity {
    private final Context context;
    private final ShortXContext s;

    public ActivityImpl(Context context, ShortXContext s) {
        this.context = context;
        this.s = s;
    }

    @Override
    public boolean launchProcessForPackage(String pkgName) {
        if (!PkgUtils.isPkgInstalled(context, pkgName)) return false;
        return true;
    }

    @Override
    public boolean launchActivity(Intent intent) {
        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean launchMainActivityForPackage(String pkgName) {
        if (!PkgUtils.isPkgInstalled(context, pkgName)) return false;
        Intent main = getLaunchIntentForPackage(pkgName);
        if (main == null) return false;
        return launchActivity(main);
    }

    @Override
    public boolean launchActivityForUser(Intent intent, int userId) {
        return false;
    }

    @Override
    public boolean launchMainActivityForPackageForUser(String pkgName, int userId) {
        return false;
    }

    @Override
    public Intent getLaunchIntentForPackage(String pkgName) {
        return getLaunchIntentForPackage(pkgName, UserHandle.getCallingUserId());
    }

    @Override
    public Intent getLaunchIntentForPackage(String pkgName, int userId) {
        if (!PkgUtils.isPkgInstalled(context, pkgName, userId)) {
            return null;
        }
        return context.getPackageManager()
                .getLaunchIntentForPackage(pkgName);
    }

    @Override
    public String getFrontAppPackage() {
        return null;
    }

    @Override
    public ComponentName getFrontAppPackageComponent() {
        return null;
    }

    @Override
    public void setInactive(String pkgName) {

    }

    @Override
    public boolean isInactive(String pkgName) {
        return false;
    }

}
