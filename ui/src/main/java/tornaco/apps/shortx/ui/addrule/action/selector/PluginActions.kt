package tornaco.apps.shortx.ui.addrule.action.selector


import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import shortx.plugin.Intents
import shortx.plugin.ParamsData
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.plugin.toParamsData
import tornaco.apps.shortx.core.plugin.toParamsDataWrapper
import tornaco.apps.shortx.core.proto.plugins.ShortXPluginAction
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.toIntent
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.ActionCategory
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.SelectorListItem
import tornaco.apps.shortx.ui.addrule.base.rememberCategoryState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder

@Composable
fun pluginActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            handlePickerResult(result, addAction)
        }
    )

    return { source ->
        when (source.clazz) {
            Action.PluginAction::class -> {
                val pa =
                    requireNotNull(source.getActionAs<Action.PluginAction>()) { "Not PluginAction" }
                pa.action.toIntent(pa.params.toParamsData())?.let {
                    pickLauncher.launch(it)
                }
                true
            }

            else -> {
                false
            }
        }
    }
}

@Composable
fun PluginActions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    selectedFilter: ActionCategory,
    pluginActions: List<ShortXPluginAction>,
    addAction: (Action) -> Unit
) {
    composeLogger.d("pluginActions- ${pluginActions.size}")
    val i18N = LocalI18N.current
    val actionItems = pluginActions.map {
        SelectorItem(
            type = it::class,
            data = it,
            labelAndDescription = it.actionLabel to null,
            icon = Remix.Logos.mini_program_line,
            isForSubscribedOnly = remember(subscribeState) {
                !subscribeState.isSubscribed
            },
            iconColor = colorForAction(Action.PluginAction::class)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    val categoryState = rememberCategoryState(categoryTitle = i18N[ActionCategory.Plugin.i18NKey])
    if (actionItems.isNotEmpty() && selectedFilter == ActionCategory.All) {
        ActionCategory(i18N[ActionCategory.Plugin.i18NKey], actionItems.size, categoryState)
    }

    val isVisible =
        (categoryState.isExpand && selectedFilter == ActionCategory.All) || selectedFilter == ActionCategory.Plugin

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    AnimatedVisibility(visible = isVisible) {
        SelectorList(
            modifier = Modifier,
            actionItems = actionItems,
            selectorListItem = { selectorItem ->
                val pickLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { result ->
                        handlePickerResult(result, addAction)
                    }
                )

                SelectorListItem(
                    modifier = Modifier,
                    icon = selectorItem.icon,
                    iconColor = selectorItem.iconColor,
                    title = selectorItem.labelAndDescription.first,
                    text1 = selectorItem.labelAndDescription.second,
                    isForSubscribedOnly = selectorItem.isForSubscribedOnly
                ) {
                    if (selectorItem.isForSubscribedOnly) {
                        subscribeDialogState.show()
                    } else {
                        composeLogger.d("selectorItem.data: ${selectorItem.data}")
                        val pa = requireNotNull((selectorItem.data as? ShortXPluginAction)) {
                            "Not ShortXPluginAction"
                        }
                        val intent = pa.toIntent()
                        composeLogger.d("intent: $intent")
                        intent?.let {
                            pickLauncher.launch(it)
                        }
                    }
                }
            }
        )
    }
}

private fun handlePickerResult(
    result: ActivityResult,
    addAction: (Action) -> Unit
) {
    composeLogger.d("result: $result")

    val paramsData = result.data?.getByteArrayExtra(Intents.EXTRA_ACTION_PICKER_RESULT)
        ?.let {
            ParamsData.parseFrom(it)
        }
    composeLogger.d("ParamsData: $paramsData")

    val request = result.data?.getByteArrayExtra(Intents.ACTION_INPUT_REQUEST)?.let {
        ShortXPluginAction.parseFrom(it)
    }
    composeLogger.d("request: $request")

    if (paramsData != null && request != null) {
        addAction(
            Action.PluginAction(
                action = request,
                params = paramsData.toParamsDataWrapper()
            )
        )
    }
}

fun ShortXPluginAction.toIntent(paramsData: ParamsData? = null): Intent? {
    return pickerIntent?.toIntent(
        contextData = ContextData.obtain(emptyMap()),
        localVars = LocalVars.obtain(),
        parameterInputs = emptyList(),
        globalVarsLazy = { emptyList() }
    )?.apply {
        // To Resolve error: Activity is launching as a new task, so cancelling activity result.
        removeFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(Intents.ACTION_INPUT_REQUEST, this@toIntent.toByteArray())
        paramsData?.let {
            putExtra(Intents.ACTION_INPUT_PARAMS, paramsData.toByteArray())
        }
    }
}