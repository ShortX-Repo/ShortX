package tornaco.apps.shortx

import android.content.Context
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.data.Dirs

class FakeServiceInstaller(private val context: Context) {
    fun install(): IShortX {
        Dirs.baseServerDirInjection = context.filesDir
        ShortXService.main()
        ShortXService.start(context)
        ShortXService.systemReady()

        val serviceBinder = ShortXService.serviceBinder
        val binderProxy = object : IShortX by serviceBinder {
            // crackVersion
            override fun getAppLabel(p0: ByteArrayWrapper?): String {
                return "ShortX"
            }
        }
        return binderProxy
    }
}