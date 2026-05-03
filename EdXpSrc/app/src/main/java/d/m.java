package d;

import a.b;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import cc.xposed.R;
import e.j;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/* JADX INFO: loaded from: classes.dex */
public class m extends e.j {
    private static final String P;
    private static final String Q;
    private static d R;
    private ArrayList<File> M;
    private boolean N = false;
    private boolean O = false;

    class a extends BroadcastReceiver {
        a() {
        }

        private void a(Context context, String str) {
            m.this.W1(str);
            try {
                context.unregisterReceiver(this);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String message;
            byte[] byteArrayExtra = intent.getByteArrayExtra("buffer");
            String stringExtra = intent.getStringExtra("name");
            if (byteArrayExtra == null || stringExtra == null) {
                message = null;
            } else {
                try {
                    File file = new File(f.l.l + stringExtra);
                    File parentFile = file.getParentFile();
                    if (parentFile != null && !parentFile.exists() && !parentFile.mkdir()) {
                        f.v.c("Error import " + file.getName());
                        return;
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    try {
                        fileOutputStream.write(byteArrayExtra);
                        fileOutputStream.close();
                        return;
                    } finally {
                    }
                } catch (IOException e2) {
                    f.v.d(e2);
                    message = e2.getMessage();
                }
            }
            a(context, message);
        }
    }

    private static class b implements f {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final OutputStream f355a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private final String f356b;

        b(OutputStream outputStream, String str) {
            this.f355a = outputStream;
            this.f356b = str;
        }

        @Override // d.m.f
        public String a() throws IOException {
            f.r0 r0Var = new f.r0(this.f356b);
            r0Var.f(a.j.F);
            try {
                FileInputStream fileInputStream = new FileInputStream(this.f356b);
                try {
                    f.r.c(fileInputStream, this.f355a);
                    fileInputStream.close();
                    try {
                        r0Var.a().delete();
                        return "";
                    } catch (Throwable unused) {
                        return "";
                    }
                } finally {
                }
            } finally {
                this.f355a.close();
            }
        }
    }

    private static class c implements f {
        private c() {
        }

        /* synthetic */ c(a aVar) {
            this();
        }

        @Override // d.m.f
        public String a() throws IOException {
            String strF;
            File file = new File(m.P);
            if (!file.exists() && !file.mkdir()) {
                throw new IOException("Failed to make dir!");
            }
            do {
                strF = f.p0.f();
            } while (new File(m.P + strF + ".bak").exists());
            new f.r0(m.P + strF + ".bak").f(a.j.F);
            return strF;
        }
    }

    private static class d extends AsyncTask<f, Void, String> {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private m f357a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private String f358b = null;

        d(m mVar) {
            this.f357a = mVar;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public String doInBackground(f... fVarArr) {
            try {
                return fVarArr[0].a();
            } catch (Throwable th) {
                f.v.d(th);
                this.f358b = th.getMessage();
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
        public void onPostExecute(String str) {
            d unused = m.R = null;
            m mVar = this.f357a;
            if (mVar != null) {
                mVar.P1(str, this.f358b);
            }
        }

        void c(m mVar) {
            this.f357a = mVar;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            m mVar = this.f357a;
            if (mVar != null) {
                mVar.O = true;
                mVar.N(new e.e0(), 0);
            }
        }
    }

    private static class e extends g {

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private final InputStream f359b;

        e(InputStream inputStream, String str) {
            super(str);
            this.f359b = inputStream;
        }

        @Override // d.m.g, d.m.f
        public String a() throws IOException {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(this.f360a);
                try {
                    f.r.c(this.f359b, fileOutputStream);
                    fileOutputStream.close();
                    this.f359b.close();
                    super.a();
                    try {
                        this.f360a.delete();
                        return null;
                    } catch (Throwable unused) {
                        return null;
                    }
                } finally {
                }
            } catch (Throwable th) {
                this.f359b.close();
                throw th;
            }
        }
    }

    private interface f {
        String a();
    }

    private static class g implements f {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final File f360a;

        g(String str) {
            this.f360a = new File(str);
        }

        @Override // d.m.f
        public String a() throws IOException {
            f.r0 r0Var = new f.r0(this.f360a);
            String str = a.p.P;
            String str2 = f.l.l;
            if (!r0Var.b(str.substring(str2.length()), f.l.p)) {
                throw new IOException("Invalid file!");
            }
            f.r.g(a.j.F);
            r0Var.d(str2);
            a.z.w0();
            return null;
        }
    }

    static {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (absolutePath.endsWith(f.l.j)) {
            absolutePath = absolutePath.substring(0, (absolutePath.length() - r1.length()) - 1);
        }
        P = absolutePath + "/XEdgePro/";
        Q = absolutePath + "/Android/data/XEdgePro/";
        R = null;
    }

    private boolean I1() {
        String str = a.p.P;
        String str2 = f.l.j;
        String str3 = f.l.v;
        return new File(str.replace(str2, str3)).exists() || f.z.K(f()).U(str3);
    }

    static boolean J1(Context context) {
        int i = Build.VERSION.SDK_INT;
        return (i < 30 || Environment.isExternalStorageLegacy()) ? i < 23 || context.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0 : Environment.isExternalStorageManager();
    }

    private static long K1(ContentResolver contentResolver, Uri uri) {
        String scheme = uri.getScheme();
        if ("android.resource".equals(scheme)) {
            return -1L;
        }
        if (!"file".equals(scheme)) {
            AssetFileDescriptor assetFileDescriptorOpenAssetFileDescriptor = contentResolver.openAssetFileDescriptor(uri, "r", null);
            if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                return assetFileDescriptorOpenAssetFileDescriptor.getLength();
            }
        } else if (uri.getPath() != null) {
            return new File(uri.getPath()).length();
        }
        return -1L;
    }

    private static void L1(Intent intent) {
        intent.addCategory("android.intent.category.OPENABLE");
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension("bak");
        if (mimeTypeFromExtension == null) {
            mimeTypeFromExtension = "*/*";
        }
        intent.setType(mimeTypeFromExtension);
    }

    private String M1(int i) {
        return this.M.get(i - 4).getName().substring(0, r3.length() - 4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean N1(File file, String str) {
        return str.endsWith(".bak");
    }

    private void O1() {
        try {
            String str = Q;
            File file = new File(str);
            if (file.exists()) {
                String str2 = P;
                f.r.i(file, new File(str2));
                f.v.c("Backups moved from " + str + " to " + str2);
            }
        } catch (Throwable unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void P1(String str, String str2) {
        Fragment fragmentN = n();
        if (fragmentN instanceof e.e0) {
            ((e.e0) fragmentN).dismiss();
        }
        this.O = false;
        if ("".equals(str)) {
            e0(R.string.saved);
            return;
        }
        if (str != null) {
            this.M.add(new File(P + str + ".bak"));
            w0();
            return;
        }
        if (str2 == null) {
            Q1();
        } else if ("Invalid file!".equals(str2)) {
            e0(R.string.invalid_file);
        } else {
            f0(str2);
        }
    }

    private void Q1() {
        f0(u(R.string.settings_restored));
        try {
            g().n();
            getActivity().recreate();
        } catch (Throwable th) {
            f.v.d(th);
        }
        Context contextF = f();
        if (a.j.m(contextF)) {
            f.v.c("send files after restored.");
            a.j.g(contextF, true);
        } else {
            f.v.c("notify restore.");
            b.s0.B(contextF, 25);
        }
    }

    private void R1() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        L1(intent);
        startActivityForResult(intent, 1);
    }

    private void S1() {
        Intent intent = new Intent("android.intent.action.CREATE_DOCUMENT");
        intent.putExtra("android.intent.extra.TITLE", "xposed-edge-pro-backup.bak");
        L1(intent);
        startActivityForResult(intent, 2);
    }

    private void T1(f fVar) {
        d dVar = new d(this);
        R = dVar;
        dVar.execute(fVar);
    }

    private void U1() throws Throwable {
        V1();
    }

    private void V1() throws Throwable {
        a aVar;
        Throwable th;
        this.O = true;
        Context contextF = f();
        try {
            aVar = new a();
        } catch (Throwable th2) {
            aVar = null;
            th = th2;
        }
        try {
            contextF.registerReceiver(aVar, new IntentFilter(a.j.A));
            f.r.g(a.j.F);
            a.j.f(contextF);
            N(new e.e0(), 0);
        } catch (Throwable th3) {
            th = th3;
            this.O = false;
            if (aVar != null) {
                contextF.unregisterReceiver(aVar);
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void W1(String str) {
        this.O = false;
        a.z.w0();
        Fragment fragmentN = n();
        if (fragmentN instanceof DialogFragment) {
            ((DialogFragment) fragmentN).dismiss();
        }
        if (str != null) {
            f0(str);
        } else {
            Q1();
        }
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.backups);
    }

    @Override // e.j
    protected int B0() {
        File[] fileArrListFiles = new File(P).listFiles(new FilenameFilter() { // from class: d.l
            @Override // java.io.FilenameFilter
            public final boolean accept(File file, String str) {
                return m.N1(file, str);
            }
        });
        if (fileArrListFiles == null) {
            this.M = new ArrayList<>();
        } else {
            ArrayList<File> arrayList = new ArrayList<>(fileArrListFiles.length);
            this.M = arrayList;
            Collections.addAll(arrayList, fileArrListFiles);
        }
        this.N = I1();
        return this.M.size() + 4;
    }

    @Override // e.j0.c
    protected void J(Bundle bundle, int i) {
        CharSequence charSequence;
        if (bundle == null) {
            return;
        }
        try {
            if (i == 1) {
                int i2 = bundle.getInt("result", -1);
                int iE0 = E0();
                if (i2 == 0) {
                    T1(new g(this.M.get(iE0 - 4).getPath()));
                    return;
                }
                if (i2 != 1) {
                    if (i2 != 2) {
                        return;
                    }
                    N(new e.y().J(u(R.string.enter_name), null, M1(iE0), 8, 64), 3);
                } else {
                    int i3 = iE0 - 4;
                    this.M.get(i3).delete();
                    this.M.remove(i3);
                    u1(iE0);
                }
            } else {
                if (i != 2) {
                    if (i == 3 && (charSequence = bundle.getCharSequence("result", null)) != null && charSequence.length() > 0) {
                        String strTrim = charSequence.toString().trim();
                        if (strTrim.length() <= 0) {
                            return;
                        }
                        int iE02 = E0();
                        int i4 = iE02 - 4;
                        String str = P + strTrim + ".bak";
                        File file = this.M.get(i4);
                        if (str.equals(file.getAbsolutePath())) {
                            return;
                        }
                        File file2 = new File(str);
                        if (file.renameTo(file2)) {
                            this.M.set(i4, file2);
                            ((j.k) L0(iE02)).setText(strTrim);
                            return;
                        } else {
                            f.v.c("Failed to rename to " + str);
                            return;
                        }
                    }
                    return;
                }
                if (!bundle.getBoolean("result", false)) {
                } else {
                    U1();
                }
            }
        } catch (Throwable th) {
            g0(th);
        }
    }

    @Override // e.j
    protected View h1(int i) {
        if (i == 0) {
            j.k kVar = new j.k(u(R.string.add_backup), (CharSequence) null);
            if (!J1(M0())) {
                kVar.e();
            }
            return kVar;
        }
        if (i == 1) {
            return new j.k(u(R.string.add_backup_to), (CharSequence) null);
        }
        if (i != 2) {
            return i != 3 ? new j.k(this, M1(i)) : new j.k(u(R.string.open_from), (CharSequence) null);
        }
        j.k kVar2 = new j.k(u(R.string.import_from_free_version), (CharSequence) null);
        kVar2.setEnabled(this.N);
        return kVar2;
    }

    @Override // e.j
    protected void k1(int i) {
        if (this.O) {
            f.v.c("working...");
            return;
        }
        if (i == 0) {
            if (J1(M0())) {
                T1(new c(null));
                return;
            } else {
                e0(R.string.permission_denied);
                return;
            }
        }
        if (i == 1) {
            S1();
            return;
        }
        if (i == 2) {
            if (this.N) {
                N(new e.m().u(u(R.string.check_import_from_free_version)), 2);
            }
        } else if (i != 3) {
            N(new e.z().u(new CharSequence[]{u(R.string.restore), u(R.string.delete), u(R.string.rename)}), 1);
        } else {
            R1();
        }
    }

    @Override // android.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        f bVar;
        if (i2 != -1) {
            return;
        }
        Uri data = intent.getData();
        Activity activity = getActivity();
        if (data == null || activity == null) {
            return;
        }
        try {
            ContentResolver contentResolver = activity.getContentResolver();
            if (i == 1) {
                if (K1(contentResolver, data) > 20971520) {
                    e0(R.string.invalid_file);
                    return;
                }
                InputStream inputStreamOpenInputStream = contentResolver.openInputStream(data);
                if (inputStreamOpenInputStream == null) {
                    throw new IOException("Failed to open input stream!");
                }
                bVar = new e(inputStreamOpenInputStream, activity.getCacheDir() + "/tmp.bak");
            } else {
                if (i != 2) {
                    f.v.c(Arrays.toString(new File(P + "XEdgePro").list()));
                    return;
                }
                OutputStream outputStreamOpenOutputStream = contentResolver.openOutputStream(data);
                if (outputStreamOpenOutputStream == null) {
                    throw new IOException("Failed to open output stream!");
                }
                bVar = new b(outputStreamOpenOutputStream, activity.getCacheDir() + "/tmp.bak");
            }
            T1(bVar);
        } catch (Throwable th) {
            g0(th);
        }
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        d dVar = R;
        if (dVar != null) {
            dVar.c(this);
        }
        O1();
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        d dVar = R;
        if (dVar != null) {
            dVar.c(null);
        }
    }

    @Override // e.j0.c
    protected void y() {
        if (this.O) {
            W1(null);
        } else {
            super.y();
        }
    }
}
