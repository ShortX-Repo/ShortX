package tornaco.apps.shortx.j

import android.os.IBinder

class Retriever {
    external fun retrieveServiceBinder(): IBinder

    companion object {
        init {
            System.loadLibrary("services")
        }
    }
}