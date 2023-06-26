/*
 * Copyright (C) 2014 The TeamEos Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Helper functions mostly for device configuration and some utilities
 * including a fun ViewGroup crawler and dpi conversion
 *
 */

package tornaco.apps.shortx.services.app;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

// https://github.com/Evolution-X/frameworks_base/blob/tiramisu/core/java/com/android/internal/util/hwkeys/ActionUtils.java#L915
@SuppressLint("MissingPermission")
public final class ActionUtils {
    public static final String PACKAGE_SYSTEMUI = "com.android.systemui";

    // Switch to last app
    public static void switchToLastApp(Context context) {
        final ActivityManager am =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo lastTask = getLastTask(context, am);

        if (lastTask != null) {
            am.moveTaskToFront(lastTask.id, ActivityManager.MOVE_TASK_NO_USER_ACTION,
                    getAnimation(context).toBundle());
        }
    }

    private static ActivityOptions getAnimation(Context context) {
        return ActivityOptions.makeBasic();
    }

    private static ActivityManager.RunningTaskInfo getLastTask(Context context,
                                                               final ActivityManager am) {
        final List<String> packageNames = getCurrentLauncherPackages(context);
        final List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(5);
        for (int i = 1; i < tasks.size(); i++) {
            String packageName = tasks.get(i).topActivity.getPackageName();
            if (!packageName.equals(context.getPackageName())
                    && !packageName.equals(PACKAGE_SYSTEMUI)
                    && !packageNames.contains(packageName)) {
                return tasks.get(i);
            }
        }
        return null;
    }

    private static List<String> getCurrentLauncherPackages(Context context) {
        final PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> homeActivities = new ArrayList<>();
        pm.getHomeActivities(homeActivities);
        final List<String> packageNames = new ArrayList<>();
        for (ResolveInfo info : homeActivities) {
            final String name = info.activityInfo.packageName;
            if (!name.equals("com.android.settings")) {
                packageNames.add(name);
            }
        }
        return packageNames;
    }
}
