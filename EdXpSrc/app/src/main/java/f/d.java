package f;

import f.d;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static volatile ExecutorService f432a;

    /* JADX INFO: Access modifiers changed from: private */
    static class a extends Thread {
        private static final AtomicInteger z = new AtomicInteger(1);

        a(Runnable runnable) {
            super(runnable, "Async #" + z.getAndIncrement());
        }

        a(Runnable runnable, String str) {
            super(runnable, str);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                super.run();
            } catch (Throwable th) {
                v.d(th);
            }
        }
    }

    public static void b(Runnable runnable) {
        c().execute(runnable);
    }

    public static Executor c() {
        if (f432a == null) {
            synchronized (d.class) {
                if (f432a == null) {
                    f432a = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadFactory() { // from class: f.c
                        @Override // java.util.concurrent.ThreadFactory
                        public final Thread newThread(Runnable runnable) {
                            return new d.a(runnable);
                        }
                    });
                }
            }
        }
        return f432a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Thread d(String str, Runnable runnable) {
        return new a(runnable, str);
    }

    public static ExecutorService e(final String str) {
        return Executors.newSingleThreadExecutor(new ThreadFactory() { // from class: f.b
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return d.d(str, runnable);
            }
        });
    }

    public static void f(long j) {
        try {
            Thread.sleep(j);
        } catch (Throwable th) {
            v.d(th);
        }
    }
}
