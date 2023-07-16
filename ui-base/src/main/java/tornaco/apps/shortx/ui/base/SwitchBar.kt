/*
 * (C) Copyright 2022 Thanox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.I18N

@Composable
fun SwitchBar(modifier: Modifier = Modifier, isChecked: Boolean, onCheckChange: (Boolean) -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clip(
                RoundedCornerShape(32.dp)
            )
            .clickableWithRipple {
                onCheckChange(!isChecked)
            }
            .background(color = ColorDefaults.backgroundSurfaceColor(4.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterStart),
            text = if (isChecked) {
                LocalI18N.current["ui.common.on"]
            } else {
                LocalI18N.current["ui.common.off"]
            },
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.W500,
                fontSize = 18.sp
            )
        )
        Switch(
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.CenterEnd),
            checked = isChecked,
            onCheckedChange = {
                onCheckChange(it)
            })
    }
}


@Preview
@Composable
private fun SwitchBarPreview() {
    CompositionLocalProvider(LocalI18N provides object : I18N {
        override fun get(key: String, args: Map<String, String>): String {
            return key.substringAfterLast(".")
        }
    }) {
        Column {
            SwitchBar(isChecked = true, onCheckChange = {})
            StandardSpacer()
            SwitchBar(isChecked = false, onCheckChange = {})
        }
    }
}