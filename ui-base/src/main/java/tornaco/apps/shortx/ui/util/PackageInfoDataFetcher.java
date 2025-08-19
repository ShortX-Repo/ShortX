package tornaco.apps.shortx.ui.util;


import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import tornaco.apps.shortx.core.proto.common.AppPkg;

public class PackageInfoDataFetcher implements DataFetcher<Bitmap> {

    private final AppPkg info;
    private final Context context;

    public PackageInfoDataFetcher(AppPkg info, Context context) {
        this.info = info;
        this.context = context;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Bitmap> callback) {
        Bitmap bd = AppIconLoaderUtil.loadAppIconBitmapWithIconPack(context, info.getPkgName(), info.getUserId());
        if (bd == null) {
            callback.onLoadFailed(new IllegalStateException("loadAppIconBitmapWithIconPack return null"));
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