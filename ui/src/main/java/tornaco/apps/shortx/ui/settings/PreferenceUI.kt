package tornaco.apps.shortx.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CategoryTitle
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.thenIf

@Composable
fun PreferenceUi(preferences: List<Preference>) {
    splitListByCategory(preferences).forEach { group ->
        val firstPrefIndex = group.indexOfFirst { !it.isDecor }
        val lastPrefIndex = group.indexOfLast { !it.isDecor }
        val onlyOnePref = group.filter { !it.isDecor }.size == 1
        Column(Modifier.padding(top = 16.dp)) {
            group.forEachIndexed { index, pref ->
                Box(
                    Modifier
                        .thenIf(Modifier.padding(horizontal = 16.dp), !pref.isDecor)
                        .thenIf(Modifier.clip(RoundedCornerShape(24.dp)), onlyOnePref)
                        .thenIf(
                            Modifier.clip(
                                when (index) {
                                    firstPrefIndex -> RoundedCornerShape(
                                        topStart = 24.dp,
                                        topEnd = 24.dp,
                                        bottomStart = 4.dp,
                                        bottomEnd = 4.dp
                                    )

                                    lastPrefIndex -> RoundedCornerShape(
                                        topStart = 4.dp,
                                        topEnd = 4.dp,
                                        bottomStart = 24.dp,
                                        bottomEnd = 24.dp
                                    )

                                    else -> RoundedCornerShape(4.dp)
                                }
                            ), !onlyOnePref
                        )
                        .thenIf(
                            Modifier.background(ColorDefaults.backgroundSurfaceColor(0.8.dp)),
                            !pref.isDecor
                        )
                ) {
                    PreferenceUi(preference = pref)
                }
                SmallSpacer()
            }
        }
    }
}

private fun splitListByCategory(list: List<Preference>): List<List<Preference>> {
    val result = mutableListOf<MutableList<Preference>>()
    var currentList = mutableListOf<Preference>()

    list.forEach { item ->
        if (item is Preference.Category) {
            if (currentList.isNotEmpty()) {
                result.add(currentList)
            }
            currentList = mutableListOf()  // 每次遇到A开始一个新的子List
        }
        currentList.add(item)
    }

    // 添加最后一组
    if (currentList.isNotEmpty()) {
        result.add(currentList)
    }
    return result
}


@Composable
private fun PreferenceUi(preference: Preference) {
    when (preference) {
        is Preference.SwitchPreference -> {
            SwitchPreferenceUi(preference = preference)
        }

        is Preference.TextPreference -> {
            TextPreferenceUi(preference = preference)
        }

        is Preference.Divider -> {
            HorizontalDivider()
        }

        is Preference.Category -> {
            StandardSpacer()
            CategoryUi(preference = preference)
        }

        is Preference.ExpandablePreference -> {
            ExpandablePreferenceUi(preference = preference)
        }
    }
}

@Composable
private fun CategoryUi(preference: Preference.Category) {
    Column {
        CategoryTitle(title = preference.title)
        TinySpacer()
    }
}

@Composable
private fun ExpandablePreferenceUi(preference: Preference.ExpandablePreference) {
    ExpandableContainer(
        mainContent = {
            TextPreferenceUi(
                preference = Preference.TextPreference(
                    title = preference.title,
                    summary = preference.summary,
                    icon = preference.icon,
                    onClick = preference.onClick
                )
            )
        }, expandContent = {
            Column {
                preference.preferences.forEach {
                    PreferenceUi(it)
                }
            }
        })
}

@Composable
private fun TextPreferenceUi(preference: Preference.TextPreference) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    delay(128)
                    preference.onClick?.invoke()
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 64.dp)
                .weight(1f, fill = false)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PreferenceIcon(preference)
            StandardSpacer()
            Column(modifier = Modifier) {
                Text(
                    modifier = Modifier.padding(end = 36.dp),
                    text = preference.title, style = MaterialTheme.typography.titleMedium
                )
                preference.summary?.let {
                    SmallSpacer()
                    Text(
                        modifier = Modifier.padding(end = 36.dp),
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        preference.badge?.let {
            OutlineBadge(modifier = Modifier.padding(end = 16.dp), text = it)
        }
    }
}

@Composable
private fun PreferenceIcon(preference: Preference) {
    preference.icon?.let {
        ColoredIcon(
            size = 24.dp,
            color = prefColorForPreference(preference),
        ) {
            RemixIcon(
                modifier = Modifier.size(12.dp),
                remixName = it,
                tint = Color.White
            )
        }
    }
}


@Composable
private fun SwitchPreferenceUi(preference: Preference.SwitchPreference) {
    var isShowMoreSummary by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (preference.onClick != null) {
                    preference.onClick.invoke()
                } else {
                    if (preference.hasLongSummary) {
                        isShowMoreSummary = !isShowMoreSummary
                    } else {
                        preference.onCheckChanged(!preference.value)
                    }
                }
            }, verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 64.dp)
                .weight(1f, fill = false)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PreferenceIcon(preference)
            StandardSpacer()
            Column(modifier = Modifier) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(
                        text = preference.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    StandardSpacer()
                    preference.badge?.let { OutlineBadge(text = it) }
                }
                SmallSpacer()

                val showMoreLines = !preference.hasLongSummary || isShowMoreSummary

                AnimatedVisibility(visible = showMoreLines) {
                    Text(
                        modifier = Modifier.padding(end = 36.dp),
                        text = preference.summary ?: preference.title,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 20
                    )
                }
                AnimatedVisibility(visible = !showMoreLines) {
                    Text(
                        modifier = Modifier.padding(end = 36.dp),
                        text = preference.summary ?: preference.title,
                        style = MaterialTheme.typography.bodySmall,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4
                    )
                }
            }
        }

        ShortXSwitch(
            modifier = Modifier.padding(end = 16.dp),
            checked = preference.value,
            onCheckedChange = {
                preference.onCheckChanged(it)
            })
    }
}


private enum class ExpandableState { Expand, Collapsed }

@Composable
private fun ExpandableContainer(
    mainContent: @Composable () -> Unit,
    expandContent: @Composable () -> Unit,
) {
    var expandState by remember {
        mutableStateOf(ExpandableState.Collapsed)
    }
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                mainContent()
            }

            IconButton(
                modifier = Modifier
                    .padding(end = 16.dp),
                onClick = {
                    expandState =
                        if (expandState == ExpandableState.Expand) {
                            ExpandableState.Collapsed
                        } else {
                            ExpandableState.Expand
                        }
                }) {

                Crossfade(
                    targetState = expandState == ExpandableState.Expand,
                    label = "Crossfade"
                ) { isExpand ->
                    if (isExpand) {
                        RemixIcon(
                            remixName = Remix.Arrows.arrow_drop_up_line,
                        )
                    } else {
                        RemixIcon(
                            remixName = Remix.Arrows.arrow_drop_down_line,
                        )
                    }
                }
            }

        }

        AnimatedVisibility(visible = expandState == ExpandableState.Expand) {
            expandContent()
        }
    }

}