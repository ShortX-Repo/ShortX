package  tornaco.apps.shortx.ui.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import tornaco.apps.shortx.services.ui.pie.AppMenu.AppIcon
import tornaco.apps.shortx.services.ui.pie.AppPieView
import tornaco.apps.shortx.services.ui.pie.PieLauncherApp

class PieDemoActivity : AppCompatActivity() {

    fun initIcon() {
        listOf(
            github.tornaco.shortx.icons.R.drawable.ic_remix_4k_fill,
            github.tornaco.shortx.icons.R.drawable.ic_remix_4k_fill,
            github.tornaco.shortx.icons.R.drawable.ic_remix_4k_fill,
            github.tornaco.shortx.icons.R.drawable.ic_remix_4k_fill
        ).forEach {
            PieLauncherApp.appMenu.icons
                .add(
                    AppIcon(
                        "Android",
                        AppCompatResources.getDrawable(this, it)
                    )
                )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val menu = AppPieView(this)
        initIcon()
        setContentView(menu)
    }
}