package tornaco.apps.shortx.ui.util;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import tornaco.apps.shortx.core.proto.common.AppPkg;

public class PackageIconModuleLoaderFactory
        implements ModelLoaderFactory<AppPkg, Bitmap> {
    private final Context context;

    private static final Singleton2Params<Context, ModelLoader<AppPkg, Bitmap>> sLoader
            = new Singleton2Params<>() {
        @Override
        protected ModelLoader<AppPkg, Bitmap> create(Context context) {
            return new ModelLoader<>() {
                @NonNull
                @Override
                public LoadData<Bitmap> buildLoadData(@NonNull AppPkg info, int width,
                                                      int height, @NonNull Options options) {
                    Key diskCacheKey = new ObjectKey(info.getPkgName() + "-" + info.getUserId());
                    return new LoadData<>(diskCacheKey, new PackageInfoDataFetcher(info, context));
                }

                @Override
                public boolean handles(@NonNull AppPkg info) {
                    return true;
                }
            };
        }
    };

    public PackageIconModuleLoaderFactory(Context context) {
        this.context = context;
    }


    private static ModelLoader<AppPkg, Bitmap> singleInstanceLoader(Context context) {
        return sLoader.get(context);
    }

    @NonNull
    @Override
    public ModelLoader<AppPkg, Bitmap> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return singleInstanceLoader(context);
    }

    @Override
    public void teardown() {

    }
}