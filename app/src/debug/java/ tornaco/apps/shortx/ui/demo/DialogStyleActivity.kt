package  tornaco.apps.shortx.ui.demo

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tornaco.apps.shortx.R

// https://poran-cse.medium.com/creating-a-floating-alert-dialog-like-google-translate-with-material-design-3-in-android-4b5cbab57571
class DialogStyleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showAlertDialog("Android")
    }

    private fun showAlertDialog(selectedText: String) {
        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_launcher_foreground)
            //.setView() you can set custom view here
            .setTitle("Floating Window")
            .setMessage(selectedText)
            .setOnDismissListener {
                onBackPressed()
            }
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                onBackPressed()
            }
            .create().apply {
                window?.setGravity(Gravity.BOTTOM)
                show()
            }
    }
}