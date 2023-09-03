package  tornaco.apps.shortx.ui.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DemoPage(modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        ColorText("Hello")
        ColorText("Hello")
        androidx.compose.material3.Button(onClick = { }) {
            Text(text = "Clik me")
        }
    }

    val names = ArrayList<String>()
    for (i in 0..1000) {
        names.add("name" + i)
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(names) {
            Text(text = it)
        }
    }
}

@Composable
fun ColorText(title: String) {
    Text(text = title, color = MaterialTheme.colorScheme.primary)
}

class ComposeDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar {
                            Text(text = "Hello")
                        }
                    }
                ) {
                    DemoPage(
                        Modifier
                            .fillMaxSize()
                            .padding(it)
                    )
                }
            }
        }
    }
}


