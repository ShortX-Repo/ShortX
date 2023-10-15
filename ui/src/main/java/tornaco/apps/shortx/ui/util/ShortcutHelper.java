package tornaco.apps.shortx.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.WorkerThread;

import com.google.common.io.Files;
import com.stardust.autojs.apkbuilder.ApkBuilder;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

import tornaco.apps.shortx.core.proto.common.App;
import tornaco.apps.shortx.core.util.Logger;
import tornaco.apps.shortx.ui.R;

@SuppressWarnings("UnstableApiUsage")
public class ShortcutHelper {
    private static final Logger LOGGER = new Logger("ShortcutHelper");
    private static final String STUB_APK_TEMPLATE_PATH = "apk/shortcut-stub.apk";
    private static final String OUT_APK_PATH = "shortcut_stub_apks";
    private static final String WORK_DIR_PATH = "tmp_";
    private static final String ICON_DIR_PATH = "icon.png";

    @WorkerThread
    public static File createShortcutStubApkFor(Context context,
                                         App appInfo,
                                         String appLabel,
                                         String versionName,
                                         int versionCode) throws Exception {
        File workDir = new File(Objects.requireNonNull(context.getExternalCacheDir()).getAbsolutePath()
                + File.separator
                + WORK_DIR_PATH
                + System.currentTimeMillis());
        File iconPath = new File(workDir, ICON_DIR_PATH);
        Files.createParentDirs(iconPath);
        Bitmap iconBitmap = GlideUtils.loadInCurrentThread(context, appInfo);
        if (iconBitmap == null) {
            iconBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.splash_logo);
        }
        boolean compressed = iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, Files.asByteSink(iconPath).openStream());
        if (!compressed) {
            throw new IllegalStateException("Compress fail.");
        }
        return generateStubApk(context, appInfo, workDir, iconPath, appLabel, versionName, versionCode);
    }

    private static File generateStubApk(Context context,
                                        App appInfo,
                                        File workDir,
                                        File iconPath,
                                        String appLabel,
                                        String versionName,
                                        int versionCode) throws Exception {
        File outApk = new File(String.format("%s.apk",
                Objects.requireNonNull(context
                        .getExternalCacheDir()).getAbsolutePath()
                        + File.separator
                        + OUT_APK_PATH
                        + File.separator
                        + appInfo.getPkg().getPkgName()));

        Files.createParentDirs(outApk);
        Files.createParentDirs(workDir);
        InputStream inApk = context.getAssets().open(STUB_APK_TEMPLATE_PATH);
        ApkBuilder apkBuilder = new ApkBuilder(inApk, outApk, workDir.getPath()).prepare();
        apkBuilder.editManifest()
                .setVersionCode(versionCode)
                .setVersionName(versionName)
                .setAppName(appLabel)
                .setPackageName("test.app")
                .commit();
        // Replaces/
        apkBuilder
                .replaceFile("res/mipmap-xxxhdpi-v4/ic_launcher.png", iconPath.getPath())
                .replaceFile("res/mipmap-xxhdpi-v4/ic_launcher.png", iconPath.getPath())
                .replaceFile("res/mipmap-xhdpi-v4/ic_launcher.png", iconPath.getPath())
                .replaceFile("res/mipmap-hdpi-v4/ic_launcher.png", iconPath.getPath())
                .setArscPackageName("test")
                .build()
                .sign()
                .cleanWorkspace();

        return outApk;
    }
}
