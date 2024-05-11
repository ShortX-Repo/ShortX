package tornaco.apps.shortx.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
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
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer

@Composable
fun PreferenceUi(preferences: List<Preference>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        preferences.forEach {
            PreferenceUi(preference = it)
            MediumSpacer()
        }
    }
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
            Divider()
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
    CategoryTitle(title = preference.title)
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
            .clip(ShortXCardRoundedCornerShape)
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
                .heightIn(min = 60.dp)
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
            .clip(ShortXCardRoundedCornerShape)
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
                .heightIn(min = 60.dp)
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
        modifier = Modifier
            .clip(ShortXCardRoundedCornerShape),
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