package tornaco.apps.shortx.ui.main

import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import dev.enro.core.NavigationKey
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.util.printManager
import tornaco.apps.shortx.ui.base.BaseActivity

@Parcelize
class MainKey : NavigationKey

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Composable
    override fun Content() {
        MainScreen()
    }

    override fun onStart() {
        super.onStart()
        val p = printManager
        p.print("ShortX", object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
            }
        }, null)
    }
}

