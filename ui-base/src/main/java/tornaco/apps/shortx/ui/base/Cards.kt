@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.theme.WarningYellow

@Composable
fun ShortXCard(
    modifier: Modifier = Modifier,
    shape: Shape = ShortXCardRoundedCornerShape,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.animateContentSize(),
        backgroundColor = ColorDefaults.backgroundSurfaceColor(),
        shape = shape,
        elevation = 0.dp,
        onClick = onClick,
        content = content
    )
}


@Composable
fun WarningCard(
    modifier: Modifier = Modifier,
    shape: Shape = ShortXCardRoundedCornerShape,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.animateContentSize(),
        backgroundColor = WarningYellow,
        shape = shape,
        elevation = 0.dp,
        onClick = onClick,
        content = content
    )
}

@Composable
fun ErrorCard(
    modifier: Modifier,
    title: String,
    iconName: String = Remix.System.error_warning_fill,
    warnings: List<String>, onClick: () -> Unit = {}
) {
    androidx.compose.material3.Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row {
                val infiniteTransition = rememberInfiniteTransition()
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0.6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ), label = "scale"
                )

                RemixIcon(
                    modifier = Modifier.offset(y = 2.dp).scale(scale),
                    remixName = iconName
                )
                MediumSpacer()
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp
                        )
                    )
                    MediumSpacer()
                    Text(
                        text = warnings.joinToString(System.lineSeparator()),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun InfoCard(title: String?, infos: List<String>, onClick: () -> Unit = {}) {
    androidx.compose.material3.Card(
        modifier = Modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row {
                RemixIcon(
                    modifier = Modifier.offset(y = 2.dp),
                    remixName = Remix.System.information_fill
                )
                MediumSpacer()
                Column {
                    title?.let {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 14.sp
                            )
                        )
                        MediumSpacer()
                    }
                    Text(
                        text = infos.joinToString(System.lineSeparator()),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}