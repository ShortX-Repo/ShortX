@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.base.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.mapper.displayNameKey
import tornaco.apps.shortx.ui.addrule.fact.mapper.supportsGestureIndicator
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.gesture.ScreenRectPickerActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme

data class EdgeGestureSelectDialogResult(
    val edge: Edge,
    val gesture: Gesture,
    val isIntercept: Boolean,
    val apps: List<App>,
    val pkgSet: List<PkgSet>,
    val customRect: Rect? = null,
    val enableGestureHint: Boolean = true,
)

class EdgeGestureSelectorState(
    val showEdgeHint: (Edge) -> Unit,
    val selected: (EdgeGestureSelectDialogResult) -> Unit
) : CommonDialogState() {
    var edge by mutableStateOf(Edge.BottomRight)
    var gesture by mutableStateOf(Gesture.DoubleTap)
    var isIntercept by mutableStateOf(false)
    var enableGestureHint by mutableStateOf(true)
    var customRect by mutableStateOf<Rect?>(null)
    var useCustomRect by mutableStateOf(false)
    var rectInputText by mutableStateOf("")

    val apps = mutableStateListOf<App>()
    val pkgSets = mutableStateListOf<PkgSet>()

    fun show(data: Fact.EdgeGesture?) {
        composeLogger.d("EdgeGestureSelectorState show: $data")
        this.edge = data?.edge ?: Edge.BottomRight
        this.gesture = data?.gesture ?: Gesture.SingleTap
        this.isIntercept = data?.isIntercept ?: false
        this.enableGestureHint = data?.enableGestureHint ?: true
        this.customRect = data?.customRect
        this.useCustomRect = data?.customRect != null

        // 初始化输入框文本
        this.rectInputText = data?.customRect?.let { rect ->
            "${rect.left} ${rect.top} ${rect.right} ${rect.bottom}"
        } ?: ""

        this.apps.clear()
        this.pkgSets.clear()
        this.apps.addAll(data?.apps ?: emptyList())
        this.pkgSets.addAll(data?.pkgSets ?: emptyList())
        show()
    }

    fun parseRectFromInput(): Rect? {
        return try {
            val parts = rectInputText.trim().split("\\s+".toRegex())
            if (parts.size == 4) {
                val left = parts[0].toInt()
                val top = parts[1].toInt()
                val right = parts[2].toInt()
                val bottom = parts[3].toInt()
                Rect.newBuilder()
                    .setLeft(left)
                    .setTop(top)
                    .setRight(right)
                    .setBottom(bottom)
                    .build()
            } else {
                null
            }
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun updateRectFromInput() {
        customRect = parseRectFromInput()
    }

    fun getResult(): EdgeGestureSelectDialogResult {
        return EdgeGestureSelectDialogResult(
            edge = edge,
            gesture = gesture,
            isIntercept = isIntercept,
            apps = apps,
            pkgSet = pkgSets,
            customRect = if (useCustomRect) customRect else null,
            enableGestureHint = enableGestureHint
        )
    }
}

@Composable
fun rememberEdgeGestureSelectorState(
    showEdgeHint: (Edge) -> Unit,
    selected: (EdgeGestureSelectDialogResult) -> Unit
): EdgeGestureSelectorState {
    return remember {
        EdgeGestureSelectorState(showEdgeHint, selected)
    }
}

@Composable
fun EdgeGestureSelector(state: EdgeGestureSelectorState) {
    if (state.isShowing) {
        val context = LocalContext.current

        // Activity launcher for selecting screen area
        val screenRectPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val rect = result.data?.getSerializableExtra("rect") as Rect?
                if (rect != null) {
                    state.customRect = rect
                }
            }
        }

        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }, content = {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(LocalI18N.current["ui.fact.edge.gesture"]) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
                                        state.selected(state.getResult())
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.check_line)
                                    }
                                },
                            )
                        }) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingValues)
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            val i18N = LocalI18N.current
                            FieldTitle(
                                modifier = Modifier.padding(vertical = 12.dp),
                                text = i18N["ui.fact.edge.gesture.position"]
                            )

                            // Custom Rect selection area
                            AnimatedVisibility(visible = state.useCustomRect) {
                                Column {
                                    SmallTitle(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        text = i18N["ui.fact.edge.gesture.custom.rect.area"]
                                    )

                                    // Rect输入框
                                    ShortXTextField(
                                        value = state.rectInputText,
                                        onValueChange = {
                                            state.rectInputText = it
                                            state.updateRectFromInput()
                                        },
                                        label = {
                                            Text(
                                                i18N["ui.fact.edge.gesture.rect.input.label"]
                                            )
                                        },
                                        placeholder = { Text("例如: 100 200 300 400") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                        isError = state.rectInputText.isNotEmpty() && state.parseRectFromInput() == null
                                    )

                                    if (state.rectInputText.isNotEmpty() && state.parseRectFromInput() == null) {
                                        Text(
                                            text = i18N["ui.fact.edge.gesture.rect.input.error"],
                                            color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Display current selected area info
                                    val rect = state.customRect
                                    if (rect != null) {
                                        Text(
                                            text = i18N["ui.fact.edge.gesture.selected.area"].replace(
                                                "{left}",
                                                rect.left.toString()
                                            )
                                                .replace("{top}", rect.top.toString())
                                                .replace("{right}", rect.right.toString())
                                                .replace("{bottom}", rect.bottom.toString()),
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )

                                        // 添加显示区域位置的按钮
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = {
                                                shortXManager.showScreenRectHint(rect)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(i18N["ui.fact.edge.gesture.show.selected.area"])
                                        }
                                    } else {
                                        Text(
                                            text = i18N["ui.fact.edge.gesture.no.area.selected"],
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }

                                    // Select area button
                                    TextButton(
                                        onClick = {
                                            screenRectPickerLauncher.launch(
                                                ScreenRectPickerActivity.start(context)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(i18N["ui.fact.edge.gesture.select.screen.area"])
                                    }

                                    StandardSpacer()
                                    TipRow(text = i18N["ui.fact.edge.gesture.select.area.tip"])
                                    StandardSpacer()
                                }
                            }

                            // Edge position selection (shown when not using custom Rect)
                            AnimatedVisibility(visible = !state.useCustomRect) {
                                Box {
                                    val dropdownState = rememberDropdownSelectorState()

                                    Column {
                                        ListItem(
                                            title = i18N[state.edge.displayNameKey()],
                                            action1 = {
                                                IconButton(onClick = { dropdownState.open() }) {
                                                    RemixIcon(remixName = Remix.Arrows.arrow_right_s_line)
                                                }
                                            },
                                            onClick = {
                                                dropdownState.open()
                                            })
                                    }

                                    DropdownSelector(
                                        state = dropdownState,
                                        items = Edge.entries.filterNot { it == Edge.UNRECOGNIZED }
                                            .map {
                                                DropdownItem(
                                                    labelLines = listOf(i18N[it.displayNameKey()]),
                                                    data = it
                                                )
                                            },
                                        onSelect = {
                                            state.edge = it.data
                                            state.showEdgeHint(it.data)
                                        }
                                    )
                                }
                            }

                            // Custom Rect toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SmallTitle(text = i18N["ui.fact.edge.gesture.use.custom.rect"])
                                ShortXSwitch(checked = state.useCustomRect, onCheckedChange = {
                                    state.useCustomRect = it
                                    if (!it) {
                                        state.customRect = null
                                    }
                                })
                            }

                            StandardSpacer()

                            Box {
                                val dropdownState = rememberDropdownSelectorState()
                                Column {
                                    FieldTitle(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        text = i18N["ui.fact.edge.gesture.gesture"]
                                    )
                                    ListItem(
                                        title = i18N[state.gesture.displayNameKey()],
                                        action1 = {
                                            IconButton(onClick = { dropdownState.open() }) {
                                                RemixIcon(remixName = Remix.Arrows.arrow_right_s_line)
                                            }
                                        },
                                        onClick = {
                                            dropdownState.open()
                                        })
                                }
                                DropdownSelector(
                                    state = dropdownState,
                                    items = Gesture.entries.filterNot { it == Gesture.UNRECOGNIZED }
                                        .map {
                                            DropdownItem(
                                                labelLines = listOf(i18N[it.displayNameKey()]),
                                                data = it
                                            )
                                        },
                                    onSelect = {
                                        state.gesture = it.data
                                    }
                                )
                            }

                            StandardSpacer()
                            AnimatedVisibility(visible = state.gesture.supportsGestureIndicator()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    FieldTitle(text = i18N["ui.fact.edge.gesture.indicator"])
                                    ShortXSwitch(
                                        checked = state.enableGestureHint,
                                        onCheckedChange = {
                                            state.enableGestureHint = it
                                        }
                                    )
                                }
                            }

                            if (state.gesture.supportsGestureIndicator()) {
                                StandardSpacer()
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                FieldTitle(text = LocalI18N.current["ui.fact.key.event.adv.is.intercept.mode"])
                                ShortXSwitch(checked = state.isIntercept, onCheckedChange = {
                                    state.isIntercept = it
                                })
                            }
                            StandardSpacer()

                            val appFgPkgSelectorState =
                                rememberAppPkgSelectorState(
                                    onAppListSelected = { pkg ->
                                        state.apps.clear()
                                        state.pkgSets.clear()
                                        state.apps.addAll(pkg)
                                    }, onPkgSetSelected = {
                                        state.apps.clear()
                                        state.pkgSets.clear()
                                        state.pkgSets.addAll(it)
                                    })
                            AppPkgSelectorDialog(appFgPkgSelectorState)

                            val appPkgSetText = mutableListOf(
                                LocalI18N.current[
                                    if (state.apps.isEmpty() && state.pkgSets.isEmpty()) {
                                        "ui.fact.edge.gesture.front.app.any"
                                    } else {
                                        "ui.fact.edge.gesture.front.app"
                                    }
                                ]
                            ).labelLinesFromAppsAndPkgSets(
                                state.apps,
                                state.pkgSets
                            )
                            StandardSpacer()
                            FieldTitle(text = LocalI18N.current["ui.fact.edge.gesture.front.app"])
                            ListItem(titleLines = appPkgSetText, onClick = {
                                appFgPkgSelectorState.show(
                                    state.apps,
                                    state.pkgSets
                                )
                            })
                            LargeSpacer()

                            AnimatedVisibility(visible = state.isIntercept) {
                                TipRow(text = LocalI18N.current["ui.fact.motion.event.adv.summary"])
                                StandardSpacer()
                                StandardSpacer()
                            }
                        }
                    }
                }
            }
        })
    }
}

