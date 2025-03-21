package tornaco.apps.shortx.services.app

import android.os.Bundle

interface ActivityStartResultOneTimeListener {
    fun onStartActivityResult(requestCode: Int, resultCode: Int, result: Bundle)
}