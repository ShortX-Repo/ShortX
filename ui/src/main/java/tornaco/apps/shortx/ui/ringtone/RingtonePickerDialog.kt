@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.ringtone


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.common.Ringtone
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.theme.ShortXTheme

class RingtonePickerState(
    val title: String,
    val onPick: (Ringtone) -> Unit
) : CommonDialogState()

@Composable
fun rememberRingtonePickerState(
    title: String,
    onPick: (Ringtone) -> Unit
): RingtonePickerState {
    return remember {
        RingtonePickerState(title, onPick)
    }
}

@Composable
fun RingtonePickerDialog(state: RingtonePickerState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            RingtonePicker(state)
        }
    }
}

@Composable
private fun RingtonePicker(selectorState: RingtonePickerState) {
    val vm = hiltViewModel<RingtoneListViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = selectorState.title) },
                        actions = {},
                        navigationIcon = {
                            IconButton(onClick = {
                                selectorState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        }
                    )
                }) { paddingValues ->

                val onClick: (Ringtone) -> Unit = {
                    selectorState.onPick(it)
                    selectorState.dismiss()
                }

                val play: (Ringtone) -> Unit = {
                    vm.play(it)
                }

                if (state.isLoading) {
                    Loading(modifier = Modifier.fillMaxSize())
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        var selectedTabIndex by remember { mutableIntStateOf(0) }
                        TabRow(selectedTabIndex = selectedTabIndex) {
                            val titles = listOf(
                                LocalI18N.current["ui.ringtone.type.alarm"],
                                LocalI18N.current["ui.ringtone.type.notification"],
                                LocalI18N.current["ui.ringtone.type.ringtone"]
                            )
                            titles.forEachIndexed { index, title ->
                                Tab(selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = {
                                        Text(
                                            text = title,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    })
                            }
                        }

                        when (selectedTabIndex) {
                            0 -> {
                                LazyColumn {
                                    items(state.alarms) {
                                        RingtoneItem(it, play = {
                                            play(it)
                                        }) {
                                            onClick(it)
                                        }
                                    }
                                }
                            }

                            1 -> {
                                LazyColumn {
                                    items(state.notifications) {
                                        RingtoneItem(it, play = {
                                            play(it)
                                        }) {
                                            onClick(it)
                                        }
                                    }
                                }
                            }

                            2 -> {
                                LazyColumn {
                                    items(state.ringtones) {
                                        RingtoneItem(it, play = {
                                            play(it)
                                        }) {
                                            onClick(it)
                                        }
                                    }
                                }
                            }
                        }

                    }

                }

            }
        }
    }
}

@Composable
private fun RingtoneHeader(header: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        MediumTitle(text = header)
    }
}

@Composable
private fun RingtoneItem(
    ringtone: Ringtone,
    play: () -> Unit,
    onClick: () -> Unit,
) {
    ListItem(title = ringtone.title, onClick = onClick, action1 = {
        IconButton(onClick = {
            play()
        }) {
            RemixIcon(remixName = Remix.Media.play_circle_fill)
        }
    })
}
