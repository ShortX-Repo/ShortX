package tornaco.apps.shortx.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.WorkerThread;

import com.android.tools.build.apkzlib.sign.SigningExtension;
import com.android.tools.build.apkzlib.sign.SigningOptions;
import com.android.tools.build.apkzlib.zip.AlignmentRules;
import com.android.tools.build.apkzlib.zip.ZFile;
import com.android.tools.build.apkzlib.zip.ZFileOptions;
import com.google.common.io.Files;
import com.stardust.autojs.apkbuilder.ApkBuilder;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
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

    private static final ZFileOptions Z_FILE_OPTIONS = new ZFileOptions().setAlignmentRule(AlignmentRules.compose(
            AlignmentRules.constantForSuffix(".so", 4096),
            AlignmentRules.constantForSuffix(".apk", 4096)
    ));


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
        File stubApk = generateStubApk(context, workDir, iconPath, appLabel, versionName, packageName, versionCode);
        signApk(context, stubApk);
        return stubApk;
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

    private static void signApk(Context context, File apkFile) {
        try {
            ZFile dstZFile = ZFile.openReadWrite(apkFile, Z_FILE_OPTIONS);
            var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // Followed by 4 arguments: keystore path, keystore password, keystore alias, keystore alias password
            // List<String> keystoreArgs = Arrays.asList(null, "123456", "key0", "123456");
            try (var is = context.getAssets().open("apk/keystore")) {
                keyStore.load(is, "123456".toCharArray());
            }
            var entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("shortx", new KeyStore.PasswordProtection("123456".toCharArray()));
            new SigningExtension(SigningOptions.builder()
                    .setMinSdkVersion(28)
                    .setV2SigningEnabled(true)
                    .setCertificates((X509Certificate[]) entry.getCertificateChain())
                    .setKey(entry.getPrivateKey())
                    .build()).register(dstZFile);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to signApk", e);
        }
    }
}
