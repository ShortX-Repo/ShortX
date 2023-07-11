package tornaco.apps.shortx.services.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * ServiceProxy is a superclass for proxy objects which make a single call to a service. It handles
 * connecting to the service, running a task supplied by the subclass when the connection is ready,
 * and disconnecting delegate the service afterwards. ServiceProxy objects cannot be reused (trying to
 * do so generates an {@link IllegalStateException}).
 * <p/>
 * Subclasses must override {@link #onConnected} to store the binder. Then, when the subclass wants
 * to make a service call, it should call {@link #setTask}, supplying the {@link ProxyTask} that
 * should run when the connection is ready. {@link ProxyTask#run} should implement the necessary
 * logic to make the call on the service.
 */

public abstract class AppServiceProxy {

    public static final String EXTRA_FORCE_SHUTDOWN = "ServiceProxy.FORCE_SHUTDOWN";

    private static final boolean DEBUG_PROXY = false; // DO NOT CHECK THIS IN SET TO TRUE
    protected final String mTag;
    protected final Intent mIntent;
    private final Context mContext;
    private final ServiceConnection mConnection = new ProxyConnection();
    private ProxyTask mTask;
    private String mName = " unnamed";
    // Service call timeout (in seconds)
    private int mTimeout = 60;
    private long mStartTime;
    private boolean mTaskSet = false;
    private boolean mTaskCompleted = false;

    private final Executor asyncExecutor = Executors.newFixedThreadPool(3);

    public AppServiceProxy(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
        mTag = getClass().getSimpleName();
    }

    /**
     * This function is called after the proxy connects to the service but before it runs its task.
     * Subclasses must override this to store the binder correctly.
     *
     * @param binder The service IBinder.
     */
    public abstract void onConnected(IBinder binder);

    public int getTimeout() {
        return mTimeout;
    }

    public AppServiceProxy setTimeout(int secs) {
        mTimeout = secs;
        return this;
    }

    protected boolean setTask(ProxyTask task) throws IllegalStateException {
        return setTask(task, "NO-NAME");
    }

    protected boolean setTask(ProxyTask task, String name) throws IllegalStateException {
        if (mTaskSet) {
            throw new IllegalStateException("Cannot call setTask twice on the same ServiceProxy.");
        }
        mTaskSet = true;
        mName = name;
        mTask = task;
        mStartTime = System.currentTimeMillis();
        if (DEBUG_PROXY) {
            Logger.d("Bind requested for task " + mName);
        }
        return mContext.bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Callers that want to wait on the {@link ProxyTask} should call this immediately after calling
     * {@link #setTask}. This will wait until the task completes, up to the timeout (which can be
     * set with {@link #setTimeout}).
     */
    protected void waitForCompletion() {
        synchronized (mConnection) {
            long time = System.currentTimeMillis();
            try {
                if (DEBUG_PROXY) {
                    Logger.d("Waiting for task " + mName + " to complete...");
                }
                mConnection.wait(mTimeout * 1000L);
            } catch (InterruptedException e) {
                // Can be ignored safely
            }
            if (DEBUG_PROXY) {
                Logger.d("Wait for " + mName +
                        (mTaskCompleted ? " finished in " : " timed out in ") +
                        (System.currentTimeMillis() - time) + "ms");
            }
        }
    }

    /**
     * Connection test; return indicates whether the remote service can be connected to
     *
     * @return the result of trying to connect to the remote service
     */
    public boolean test() {
        try {
            return setTask(new ProxyTask() {
                @Override
                public void run() throws RemoteException {
                    if (DEBUG_PROXY) {
                        Logger.d("Connection test succeeded in " +
                                (System.currentTimeMillis() - mStartTime) + "ms");
                    }
                }
            }, "test");
        } catch (Exception e) {
            // For any failure, return false.
            return false;
        }
    }

    protected abstract class ProxyTask {
        public abstract void run() throws RemoteException;

        public boolean forUI() {
            return false;
        }
    }

    private class ProxyConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            if (DEBUG_PROXY) {
                Logger.d("Connected: " + name.getShortClassName() + " at " +
                        (System.currentTimeMillis() - mStartTime) + "ms");
            }

            // Let subclasses handle the binder.
            onConnected(binder);

            // Do our work in another thread.
            if (mTask.forUI()) {
                call();
            } else {
                asyncExecutor.execute(this::call);
            }
        }

        private Void call() {
            try {
                mTask.run();
            } catch (RemoteException e) {
                Logger.d("RemoteException thrown running mTask!");
            } finally {
                // Make sure that we unbind the mConnection even on exceptions in the
                // task provided by the subclass.
                try {
                    // Each ServiceProxy handles just one task, so we unbind after we're
                    // done with our work.
                    mContext.unbindService(mConnection);
                } catch (RuntimeException e) {
                    // The exceptions that are thrown here look like IllegalStateException,
                    // IllegalArgumentException and RuntimeException. Catching
                    // RuntimeException which get them all. Reasons for these exceptions
                    // include services that have already been stopped or unbound. This can
                    // happen if the user ended the activity that was using the service.
                    // This is harmless, but we've got to catch it.
                    Logger.d("RuntimeException when trying to unbind delegate service");
                }
            }
            mTaskCompleted = true;
            synchronized (mConnection) {
                if (DEBUG_PROXY) {
                    Logger.d("Task " + mName + " completed; disconnecting");
                }
                mConnection.notify();
            }
            return null;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (DEBUG_PROXY) {
                Logger.d("Disconnected: " + name.getShortClassName() + " at " +
                        (System.currentTimeMillis() - mStartTime) + "ms");
            }
        }
    }

    private static class Logger {
        static void d(String format, Object... args) {
            Log.d("AppServiceProxy", String.format(format, args));
        }

        static void w(String format, Object... args) {
            Log.w("AppServiceProxy", String.format(format, args));
        }
    }
}