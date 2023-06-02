@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)

package tornaco.apps.shortx.ui.record

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionIcon
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ErrorMessageDialog
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.rememberErrorMessageDialogState
import tornaco.apps.shortx.ui.theme.ErrorRed
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.theme.SuccessGreen

@Composable
fun ActionEvaRecordScreenDialog(state: CommonDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ActionEvaRecordScreen(back = { state.dismiss() })
        }
    }
}

@Composable
fun ActionEvaRecordScreen(back: () -> Unit) {
    val vm = hiltViewModel<ActionEvaRecordViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val i18N = LocalI18N.current
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    ShortXMediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = i18N["ui.settings.action.record"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {},
                        scrollBehavior = scrollBehavior
                    )
                }) { paddingValues ->
                if (state.isLoading) {
                    Loading(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = paddingValues
                    ) {
                        items(state.records) {
                            Box {
                                val actionLabel = labelAndDescriptionForActionSelector(
                                    i18N,
                                    it.actionIns::class
                                ).first
                                val messageDialogState = rememberErrorMessageDialogState(
                                    title = actionLabel,
                                    message = it.record.message
                                )
                                ErrorMessageDialog(state = messageDialogState)

                                ListItem(
                                    modifier = Modifier.padding(end = 16.dp),
                                    titleLines = listOf(actionLabel, it.formattedTimeStr),
                                    icon = {
                                        ActionIcon(it.actionIns)
                                    },
                                    action1 = {
                                        if (it.record.isSuccess) {
                                            RemixIcon(
                                                remixName = Remix.System.checkbox_circle_fill,
                                                tint = SuccessGreen
                                            )
                                        } else {
                                            RemixIcon(
                                                remixName = Remix.System.error_warning_fill,
                                                tint = ErrorRed
                                            )
                                        }
                                    },
                                    onClick = {
                                        if (!it.record.isSuccess) messageDialogState.show()
                                    })
                            }
                        }
                    }
                }


            }
        }
    }
}