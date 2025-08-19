package com.android.dx.stock;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

import tornaco.apps.shortx.core.util.Logger;

public abstract class ProxyFactory<T> {
    private final Logger logger = new Logger("ProxyFactory");

    public final T newProxy(T original, File baseDataDir) {
        try {
            return onCreateProxy(original, dxCacheDir(baseDataDir));
        } catch (Throwable e) {
            logger.e(e, "BaseProxyFactory fail create proxy" + getClass() + " - " + original);
            return null;
        }
    }

    protected abstract T onCreateProxy(T original, File dexCacheDir) throws Exception;

    private File dxCacheDir(File baseDir) throws IOException {
        // Dex cache dir.
        File dx = new File(baseDir, "dx");
        logger.i("Using dxCacheDir as dx dir: " + dx);
        // FileUtils.deleteDirQuiet(dx);
        //noinspection UnstableApiUsage
        Files.createParentDirs(new File(dx, "dummy"));
        return dx;
    }
}
