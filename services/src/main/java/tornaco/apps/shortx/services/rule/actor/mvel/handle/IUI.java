package tornaco.apps.shortx.services.rule.actor.mvel.handle;

import android.annotation.NonNull;
import android.annotation.Nullable;

@HandlerName("ui")
public
interface IUI {
    void showShortToast(@NonNull Object msg);

    void showLongToast(@NonNull Object msg);

    void showDialog(@Nullable String title,
                    @NonNull String msg,
                    @Nullable String yes);
}
