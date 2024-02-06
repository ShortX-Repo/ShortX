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

@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview(showSystemUi = false)
private fun OutlineBadgePreview() {
    Column {
        OutlineBadge(text = "Pro")
        StandardSpacer()
        OutlineBadge(text = "Preview")
        StandardSpacer()
        OutlineBadge(text = "Beta")
        StandardSpacer()
        OutlineBadge(text = "Alpha")
    }
}

@Composable
fun OutlineBadge(modifier: Modifier = Modifier, text: String) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = ShortXCardRoundedCornerShape
            )
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = text,
            fontSize = 10.sp,
            fontFamily = fontFamilyProductSansRegular(),
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
fun OutlineIconBadge(icon: @Composable () -> Unit, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = ShortXCardRoundedCornerShape
            )
            .padding(1.dp)
            .clip(ShortXCardRoundedCornerShape)
            .clickableWithRipple {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TinySpacer()
            Box(
                modifier = Modifier
                    .size(18.dp)
            ) {
                icon()
            }
            TinySpacer()
            Text(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .padding(vertical = 4.dp),
                text = text,
                fontSize = 10.sp,
                fontFamily = fontFamilyProductSansRegular()
            )
        }
    }
}

@Composable
fun ClickableBadge(text: String, onClick: () -> Unit) {
    TextButton(contentPadding = PaddingValues(4.dp), onClick = { onClick() }) {
        MD3Badge(
            text = text,
            containerColor = MaterialTheme.colorScheme.errorContainer,
            textSize = 12.sp,
            padding = 6.dp
        )
    }
}

@Composable
fun MD3Badge(text: String, fontFamily: FontFamily? = fontFamilyProductSansBold()) {
    MD3Badge(
        text = text,
        fontFamily = fontFamily,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        textSize = 12.sp
    )
}

@Composable
fun MD3Badge(
    text: String,
    fontFamily: FontFamily? = null,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    textSize: TextUnit = 12.sp,
    padding: Dp = 3.dp
) {
    androidx.compose.material3.Badge(
        containerColor = containerColor
    ) {
        Text(
            modifier = Modifier.padding(padding),
            text = text,
            fontFamily = fontFamily,
            fontSize = textSize
        )
    }
}

@Composable
fun MD3Badge(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    padding: Dp = 3.dp
) {
    androidx.compose.material3.Badge(
        modifier = modifier,
        containerColor = containerColor
    ) {
        Box(modifier = Modifier.padding(padding)) {
            text()
        }
    }
}