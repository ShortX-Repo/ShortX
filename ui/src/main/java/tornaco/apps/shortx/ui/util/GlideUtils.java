package tornaco.apps.shortx.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import tornaco.apps.shortx.core.proto.common.App;
import tornaco.apps.shortx.ui.R;

public class GlideUtils {
    @WorkerThread
    public static Bitmap loadInCurrentThread(Context context, App appInfo) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<Bitmap> ref = new AtomicReference<>();
        GlideApp.with(context)
                .asBitmap()
                .load(appInfo.getPkg())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.splash_logo)
                .fallback(R.drawable.splash_logo)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ref.set(resource);
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        ref.set(null);
                        countDownLatch.countDown();
                    }
                });
        try {
            countDownLatch.await();
        } catch (InterruptedException ignored) {
        }

        return ref.get();
    }
}
