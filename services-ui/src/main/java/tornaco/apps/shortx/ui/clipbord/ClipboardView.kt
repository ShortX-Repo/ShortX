package tornaco.apps.shortx.ui.clipbord

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.clipboard.ClipboardItem
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MD3Badge
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.PrefIcon
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme
import java.util.Date
import java.util.UUID

@SuppressLint("ViewConstructor")
class ClipboardView(
    context: Context,
    i18N: I18N,
    val clips: List<ClipboardItem>,
    onDetach: (BaseComposeView) -> Unit,
    val inputText: (String) -> Unit,
    val addText: (ClipboardItem) -> Unit,
    val removeText: (id: String) -> Unit,
    val clearAll: () -> Unit,
) : BaseComposeView(context, i18N, onDetach) {

    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme {
            val interactionSource = remember { MutableInteractionSource() }
            val requester = remember { FocusRequester() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onKeyEvent {
                        if (it.key == Key.Back) {
                            detach()
                        }
                        true
                    }
                    .focusRequester(requester)
                    .focusable()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            detach()
                        }
                    ), contentAlignment = Alignment.BottomCenter
            ) {
                LaunchedEffect(Unit) {
                    requester.requestFocus()
                }

                Surface(
                    modifier = Modifier,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    ),
                    color = ColorDefaults.backgroundSurfaceColor(),
                    shadowElevation = 8.dp,
                ) {
                    var clipState by remember { mutableStateOf(clips) }
                    var inputState by remember { mutableStateOf("") }
                    var isShowInput by remember { mutableStateOf(false) }

                    val i18N = LocalI18N.current

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction = 0.4f),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .background(ColorDefaults.backgroundSurfaceColor(elevation = 4.dp))
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MediumTitle(
                                    text = i18N["ui.action.show.clip.board"] + "(${clipState.size})",
                                    color = MaterialTheme.colorScheme.onSurface,
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Add
                                    IconButton(onClick = {
                                        isShowInput = true
                                    }) {
                                        ColoredIcon(
                                            size = 28.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        ) {
                                            RemixIcon(
                                                Remix.System.add_fill,
                                                modifier = Modifier.size(20.dp),
                                                tint = Color.White
                                            )
                                        }
                                    }

                                    // More
                                    val dropdownState = rememberDropdownSelectorState()
                                    Box {
                                        DropdownSelector(
                                            widthFraction = 0.4f,
                                            state = dropdownState,
                                            items = listOf(
                                                DropdownItem(
                                                    labelLines = listOf(i18N["ui.action.show.clip.board.clear.all"]),
                                                    data = "clearAll"
                                                )
                                            )
                                        ) {
                                            clearAll()
                                            clipState = emptyList()
                                        }

                                        IconButton(onClick = {
                                            dropdownState.open()
                                        }) {
                                            RemixIcon(
                                                Remix.System.more_2_fill,
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            AnimatedVisibility(visible = isShowInput) {
                                ShortXTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    showSymbolButton = false,
                                    value = inputState,
                                    onValueChange = {
                                        inputState = it
                                    }, trailingIcon = {
                                        IconButton(onClick = {
                                            val item = ClipboardItem.newBuilder()
                                                .setCreateAt(System.currentTimeMillis())
                                                .setId(UUID.randomUUID().toString())
                                                .setValue(inputState)
                                                .build()
                                            addText(item)
                                            clipState = clipState.toMutableList().apply {
                                                add(0, item)
                                            }
                                            isShowInput = false
                                            inputState = ""
                                        }) {
                                            RemixIcon(
                                                modifier = Modifier.size(18.dp),
                                                remixName = Remix.System.check_line,
                                                tint = MaterialTheme.colorScheme.onSurface,
                                            )
                                        }
                                    })
                            }
                        }


                        if (clipState.isNotEmpty()) {
                            itemsIndexed(clipState) { index, clip: ClipboardItem ->
                                val context = LocalContext.current
                                val formatTime = remember {
                                    if (System.currentTimeMillis() - clip.createAt < java.time.Duration.ofHours(
                                            12
                                        ).toMillis()
                                    ) {
                                        FuzzyDateTimeFormatter.getTimeAgo(
                                            context,
                                            Date(clip.createAt)
                                        )
                                    } else {
                                        DateUtils.formatLongForMessageTime(clip.createAt)
                                    }
                                }
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                detach()
                                                inputText(clip.value)
                                            }
                                            .padding(horizontal = 16.dp)
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.weight(1f, fill = false)
                                        ) {
                                            MD3Badge(text = {
                                                Text(
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    text = (index + 1).toString(),
                                                    fontSize = 10.5.sp
                                                )
                                            })

                                            TinySpacer()

                                            Column {
                                                Text(
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    text = clip.value,
                                                    maxLines = 3,
                                                    fontSize = 12.5.sp
                                                )
                                                Text(
                                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.8f
                                                    ),
                                                    text = formatTime,
                                                    fontSize = 10.5.sp
                                                )
                                            }
                                        }

                                        IconButton(onClick = {
                                            removeText(clip.id)
                                            clipState = clipState.toMutableList().apply {
                                                removeIf { it.id == clip.id }
                                            }
                                        }) {
                                            RemixIcon(
                                                modifier = Modifier.size(18.dp),
                                                remixName = Remix.System.close_fill,
                                                tint = PrefIcon
                                            )
                                        }
                                    }
                                    Divider(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp),
                                        thickness = 0.5.dp
                                    )
                                }
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 72.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "\uD83E\uDDD0",
                                        style = MaterialTheme.typography.displayLarge
                                    )
                                }
                            }
                        }
                    }
                }


            }
        }
    }
}