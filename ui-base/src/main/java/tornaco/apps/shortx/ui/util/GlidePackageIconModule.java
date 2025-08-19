package tornaco.apps.shortx.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.Formatter;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import tornaco.apps.shortx.core.http.HttpCallerImpl;
import tornaco.apps.shortx.core.http.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import tornaco.apps.shortx.core.proto.common.AppPkg;
import tornaco.apps.shortx.core.util.Logger;

/**
 * Created by guohao4 on 2017/12/23.
 * Email: Tornaco@163.com
 */
@GlideModule
public class GlidePackageIconModule extends AppGlideModule {
    private static final Logger logger = new Logger("GlidePackageIconModule");

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        int bitmapPoolSizeBytes = (int) (Runtime.getRuntime().maxMemory() / 8);
        logger.w("GlidePackageIconModule, mem cache size: " + Formatter.formatFileSize(context, bitmapPoolSizeBytes));
        builder.setBitmapPool(new LruBitmapPool(bitmapPoolSizeBytes));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.append(AppPkg.class,
                Bitmap.class, new PackageIconModuleLoaderFactory(context));
        registry.append(Base64ImageSrc.class,
                Bitmap.class, new Base64ModuleLoaderFactory(context));

        try {
            // glide trust anchor for certification path not found
            HttpCallerImpl caller = new HttpCallerImpl(new SharedPrefsCookiePersistor(context), s -> null);
            registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(caller.clientTrustAllCerts));
        } catch (Throwable e) {
            logger.e(e, "registry.OkHttpUrlLoader");
        }
    }
}

