package tornaco.apps.shortx.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.base.requireActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel = hiltViewModel<MainViewModel>(LocalContext.current.requireActivity())
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    viewModel.bindLifecycle(lifecycle)
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.loadGestureSettings()
    }

    ShortXTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(modifier = Modifier, topBar = {
                TopAppBar(title = {
                    Text(text = "ShortX")
                })
            }) {
                Column(Modifier.padding(it).padding(vertical = 16.dp)) {
                    state.areaSettings.forEach { settings ->
                        AreaSettings(settings)
                    }
                }
            }

        }
    }
}

@Composable
fun AreaSettings(settings: AreaSettings) {
    settings.gestureActions.forEach { gestureAction ->
        Row(modifier = Modifier.fillMaxWidth().clickable {

            }.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_category_24),
                contentDescription = "")
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier) {
                Text(text = gestureAction.gestureName)
                Text(text = gestureAction.actionName, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}