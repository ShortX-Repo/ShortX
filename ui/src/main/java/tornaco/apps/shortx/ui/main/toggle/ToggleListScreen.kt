package tornaco.apps.shortx.ui.main.toggle

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.base.EmptyList
import tornaco.apps.shortx.ui.main.MainViewModel


@Composable
fun ToggleListScreen() {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val mainState by mainViewModel.state.collectAsState()

}
