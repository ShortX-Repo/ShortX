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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShortXSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
    uncheckedTrackColor: Color = LocalShortXColorSchema.current.uncheckedTrack,
    uncheckedThumbColor: Color = LocalShortXColorSchema.current.uncheckedThumb,
) {
    CustomSwitch(
        modifier = modifier,
        switchOn = checked,
        onCheckedChange = onCheckedChange,
        uncheckedTrackColor = uncheckedTrackColor,
        uncheckedThumbColor = uncheckedThumbColor,
    )
}

@Composable
private fun CustomSwitch(
    modifier: Modifier = Modifier,
    switchOn: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    uncheckedTrackColor: Color,
    uncheckedThumbColor: Color,
    width: Dp = 36.dp,
    height: Dp = 23.dp,
    thumbSize: Dp = 14.dp,
) {
    // this is to disable the ripple effect
    val interactionSource = remember {
        MutableInteractionSource()
    }
    // for moving the thumb
    val alignment by animateAlignmentAsState(if (switchOn) 1f else -1f)
    // outer rectangle with border
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .background(
                color = if (switchOn) LocalShortXColorSchema.current.checkedTrack else uncheckedTrackColor,
                shape = CircleShape
            )
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                onCheckedChange(!switchOn)
            },
        contentAlignment = Alignment.Center
    ) {
        // this is to add padding at the each horizontal side
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            contentAlignment = alignment
        ) {
            // thumb with icon
            if (switchOn) {
                Box(
                    modifier = Modifier
                        .size(size = thumbSize)
                        .background(
                            color = LocalShortXColorSchema.current.checkedThumb,
                            shape = CircleShape
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(size = thumbSize)
                        .background(
                            color = uncheckedThumbColor,
                            shape = CircleShape
                        )
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                uncheckedTrackColor,
                                shape = CircleShape
                            )
                    )
                }
            }


        }
    }
}

@Composable
private fun animateAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment> {
    val bias by animateFloatAsState(targetBiasValue, label = "animateAlignmentAsState")
    return remember {
        derivedStateOf { BiasAlignment(horizontalBias = bias, verticalBias = 0f) }
    }
}

@Preview
@Composable
fun ShortXSwitchPreview() {
    ShortXSwitch(checked = true, onCheckedChange = {})
}