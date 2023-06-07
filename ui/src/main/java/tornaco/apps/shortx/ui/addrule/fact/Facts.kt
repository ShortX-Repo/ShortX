@file:OptIn(ExperimentalAnimationApi::class)

package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.*
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.fact.mapper.colorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelForFact
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.contextData
import tornaco.apps.shortx.ui.addrule.fact.selector.FactSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberFactSelectorDialogState
import tornaco.apps.shortx.ui.base.*


@Composable
fun FactSelectors(
    vm: AddRuleViewModel,
    state: AddRuleState,
    readOnly: Boolean = false,
) {
    val factSelectorState = rememberFactSelectorDialogState<String>(onSelected = { fact, _ ->
        vm.setFact(fact)
    })
    FactSelectorDialog(state = factSelectorState)

    val editFactHandler = factSelectHandler(setFact = {
        vm.setFact(it)
    })

    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle(LocalI18N.current["ui.fact.title"])

        Column {
            state.fact?.let { fact ->
                FactItem(
                    fact = fact,
                    readOnly = readOnly,
                    onClick = {
                        editFactHandler(FactSelectSource.FromFact(it))
                    }
                )
            }

            TinySpacer()
            if (!readOnly) {
                FilledTonalButton(onClick = {
                    factSelectorState.show()
                }) {
                    RemixIcon(
                        modifier = Modifier.size(16.dp),
                        remixName = Remix.System.add_line
                    )
                    TinySpacer()
                    Text(text = LocalI18N.current["ui.fact.select"])
                }
            }
        }
    }
}


@Composable
private fun FactItem(
    fact: Fact,
    readOnly: Boolean,
    onClick: (Fact) -> Unit
) {
    val contextDataInfoDialogState = rememberContextDataInfoDialogState()
    ContextDataInfoDialog(state = contextDataInfoDialogState)
    val i18N = LocalI18N.current

    ListItem(
        modifier = Modifier,
        titleLines = labelForFact(fact = fact),
        onClick = {
            onClick(fact)
        },
        icon = {
            ColoredIcon(
                size = 36.dp, color = colorForFact(fact::class).copy(
                    ActionColorBgAlpha
                )
            ) {
                RemixIcon(
                    modifier = Modifier.size(16.dp),
                    remixName = imageVectorForFact(fact::class),
                    tint = colorForFact(fact::class)
                )
            }
        },
        action1 = {
            if (fact.contextData.isNotEmpty()) {
                IconButton(onClick = {
                    contextDataInfoDialogState.show(
                        labelAndDescriptionForFactSelector(i18N, fact::class).first,
                        fact.contextData
                    )
                }) {
                    ContextDataIcon()
                }
                VerticalDivider(modifier = Modifier.height(24.dp))
            }

            if (!readOnly) {
                IconButton(onClick = {
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            }
        }
    )
}