package tornaco.apps.shortx.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
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
    }
}

@Composable
private fun TextPreferenceUi(preference: Preference.TextPreference) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .clickable {

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