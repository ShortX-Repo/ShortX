@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix

@Composable
fun ShortXCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.animateContentSize(),
        backgroundColor = LocalShortXColorSchema.current.cardBackground,
        shape = ShortXCardRoundedCornerShape,
        elevation = 0.dp,
        onClick = onClick,
        content = content
    )
}

@Composable
fun WarningCard(title: String, warnings: List<String>, onClick: () -> Unit = {}) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row {
                RemixIcon(
                    modifier = Modifier.offset(y = 2.dp),
                    remixName = Remix.System.error_warning_fill
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