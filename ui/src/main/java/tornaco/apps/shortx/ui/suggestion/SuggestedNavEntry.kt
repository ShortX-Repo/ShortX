@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.suggestion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.RegexTestDialog
import tornaco.apps.shortx.ui.addrule.rememberRegexTestState
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.SplitLines
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.gvar.GlobalVarListActivity
import tornaco.apps.shortx.ui.pkgset.PkgSetListActivity
import tornaco.apps.shortx.ui.record.ActionEvaRecordActivity
import tornaco.apps.shortx.ui.record.ConditionEvaRecordActivity
import tornaco.apps.shortx.ui.record.FactPublishRecordActivity
import tornaco.apps.shortx.ui.settings.danmu.DanmuUISettingsActivity
import tornaco.apps.shortx.ui.settings.danmu.DanmuUISettingsScreenDialog

enum class SuggestedNav(val labelKey: String) {
    GlobalVar("ui.global.var.title"),
    PkgSet("ui.pkg.set.title"),
    DanmuSettings("ui.settings.danmu"),
    ShowPointerLocation("ui.common.show.pointer.location"),
    FactRecord("ui.settings.fact.record"),
    ConditionRecord("ui.settings.condition.record"),
    ActionRecord("ui.settings.action.record"),
    RegexTest("ui.tools.regex.test.title"),
}

@Composable
fun SuggestedNavEntry() {
    Column {
        SplitLines(modifier = Modifier.padding(vertical = 30.dp))

        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
            RemixIcon(
                modifier = Modifier.size(18.dp),
                remixName = Remix.Logos.bard_fill,
                tint = MaterialTheme.colorScheme.primary
            )
            SmallSpacer()
            SmallTitle(
                modifier = Modifier.padding(vertical = 12.dp),
                text = LocalI18N.current["ui.suggest.nav.entry.title"],
            )
        }
        StandardSpacer()

        FlowRow {
            val context = LocalContext.current

            val danmuSettingsScreenState = rememberCommonDialogState()
            DanmuUISettingsScreenDialog(state = danmuSettingsScreenState)

            val regexTestDialog = rememberRegexTestState()
            RegexTestDialog(state = regexTestDialog)

            SuggestedNav.values().forEach {
                SuggestionChip(
                    onClick = {
                        when (it) {
                            SuggestedNav.GlobalVar -> GlobalVarListActivity.start(context)
                            SuggestedNav.PkgSet -> PkgSetListActivity.start(context)
                            SuggestedNav.DanmuSettings -> DanmuUISettingsActivity.start(context)
                            SuggestedNav.ShowPointerLocation -> {
                                shortXManager.setPointerLocationEnabled(!shortXManager.isPointerLocationEnabled())
                            }

                            SuggestedNav.FactRecord -> {
                                FactPublishRecordActivity.start(context)
                            }

                            SuggestedNav.ConditionRecord -> {
                                ConditionEvaRecordActivity.start(context)
                            }

                            SuggestedNav.ActionRecord -> {
                                ActionEvaRecordActivity.start(context)
                            }

                            SuggestedNav.RegexTest -> {
                                regexTestDialog.show()
                            }
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