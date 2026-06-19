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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState as orbitCollectAsState
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
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.contextdata.ContextVarListActivity
import tornaco.apps.shortx.ui.function.FunctionListActivity
import tornaco.apps.shortx.ui.gvar.GlobalVarListActivity
import tornaco.apps.shortx.ui.pkgset.PkgSetListActivity
import tornaco.apps.shortx.ui.record.EvaRecordActivity
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.settings.danmu.DanmuUISettingsActivity
import tornaco.apps.shortx.ui.settings.danmu.DanmuUISettingsScreenDialog
import tornaco.apps.shortx.core.i18n.I18N_R

enum class SuggestedNav(val labelKey: String) {
    GlobalVar(I18N_R.UI_GLOBAL_VAR_TITLE),
    SystemEnv(I18N_R.UI_SYSTEM_ENV),
    Functions(I18N_R.UI_ACTION_FUNCTIONS),
    PkgSet(I18N_R.UI_PKG_SET_TITLE),
    DanmuSettings(I18N_R.UI_SETTINGS_DANMU),
    ShowPointerLocation(I18N_R.UI_COMMON_SHOW_POINTER_LOCATION),
    FactRecord(I18N_R.UI_SETTINGS_FACT_RECORD),
    ConditionRecord(I18N_R.UI_SETTINGS_CONDITION_RECORD),
    ActionRecord(I18N_R.UI_SETTINGS_ACTION_RECORD),
    RegexTest(I18N_R.UI_TOOLS_REGEX_TEST_TITLE),
}

@Composable
fun SuggestedNavEntry() {
    val subscribeState by LVLStateHolder.orbitCollectAsState()
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
                text = LocalI18N.current[I18N_R.UI_SUGGEST_NAV_ENTRY_TITLE],
            )
        }
        StandardSpacer()

        FlowRow {
            val context = LocalContext.current

            val danmuSettingsScreenState = rememberCommonDialogState()
            DanmuUISettingsScreenDialog(state = danmuSettingsScreenState)

            val subscribeDialogState = rememberShortXBottomSheetState()
            FeatureAccessDialog(state = subscribeDialogState)

            val regexTestDialog = rememberRegexTestState()
            RegexTestDialog(state = regexTestDialog)

            SuggestedNav.values().forEach {
                SuggestionChip(
                    onClick = {
                        when (it) {
                            SuggestedNav.GlobalVar -> GlobalVarListActivity.start(context)
                            SuggestedNav.SystemEnv -> ContextVarListActivity.start(context)
                            SuggestedNav.Functions -> {
                                if (subscribeState.isSubscribed) {
                                    FunctionListActivity.start(context)
                                } else {
                                    subscribeDialogState.show()
                                }
                            }
                            SuggestedNav.PkgSet -> PkgSetListActivity.start(context)
                            SuggestedNav.DanmuSettings -> DanmuUISettingsActivity.start(context)
                            SuggestedNav.ShowPointerLocation -> {
                                shortXManager.setPointerLocationEnabled(!shortXManager.isPointerLocationEnabled())
                            }

                            SuggestedNav.FactRecord -> {
                                EvaRecordActivity.start(context)
                            }

                            SuggestedNav.ConditionRecord -> {
                                EvaRecordActivity.start(context)
                            }

                            SuggestedNav.ActionRecord -> {
                                EvaRecordActivity.start(context)
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
        override fun get(key: String, args: Map<String, String>, fallback: String?): String {
            return key.substringAfterLast(".")
        }
    }) {
        SuggestedNavEntry()
    }
}