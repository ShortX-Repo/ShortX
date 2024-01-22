package shortx.pluginsdk.demo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import shortx.plugin.Intents.EXTRA_ACTION_PICKER_RESULT
import shortx.plugin.ParamsData

class ActionPickerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Scaffold {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        var input by remember { mutableStateOf("") }
                        TextField(value = input, onValueChange = { input = it })

                        Spacer(modifier = Modifier.size(16.dp))

                        Button(onClick = {
                            val data = Intent().putExtra(
                                EXTRA_ACTION_PICKER_RESULT,
                                ParamsData.newBuilder().putStringData("input", input).build()
                                    .toByteArray()
                            )
                            setResult(RESULT_OK, data)
                            finish()
                        }) {
                            Text(text = "Done")
                        }
                    }
                }
            }
        }
    }
}