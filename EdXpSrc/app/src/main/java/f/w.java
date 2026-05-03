package f;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/* JADX INFO: loaded from: classes.dex */
public abstract class w implements l {
    public static final int A;
    public static final int B;
    public static final int C;
    public static final int D;
    public static final int z;

    private static class b extends w {
        private int E;
        private int F;

        private b() {
            super();
            this.E = 0;
            this.F = w.B;
        }

        @Override // f.w
        public Notification.Builder b(Context context) {
            return new Notification.Builder(context).setDefaults(this.E).setPriority(this.F);
        }

        @Override // f.w
        public w c(int i) {
            this.E = i;
            return this;
        }

        @Override // f.w
        public w d(int i) {
            this.F = i;
            return this;
        }
    }

    private static class c extends w {
        private final NotificationChannel E;
        private boolean F;

        c(String str, CharSequence charSequence) {
            super();
            this.F = false;
            NotificationChannel notificationChannel = new NotificationChannel(str, charSequence, w.B);
            this.E = notificationChannel;
            notificationChannel.setShowBadge(false);
            if (Build.VERSION.SDK_INT >= 29) {
                notificationChannel.setAllowBubbles(false);
            }
        }

        @Override // f.w
        public Notification.Builder b(Context context) {
            if (!this.F) {
                ((NotificationManager) context.getSystemService("notification")).createNotificationChannel(this.E);
                this.F = true;
            }
            return new Notification.Builder(context, this.E.getId());
        }

        @Override // f.w
        public w c(int i) {
            this.E.enableLights((i & 4) != 0);
            this.E.enableVibration((i & 2) != 0);
            this.E.setSound((i & 1) != 0 ? Settings.System.DEFAULT_NOTIFICATION_URI : null, Notification.AUDIO_ATTRIBUTES_DEFAULT);
            return this;
        }

        @Override // f.w
        public w d(int i) {
            this.E.setImportance(i);
            return this;
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 26) {
            z = 1;
            A = 2;
            B = 3;
            C = 4;
            D = 5;
            return;
        }
        z = -2;
        A = -1;
        B = 0;
        C = 1;
        D = 2;
    }

    private w() {
    }

    public static w a(String str, CharSequence charSequence) {
        return Build.VERSION.SDK_INT >= 26 ? new c(str, charSequence) : new b();
    }

    public abstract Notification.Builder b(Context context);

    public abstract w c(int i);

    public abstract w d(int i);
}
