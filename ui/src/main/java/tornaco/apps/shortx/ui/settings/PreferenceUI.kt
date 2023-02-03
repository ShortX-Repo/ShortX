package tornaco.apps.shortx.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
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
            CategoryUi(preference = preference)
        }
        is Preference.ExpandablePreference -> {
            ExpandablePreferenceUi(preference = preference)
        }
    }
}

@Composable
private fun CategoryUi(preference: Preference.Category) {
    Box(
        modifier = Modifier
            .padding(top = 24.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = preference.title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )
        )
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
                    icon = preference.icon
                )
            )
        }, expandContent = {
            preference.preferences.forEach {
                PreferenceUi(it)
            }
        })
}

@Composable
private fun TextPreferenceUi(preference: Preference.TextPreference) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .apply {
                if (preference.onClick != null) {
                    clickable {
                        preference.onClick.invoke()
                    }
                }
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 60.dp)
                .weight(1f, fill = false)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            preference.icon?.let {
                Icon(imageVector = it, contentDescription = preference.title)
            }
            StandardSpacer()
            Column(modifier = Modifier) {
                Text(text = preference.title, style = MaterialTheme.typography.titleMedium)
                preference.summary?.let {
                    SmallSpacer()
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}


@Composable
private fun SwitchPreferenceUi(preference: Preference.SwitchPreference) {
    var checkState by remember(preference) {
        mutableStateOf(preference.value)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .clickable {
                checkState = !checkState
                preference.onCheckChanged(checkState)
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
            preference.icon?.let {
                Icon(imageVector = it, contentDescription = preference.title)
            }
            StandardSpacer()
            Column(modifier = Modifier) {
                Text(text = preference.title, style = MaterialTheme.typography.titleMedium)
                preference.summary?.let {
                    SmallSpacer()
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Switch(
            modifier = Modifier.padding(end = 16.dp),
            checked = checkState,
            onCheckedChange = {
                checkState = it
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
            .clip(ShortXCardRoundedCornerShape)
            .clickable {
                expandState =
                    if (expandState == ExpandableState.Expand) {
                        ExpandableState.Collapsed
                    } else {
                        ExpandableState.Expand
                    }
            },
        verticalArrangement = Arrangement.Center
    ) {
        mainContent()

        AnimatedVisibility(visible = expandState == ExpandableState.Expand) {
            expandContent()
        }
    }

}