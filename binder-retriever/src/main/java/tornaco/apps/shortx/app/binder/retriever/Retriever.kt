package tornaco.apps.shortx.app.binder.retriever

import android.os.IBinder

class Retriever {
    external fun retrieveServiceBinder(): IBinder

    companion object {
        // Used to load the 'retriever' library on application startup.
        init {
            System.loadLibrary("retriever")
        }
    }
}