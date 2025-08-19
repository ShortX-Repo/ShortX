package tornaco.apps.shortx.ui.util;


import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import tornaco.apps.shortx.core.util.BitmapUtils;

class Base64DataFetcher implements DataFetcher<Bitmap> {

    private final Base64ImageSrc info;

    public Base64DataFetcher(Base64ImageSrc info) {
        this.info = info;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Bitmap> callback) {
        Bitmap bd = BitmapUtils.base64ToBitmap(info.getBase64());
        if (bd == null) {
            callback.onLoadFailed(new IllegalStateException("base64ToBitmap return null"));
        } else {
            callback.onDataReady(bd);
        }

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
}

class Base64ModuleLoaderFactory
        implements ModelLoaderFactory<Base64ImageSrc, Bitmap> {
    private final Context context;

    private static final Singleton2Params<Context, ModelLoader<Base64ImageSrc, Bitmap>> sLoader
            = new Singleton2Params<>() {
        @Override
        protected ModelLoader<Base64ImageSrc, Bitmap> create(Context context) {
            return new ModelLoader<>() {
                @NonNull
                @Override
                public LoadData<Bitmap> buildLoadData(@NonNull Base64ImageSrc info, int width,
                                                      int height, @NonNull Options options) {
                    Key diskCacheKey = new ObjectKey(info.getBase64().hashCode());
                    return new LoadData<>(diskCacheKey, new Base64DataFetcher(info));
                }

                @Override
                public boolean handles(@NonNull Base64ImageSrc info) {
                    return true;
                }
            };
        }
    };

    public Base64ModuleLoaderFactory(Context context) {
        this.context = context;
    }


    private static ModelLoader<Base64ImageSrc, Bitmap> singleInstanceLoader(Context context) {
        return sLoader.get(context);
    }

    @NonNull
    @Override
    public ModelLoader<Base64ImageSrc, Bitmap> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return singleInstanceLoader(context);
    }

    @Override
    public void teardown() {

    }
}