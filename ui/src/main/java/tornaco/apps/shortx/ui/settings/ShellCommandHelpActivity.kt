package tornaco.apps.shortx.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tornaco.apps.shortx.core.os.CancellationSignal
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import javax.inject.Inject

data class ShellCommandHelpState(
    val isLoading: Boolean = true,
    val content: String = "",
    val error: String? = null,
)

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class ShellCommandHelpViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(ShellCommandHelpState())
    val state: StateFlow<ShellCommandHelpState> = _state

    private var currentSignal: CancellationSignal? = null

    fun load() {
        currentSignal?.cancel()
        _state.value = ShellCommandHelpState(isLoading = true)
        currentSignal = shortXManager.executeShellCommand("am shortx") { result ->
            val content = buildString {
                if (result.outList.isNotEmpty()) {
                    append(result.outList.joinToString(System.lineSeparator()))
                }
                if (result.errList.isNotEmpty()) {
                    if (isNotEmpty()) append(System.lineSeparator()).append(System.lineSeparator())
                    append(result.errList.joinToString(System.lineSeparator()))
                }
            }.trim()

            _state.value = if (result.code == 0 || content.isNotEmpty()) {
                ShellCommandHelpState(
                    isLoading = false,
                    content = content.ifEmpty { "No shell help output." },
                    error = null,
                )
            } else {
                ShellCommandHelpState(
                    isLoading = false,
                    content = "",
                    error = "Failed to load shell help (exit code: ${result.code}).",
                )
            }
        }
    }

    override fun onCleared() {
        currentSignal?.cancel()
        super.onCleared()
    }
}

@AndroidEntryPoint
class ShellCommandHelpActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, ShellCommandHelpActivity::class.java))
        }
    }

    @Composable
    override fun Content() {
        val viewModel = hiltViewModel<ShellCommandHelpViewModel>()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(viewModel) {
            viewModel.load()
        }

        ShellCommandHelpScreen(
            state = state,
            refresh = { viewModel.load() },
            back = { finish() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShellCommandHelpScreen(
    state: ShellCommandHelpState,
    refresh: () -> Unit,
    back: () -> Unit,
) {
    val i18N = LocalI18N.current
    Scaffold(
        topBar = {
            ShortXTopAppBar(
                title = { Text(text = i18N["ui.settings.shell.commands.page.title"]) },
                navigationIcon = {
                    IconButton(onClick = back) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                actions = {
                    IconButton(onClick = refresh) {
                        RemixIcon(remixName = Remix.System.refresh_line)
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Loading(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            state.error != null -> {
                LoadError(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    message = state.error ?: i18N["ui.settings.shell.commands.load.error"],
                    refresh = refresh,
                )
            }

            else -> {
                SelectionContainer {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = state.content,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
