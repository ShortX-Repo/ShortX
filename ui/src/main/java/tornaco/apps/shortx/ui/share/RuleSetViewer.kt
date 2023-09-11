@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.share

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.theme.ShortXTheme

@Composable
fun RuleSetViewer(ruleSet: RuleSet, back: (Boolean) -> Unit) {
    var selectedIdList by rememberSaveable {
        mutableStateOf(listOf<String>())
    }
    val context = LocalContext.current
    val props = LocalShortXProps.current
    val savedMessage = LocalI18N.current["ui.toast.rule.saved"]
    val showToast = {
        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    ShortXMediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = ruleSet.title + " - " + LocalI18N.current["ui.common.share.viewer.title"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back(false)
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(
                                enabled = selectedIdList.isNotEmpty(),
                                onClick = {
                                    val token = props.firstOrNull()
                                    shortXManager.addOrUpdateRuleSet(
                                        RuleSet.newBuilder(ruleSet).build()
                                    )
                                    ruleSet.ruleList.rulesList.forEach {
                                        shortXManager.addRule(
                                            Rule.newBuilder(it)
                                                .setIsEnabled(false)
                                                .addAllExtras(mutableListOf<StringPair>().apply {
                                                    if (!token.isNullOrEmpty()) {
                                                        add((BuildProp.APP_PACKAGE_NAME to token).toStringPair())
                                                    }
                                                })
                                                .build()
                                        )
                                    }
                                    showToast()

                                    back(true)
                                }) {
                                RemixIcon(Remix.System.download_line)
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }) { paddingValues ->
                ClickOutsideClearFocusBox(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LazyColumn {
                        stickyHeader {
                            ShortXCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    modifier = Modifier.padding(16.dp),
                                    text = LocalI18N.current["ui.common.import.set.desctiption"],
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        stickyHeader {
                            Row {
                                ListItem(
                                    modifier = Modifier,
                                    title = LocalI18N.current["ui.common.selected.of.total", mapOf(
                                        "total" to ruleSet.ruleList.rulesList.size.toString(),
                                        "selected" to selectedIdList.size.toString(),
                                    )],
                                    action1 = {
                                        TextButton(onClick = {
                                            selectedIdList =
                                                if (selectedIdList.isEmpty()) {
                                                    ruleSet.ruleList.rulesList.map { it.id }
                                                } else {
                                                    emptyList()
                                                }
                                        }) {
                                            Text(text = LocalI18N.current["ui.common.select.all"])
                                        }
                                    }
                                )
                            }
                        }

                        items(ruleSet.ruleList.rulesList) { rule ->
                            ListItem(title = rule.title,
                                action1 = {
                                    Checkbox(
                                        checked = selectedIdList.contains(rule.id),
                                        onCheckedChange = { checked ->
                                            selectedIdList = selectedIdList.toMutableList().apply {
                                                checked.select(
                                                    t = { add(rule.id) },
                                                    f = { remove(rule.id) }
                                                )
                                            }
                                        })
                                },
                                icon = {
                                    ColoredIcon(
                                        modifier = Modifier,
                                        size = 32.dp,
                                        padding = 0.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = ActionColorBgAlpha)
                                    ) {
                                        RemixIcon(
                                            modifier = Modifier.size(16.dp),
                                            remixName = Remix.Weather.meteor_line,
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                },
                                onClick = {
                                    ViewerActivity.start(
                                        context,
                                        SharedData(SharedData.TYPE_RULE, rule.toJson())
                                    )
                                })
                        }
                    }
                }
            }
        }
    }
}