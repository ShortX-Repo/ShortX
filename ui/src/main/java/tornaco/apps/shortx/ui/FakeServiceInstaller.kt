package tornaco.apps.shortx.ui

import android.content.Context
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.services.ShortXService

class FakeServiceInstaller(private val context: Context) {
    fun install(): IShortX.Stub {
        ShortXService.main()
        ShortXService.start(context)
        ShortXService.systemReady()

        return ShortXService.serviceBinder
    }
}