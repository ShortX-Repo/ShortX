package tornaco.apps.shortx.ui.demo

import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tornaco.apps.shortx.core.util.Logger

class PickRingtoneActivity : AppCompatActivity() {

    private val RINGTONE_PICKER_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL)
        startActivityForResult(intent, RINGTONE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RINGTONE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri =
                data?.getParcelableExtra<android.net.Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            // 使用选择的铃声 URI 进行其他操作
            Logger().w("onActivityResult= $uri")
        }
    }
}