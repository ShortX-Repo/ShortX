@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.suggestion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.gvar.GlobalVarListScreenDialog
import tornaco.apps.shortx.ui.pkgset.PkgSetListScreenDialog
import tornaco.apps.shortx.ui.settings.danmu.DanmuUISettingsScreenDialog
import tornaco.apps.shortx.ui.theme.NoteTextColor

enum class SuggestedNav(val labelKey: String) {
    GlobalVar("ui.global.var.title"),
    PkgSet("ui.pkg.set.title"),
    DanmuSettings("ui.settings.danmu")
}

@Composable
fun SuggestedNavEntry() {
    Column {
        SectionSpacer()

        Canvas(modifier = Modifier.fillMaxWidth(), onDraw = {
            drawLine(
                color = NoteTextColor,
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        })

        StandardSpacer()

        Row(verticalAlignment = Alignment.CenterVertically) {
            RemixIcon(
                modifier = Modifier.size(18.dp),
                remixName = Remix.Logos.bard_fill,
                tint = MaterialTheme.colorScheme.primary
            )
            SmallSpacer()
            SmallTitle(
                text = LocalI18N.current["ui.suggest.nav.entry.title"],
            )
        }

        FlowRow {
            val globalVarScreenState = rememberCommonDialogState()
            GlobalVarListScreenDialog(state = globalVarScreenState)

            val pkgSetScreenState = rememberCommonDialogState()
            PkgSetListScreenDialog(state = pkgSetScreenState)

            val danmuSettingsScreenState = rememberCommonDialogState()
            DanmuUISettingsScreenDialog(state = danmuSettingsScreenState)

            SuggestedNav.values().forEach {
                SuggestionChip(
                    onClick = {
                        when (it) {
                            SuggestedNav.GlobalVar -> globalVarScreenState.show()
                            SuggestedNav.PkgSet -> pkgSetScreenState.show()
                            SuggestedNav.DanmuSettings -> danmuSettingsScreenState.show()
                        }
                    }, label = {
                        Text(text = LocalI18N.current[it.labelKey])
                    })

                LargeSpacer()
            }
        }

        SectionSpacer()
    }
}


@Composable
@Preview
private fun SuggestedNavEntryPreview() {
    CompositionLocalProvider(LocalI18N provides object : I18N {
        override fun get(key: String, args: Map<String, String>): String {
            return key.substringAfterLast(".")
        }
    }) {
        SuggestedNavEntry()
    }
}