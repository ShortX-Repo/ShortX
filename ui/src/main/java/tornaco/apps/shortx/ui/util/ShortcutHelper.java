package tornaco.apps.shortx.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.WorkerThread;

import com.google.common.io.Files;
import com.stardust.autojs.apkbuilder.ApkBuilder;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

import kotlin.io.FilesKt;
import tornaco.apps.shortx.core.util.Logger;
import tornaco.apps.shortx.ui.base.IconsKt;

public class ShortcutHelper {
    private static final Logger LOGGER = new Logger("ShortcutHelper");
    private static final String STUB_APK_TEMPLATE_PATH = "apk/shortcut-stub.apk";
    private static final String OUT_APK_PATH = "shortcut_stub_apks";
    private static final String WORK_DIR_PATH = "tmp_";
    private static final String ICON_DIR_PATH = "icon.png";

    public static void cleanUp(Context context) {
        try {
            FilesKt.deleteRecursively(new File(context.getFilesDir().getAbsolutePath()
                    + File.separator
                    + OUT_APK_PATH));
            LOGGER.i("cleanUp complete.");
        } catch (Throwable ignored) {
        }
    }

    @WorkerThread
    public static File createShortcutStubApkFor(Context context,
                                                String appLabel,
                                                String iconName,
                                                String versionName,
                                                String packageName,
                                                int versionCode) throws Exception {
        File workDir = new File(Objects.requireNonNull(context.getExternalCacheDir()).getAbsolutePath()
                + File.separator
                + WORK_DIR_PATH
                + System.currentTimeMillis());
        File iconPath = new File(workDir, ICON_DIR_PATH);
        Files.createParentDirs(iconPath);


        Bitmap bitmap = IconUtils.INSTANCE.getCenteredAndColoredBitmap(context, IconsKt.remixResId(context, iconName), Color.BLUE, 64, 64);
        boolean compressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, Files.asByteSink(iconPath).openStream());
        if (!compressed) {
            throw new IllegalStateException("Compress fail.");
        }
        return generateStubApk(context, workDir, iconPath, appLabel, versionName, packageName, versionCode);
    }

    private static File generateStubApk(Context context,
                                        File workDir,
                                        File iconPath,
                                        String appLabel,
                                        String versionName,
                                        String packageName,
                                        int versionCode) throws Exception {
        File outApk = new File(String.format("%s.apk",
                context.getFilesDir().getAbsolutePath()
                        + File.separator
                        + OUT_APK_PATH
                        + File.separator
                        + packageName));

        Files.createParentDirs(outApk);
        Files.createParentDirs(workDir);
        InputStream inApk = context.getAssets().open(STUB_APK_TEMPLATE_PATH);
        ApkBuilder apkBuilder = new ApkBuilder(inApk, outApk, workDir.getPath()).prepare();
        apkBuilder.editManifest()
                .setVersionCode(versionCode)
                .setVersionName(versionName)
                .setAppName(appLabel)
                .setPackageName(packageName)
                .commit();
        // Replaces/
        apkBuilder
                .replaceFile("res/o-.png", iconPath.getPath())
                .replaceFile("res/RJ.png", iconPath.getPath())
                .replaceFile("res/FS.png", iconPath.getPath())
                .replaceFile("res/yn.png", iconPath.getPath())
                .replaceFile("res/9w.png", iconPath.getPath())
                .setArscPackageName("test")
                .build()
                .sign()
                .cleanWorkspace();

        return outApk;
    }
}
