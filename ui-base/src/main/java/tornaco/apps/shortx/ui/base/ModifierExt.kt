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

@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

@Composable
fun Modifier.longPressZoom(
    scaleFactor: Float = 1.36f,
    animationDuration: Int = 400,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
): Modifier {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    return this
        .graphicsLayer(
            scaleX = scale.value,
            scaleY = scale.value
        )
        .combinedClickable(
            onClick = {
                onClick?.invoke() // 点击事件
            },
            onLongClick = {
                onLongClick?.invoke() // 点击事件
                coroutineScope.launch {
                    // 长按时放大
                    scale.animateTo(
                        scaleFactor,
                        animationSpec = tween(animationDuration)
                    )
                    // 模拟长按结束后缩小回去
                    scale.animateTo(
                        1f,
                        animationSpec = tween(animationDuration)
                    )
                }
            }
        )
}
