package tornaco.apps.shortx.ui.changelog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXBottomSheet
import tornaco.apps.shortx.ui.base.ShortXBottomSheetState
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.TinySpacer

@Composable
fun ChangeLogBottomSheet(state: ShortXBottomSheetState) {
    val context = LocalContext.current
    val changeLog = remember { ChangeLogRepository.load(context) }

    ShortXBottomSheet(state = state) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.82f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RemixIcon(remixName = Remix.Document.article_fill)
                MediumSpacer()
                Text(
                    text = LocalI18N.current["ui.changelog.title"],
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            SmallSpacer()
            Text(
                text = LocalI18N.current[
                    "ui.changelog.current.version",
                    mapOf(
                        "version" to BuildPropInternal.APP_VERSION_NAME,
                        "code" to BuildPropInternal.APP_VERSION_CODE.toString()
                    )
                ],
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LargeSpacer()

            changeLog.items.sortedByDescending { it.versionCode }.forEachIndexed { index, item ->
                ChangeLogItemCard(item)
                if (index != changeLog.items.lastIndex) {
                    MediumSpacer()
                }
            }

            Spacer(modifier = Modifier.padding(bottom = 24.dp))
        }
    }
}

@Composable
private fun ChangeLogItemCard(item: ChangeLogItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.versionName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                if (item.date.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(item.date) })
                }
            }
            TinySpacer()
            Text(
                text = LocalI18N.current[
                    "ui.changelog.version.code",
                    mapOf("code" to item.versionCode.toString())
                ],
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            item.sections.forEach { section ->
                MediumSpacer()
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                if (section.body.isNotBlank()) {
                    TinySpacer()
                    Text(
                        text = section.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                section.bullets.forEach { bullet ->
                    TinySpacer()
                    Row {
                        Text(
                            text = "-",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        SmallSpacer()
                        Text(
                            text = bullet,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
