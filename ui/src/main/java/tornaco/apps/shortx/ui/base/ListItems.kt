@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.res.Remix

const val PREFIX_APP_ICON = "app_icon://"
const val PREFIX_MD_ICON = "md_icon://"

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    titleLines: List<String>,
    icon: @Composable () -> Unit = {},
    textDecoration: TextDecoration = TextDecoration.None,
    titleColor: Color = Color.Unspecified,
    textColor: Color = Color.Unspecified,
    action1: @Composable RowScope.() -> Unit = {},
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .thenIf(
                Modifier.combinedClickable(
                    onClick = { onClick?.invoke() },
                    onLongClick = onLongClick
                ), onClick != null
            ),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 60.dp)
                .weight(1f, fill = false)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier) {
                Text(
                    text = titleLines[0],
                    style = MaterialTheme.typography.titleMedium.copy(textDecoration = textDecoration),
                    color = titleColor
                )
                if (titleLines.size > 1) {
                    titleLines.subList(1, titleLines.size).forEach {
                        if (it.contains(PREFIX_APP_ICON)) {
                            val pkgName = it.substringAfter(PREFIX_APP_ICON)
                            Row(verticalAlignment = CenterVertically) {
                                AppIcon(modifier = Modifier.size(16.dp), pkg = Pkg(pkgName, 0))
                                SmallSpacer()
                                Text(
                                    text = it.substringBefore(PREFIX_APP_ICON),
                                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = textDecoration),
                                    color = textColor
                                )
                            }
                        } else if (it.contains(PREFIX_MD_ICON)) {
                            val iconName = it.substringAfter(PREFIX_MD_ICON)
                            Row(verticalAlignment = CenterVertically) {
                                RemixIcon(
                                    modifier = Modifier.size(16.dp),
                                    remixName = iconName
                                )
                                SmallSpacer()
                                Text(
                                    text = it.substringBefore(PREFIX_MD_ICON),
                                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = textDecoration),
                                    color = textColor
                                )
                            }
                        } else {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall.copy(textDecoration = textDecoration),
                                maxLines = 5,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }

        Row(verticalAlignment = CenterVertically) {
            action1()
        }
    }
}

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: (@Composable () -> Unit)? = null,
    action1: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .clickable {
                onClick()
            },
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 60.dp)
                .weight(1f, fill = false)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = CenterVertically
        ) {
            icon?.let {
                it()
                Spacer(modifier = Modifier.size(16.dp))
            }
            Column(modifier = Modifier) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }
        }

        action1()
    }
}

@Composable
fun ListItem(
    title: String? = null,
    text1: String? = null,
    text2: String? = null,
    overflow: @Composable ColumnScope.() -> Unit = {},
    icon: (@Composable () -> Unit)? = null,
    checked: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onCheckedChange: ((Boolean) -> Unit),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .combinedClickable(onLongClick = onLongClick) {
                onClick()
            },
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(end = 24.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = CenterVertically
        ) {
            icon?.invoke()
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier) {
                title?.let {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                        )
                    )
                }
                text1?.let {
                    TinySpacer()
                    Text(
                        text = text1,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        maxLines = 12
                    )
                    TinySpacer()
                    TinySpacer()
                    TinySpacer()
                }

                text2?.let {
                    Text(
                        text = text2,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        maxLines = 12
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            overflow()
            ShortXSwitch(
                modifier = Modifier.padding(end = 16.dp),
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}


@Composable
fun ListItem(
    icon: (@Composable () -> Unit)? = null,
    title: String,
    text1: String? = null,
    text2: String? = null,
    overflow: @Composable ColumnScope.() -> Unit = {},
    onClick: () -> Unit,
) {
    ListItem(icon = icon, title = title, texts = arrayOf(
        {
            text1?.let {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = text1,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    maxLines = 12
                )
            }
        },
        {
            text2?.let {
                Column {
                    Text(
                        modifier = Modifier.padding(top = 6.dp),
                        text = text2,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        maxLines = 12
                    )
                }
            }
        }


    ), overflow = overflow, onClick = onClick)
}


@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    title: String,
    texts: Array<@Composable () -> Unit> = arrayOf(),
    overflow: @Composable ColumnScope.() -> Unit = {},
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .clickable {
                onClick()
            },
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(end = 24.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = CenterVertically
        ) {
            icon?.invoke()
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                )
                texts.forEach {
                    it()
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            overflow()
        }
    }
}


@Composable
@Preview
fun ListItemPreview() {
    LazyColumn {
        items(10) {
            ListItem(title = "Title",
                text1 = "Text1",
                text2 = "Text2",
                checked = true,
                onClick = {},
                onCheckedChange = {}
            )
        }
    }
}

@Composable
@Preview
fun ListItem2Preview() {
    LazyColumn {
        items(10) {
            ListItem(title = "Title",
                text1 = "Text1",
                text2 = "Text2",
                onClick = {}
            )
        }
    }
}


@Composable
@Preview
fun ListItem3Preview() {
    LazyColumn {
        items(10) {
            ListItem(titleLines = listOf(
                "Title",
                "Message"
            ),
                onClick = {},
                action1 = {
                    IconButton(onClick = { /*TODO*/ }) {
                        RemixIcon(remixName = Remix.System.alarm_warning_line)
                    }
                }
            )
        }
    }
}