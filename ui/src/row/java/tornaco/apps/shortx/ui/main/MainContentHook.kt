package tornaco.apps.shortx.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun MainContentHook() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        CheckForUpdate(context).invoke()
    }
}