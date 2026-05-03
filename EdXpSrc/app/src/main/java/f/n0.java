package f;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;

/* JADX INFO: loaded from: classes.dex */
public abstract class n0 implements l {
    private final b A;
    private final d z;

    public interface b {
        void a();

        void b();
    }

    private static class c extends n0 {
        private Camera B;

        private c(Handler handler, Context context, b bVar) {
            super(handler, context, bVar);
            this.B = null;
        }

        @Override // f.n0
        public boolean a() {
            return this.B != null;
        }

        @Override // f.n0
        protected void f() {
            Camera camera = this.B;
            if (camera == null) {
                return;
            }
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode("off");
                this.B.setParameters(parameters);
                this.B.stopPreview();
                c();
            } catch (Throwable th) {
                v.d(th);
            }
            this.B.release();
            this.B = null;
        }

        @Override // f.n0
        protected boolean h() {
            if (this.B != null) {
                return true;
            }
            try {
                Camera cameraOpen = Camera.open();
                this.B = cameraOpen;
                Camera.Parameters parameters = cameraOpen.getParameters();
                parameters.setFlashMode("torch");
                this.B.setParameters(parameters);
                this.B.setPreviewTexture(new SurfaceTexture(0));
                this.B.startPreview();
                d(true);
                return true;
            } catch (Throwable th) {
                v.d(th);
                Camera camera = this.B;
                if (camera != null) {
                    camera.release();
                    this.B = null;
                }
                return false;
            }
        }
    }

    private class d implements Runnable {
        private final PowerManager.WakeLock A;
        protected final Handler z;

        d(Context context, Handler handler) {
            this.z = handler;
            PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, l.x + ":torch");
            this.A = wakeLockNewWakeLock;
            wakeLockNewWakeLock.setReferenceCounted(false);
        }

        void a() {
            try {
                this.z.removeCallbacks(this);
                this.z.postDelayed(this, 1200000L);
                this.A.acquire(1200000L);
            } catch (Throwable th) {
                v.d(th);
            }
        }

        void b() {
            try {
                this.z.removeCallbacks(this);
                this.A.release();
            } catch (Throwable th) {
                v.d(th);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            n0.this.e();
        }
    }

    private static class e extends n0 {
        private final Context B;
        private final Handler C;
        private CameraManager D;
        private String E;
        private boolean F;
        private boolean G;
        private boolean H;

        class a extends CameraManager.TorchCallback {
            a() {
            }

            @Override // android.hardware.camera2.CameraManager.TorchCallback
            public void onTorchModeChanged(String str, boolean z) {
                try {
                    if (e.this.E == null && z) {
                        e.this.E = str;
                    }
                    if (e.this.G == z || !str.equals(e.this.E)) {
                        return;
                    }
                    e.this.G = z;
                    if (z) {
                        e eVar = e.this;
                        eVar.d(eVar.H);
                    } else {
                        e.this.c();
                    }
                    e.this.H = false;
                } catch (Throwable th) {
                    v.d(th);
                }
            }
        }

        private e(Handler handler, Context context, b bVar) {
            super(handler, context, bVar);
            this.E = null;
            this.F = false;
            this.G = false;
            this.H = false;
            this.B = context;
            this.C = handler;
            p();
        }

        private static boolean o(CameraManager cameraManager, String str) throws CameraAccessException {
            if (str == null) {
                return false;
            }
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(str);
            Boolean bool = (Boolean) cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer num = (Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
            return bool != null && bool.booleanValue() && num != null && num.intValue() == 1;
        }

        private void p() {
            if (this.D == null) {
                CameraManager cameraManager = (CameraManager) this.B.getSystemService("camera");
                this.D = cameraManager;
                if (cameraManager == null) {
                    return;
                } else {
                    cameraManager.registerTorchCallback(new a(), this.C);
                }
            }
            String[] cameraIdList = null;
            try {
                cameraIdList = this.D.getCameraIdList();
                for (String str : cameraIdList) {
                    if (str != null && o(this.D, str)) {
                        this.E = str;
                        return;
                    }
                }
            } catch (NullPointerException e2) {
                v.c(e2.toString());
            } catch (Throwable th) {
                v.d(th);
            }
            if (cameraIdList == null || cameraIdList.length <= 0) {
                return;
            }
            this.E = cameraIdList[0];
            this.F = true;
        }

        @Override // f.n0
        public boolean a() {
            return this.G;
        }

        @Override // f.n0
        protected void f() {
            String str = this.E;
            if (str == null) {
                return;
            }
            try {
                this.H = false;
                this.D.setTorchMode(str, false);
            } catch (Throwable unused) {
            }
        }

        @Override // f.n0
        protected boolean h() {
            if (this.E == null) {
                p();
            }
            String str = this.E;
            if (str == null) {
                return false;
            }
            try {
                this.H = true;
                this.D.setTorchMode(str, true);
                return true;
            } catch (Throwable unused) {
                if (!this.F) {
                    return false;
                }
                this.E = null;
                return false;
            }
        }
    }

    private n0(Handler handler, Context context, b bVar) {
        this.z = new d(context, handler);
        this.A = bVar;
    }

    public static n0 b(Handler handler, Context context, b bVar) {
        return Build.VERSION.SDK_INT < 23 ? new c(handler, context, bVar) : new e(handler, context, bVar);
    }

    public abstract boolean a();

    void c() {
        this.z.b();
        b bVar = this.A;
        if (bVar != null) {
            bVar.a();
        }
    }

    void d(boolean z) {
        if (z) {
            this.z.a();
        }
        b bVar = this.A;
        if (bVar != null) {
            bVar.b();
        }
    }

    public final void e() {
        if (a()) {
            try {
                f();
            } catch (Throwable th) {
                v.d(th);
            }
        }
        this.z.b();
    }

    protected abstract void f();

    public final boolean g() {
        if (a()) {
            this.z.a();
            return true;
        }
        try {
            return h();
        } catch (Throwable th) {
            v.d(th);
            e();
            return false;
        }
    }

    protected abstract boolean h();
}
