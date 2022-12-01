package tornaco.apps.shortx.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.Formatter;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.signature.ObjectKey;

import tornaco.apps.shortx.core.App;
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

    @SuppressWarnings("NullableProblems")
    private static class PackageInfoDataFetcher implements DataFetcher<Bitmap> {

        private final App info;
        private final Context context;

        public PackageInfoDataFetcher(App info, Context context) {
            this.info = info;
            this.context = context;
        }

        @Override
        public void loadData(Priority priority, DataCallback<? super Bitmap> callback) {
            Bitmap bd = AppIconLoaderUtil.loadAppIconBitmapWithIconPack(context, getInfo().getPkg().getPkgName(), getInfo().getPkg().getUserId());
            callback.onDataReady(bd);
        }

        @Override
        public void cleanup() {

        }

        @Override
        public void cancel() {

        }

        @NonNull
        @Override
        public Class<Bitmap> getDataClass() {
            return Bitmap.class;
        }

        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }

        public App getInfo() {
            return this.info;
        }
    }

    private static class PackageIconModuleLoaderFactory
            implements ModelLoaderFactory<App, Bitmap> {
        private final Context context;

        private static final Singleton2<Context, ModelLoader<App, Bitmap>> sLoader
                = new Singleton2<Context, ModelLoader<App, Bitmap>>() {
            @Override
            protected ModelLoader<App, Bitmap> create(Context context) {
                return new ModelLoader<App, Bitmap>() {
                    @NonNull
                    @Override
                    public LoadData<Bitmap> buildLoadData(App info, int width,
                                                          int height, Options options) {
                        Key diskCacheKey = new ObjectKey(info.getPkg().getPkgName() + "-" + info.getPkg().getUserId());
                        return new LoadData<>(diskCacheKey, new PackageInfoDataFetcher(info, context));
                    }

                    @Override
                    public boolean handles(App info) {
                        return info.getPkg() != null;
                    }
                };
            }
        };

        public PackageIconModuleLoaderFactory(Context context) {
            this.context = context;
        }


        private static ModelLoader<App, Bitmap> singleInstanceLoader(Context context) {
            return sLoader.get(context);
        }

        @Override
        public ModelLoader<App, Bitmap> build(MultiModelLoaderFactory multiFactory) {
            return singleInstanceLoader(context);
        }

        @Override
        public void teardown() {

        }
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.append(App.class,
                Bitmap.class, new PackageIconModuleLoaderFactory(context));
    }
}

abstract class Singleton2<P, T> {
    private T mInstance;

    protected abstract T create(P p);

    public final T get(P p) {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create(p);
            }
            return mInstance;
        }
    }
}

