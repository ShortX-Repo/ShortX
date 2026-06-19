@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.proto.common.DownloadSubject
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class DownloadFileSelectorState(
    val onSelected: (Action.DownloadFile) -> Unit
) : CommonDialogState() {
    var url by mutableStateOf("")
    var fileName by mutableStateOf("")
    var title by mutableStateOf("")
    var description by mutableStateOf("")

    fun show(current: Action.DownloadFile? = null) {
        this.url = current?.subject?.url ?: ""
        this.fileName = current?.subject?.fileName ?: ""
        this.title = current?.subject?.title ?: ""
        this.description = current?.subject?.description ?: ""

        show()
    }

}

@Composable
fun rememberDownloadFileSelectorState(
    onSelected: (Action.DownloadFile) -> Unit
): DownloadFileSelectorState {
    return remember {
        DownloadFileSelectorState(onSelected)
    }
}

@Composable
fun DownloadFileSelectorDialog(state: DownloadFileSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            DownloadFileSelector(state)
        }
    }
}

@Composable
private fun DownloadFileSelector(
    state: DownloadFileSelectorState
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(text = LocalI18N.current[I18N_R.UI_ACTION_DOWNLOAD_FILE]) },
                    navigationIcon = {
                        IconButton(onClick = {
                            state.dismiss()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            state.onSelected(
                                Action.DownloadFile(
                                    subject = DownloadSubject.newBuilder()
                                        .setUrl(state.url)
                                        .setTitle(state.title)
                                        .setDescription(state.description)
                                        .setFileName(state.fileName)
                                        .build()
                                )
                            )
                            state.dismiss()
                        }) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = "Done")
                        }
                    },
                )
            }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    FieldTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        LocalI18N.current[I18N_R.UI_ACTION_DOWNLOAD_FILE_URL]
                    )
                    TinySpacer()
                    ShortXTextField(modifier = Modifier.fillMaxWidth(),
                        value = state.url,
                        maxLines = 6,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        onValueChange = {
                            state.url = it
                        })
                    StandardSpacer()

                    FieldTitleWithTip(
                        LocalI18N.current[I18N_R.UI_ACTION_DOWNLOAD_FILE_FILE_NAME],
                        LocalI18N.current[I18N_R.UI_ACTION_DOWNLOAD_FILE_TIP],
                    )
                    TinySpacer()
                    ShortXTextField(modifier = Modifier.fillMaxWidth(),
                        value = state.fileName,
                        onValueChange = {
                            state.fileName = it
                        })
                    StandardSpacer()

                    FieldTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        LocalI18N.current[I18N_R.UI_ACTION_DOWNLOAD_FILE_TITLE]
                    )
                    TinySpacer()
                    ShortXTextField(modifier = Modifier.fillMaxWidth(),
                        value = state.title,
                        onValueChange = {
                            state.title = it
                        })
                    StandardSpacer()

                    FieldTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        LocalI18N.current[I18N_R.UI_ACTION_DOWNLOAD_FILE_DESCRIPTION]
                    )
                    TinySpacer()
                    ShortXTextField(modifier = Modifier.fillMaxWidth(),
                        value = state.description,
                        onValueChange = {
                            state.description = it
                        })
                    StandardSpacer()
                }
            }
        }
    }
}