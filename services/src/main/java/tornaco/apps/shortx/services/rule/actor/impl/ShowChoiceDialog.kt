package tornaco.apps.shortx.services.rule.actor.impl

import android.app.AlertDialog
import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ShowChoiceDialog : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowChoiceDialog::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ShowChoiceDialog::class.java
        evaluateShowDialogAction(
            context = androidUiContext,
            shortXContext = shortXContext,
            d = d,
            contextData = contextData,
            localVars = localVars
        )
    }

    private suspend fun evaluateShowDialogAction(
        context: Context,
        shortXContext: ShortXContext,
        d: tornaco.apps.shortx.core.proto.action.ShowChoiceDialog,
        contextData: ContextData,
        localVars: LocalVars
    ) = withContext(Dispatchers.Main) {
        logger.d("ShowChoiceDialog, showing: ${d.choicesList}")
        require(d.choicesList.isNotEmpty()) { "No choices!" }
        val cv = RuleContextAndVars(localVars, contextData, shortXContext)
        val checkedIndexState = mutableListOf<Int>()
        val result = ShowTextFieldDialog.ShowDialog(context).invoke { dialogContext ->
            AlertDialog.Builder(dialogContext)
                .setTitle(
                    d.title.orEmpty().compileContextAndVars(cv)
                )
                .apply {
                    if (d.isMultipleChoice) {
                        setMultiChoiceItems(
                            d.choicesList.map {
                                it.label.orEmpty().compileContextAndVars(cv)
                            }.toTypedArray(),
                            d.choicesList.mapIndexed { index, it ->
                                it.isSelectedByDefault.apply {
                                    if (this) checkedIndexState.add(index)
                                }
                            }.toBooleanArray(),
                        ) { _, which, isChecked ->
                            if (isChecked) {
                                checkedIndexState.add(which)
                            } else {
                                checkedIndexState.remove(
                                    which
                                )
                            }
                        }
                    } else {
                        setSingleChoiceItems(
                            d.choicesList.map {
                                it.label.orEmpty().compileContextAndVars(cv)
                            }.toTypedArray(),
                            (d.choicesList.indexOfFirst { it.isSelectedByDefault }
                                .takeIf { it >= 0 }
                                ?: 0).apply {
                                checkedIndexState.add(this)
                            }
                        ) { _, which ->
                            checkedIndexState.clear()
                            checkedIndexState.add(which)
                        }
                    }
                }
                .setCancelable(d.cancelable)
        }

        logger.d("ShowChoiceDialog, result is: $result")

        withContext(Dispatchers.Default) {
            logger.d("handle result: $result $checkedIndexState")
            when (result) {
                DialogAction.Positive -> {
                    val selectedValues = d.choicesList.filterIndexed { index, _ ->
                        checkedIndexState.contains(index)
                    }.map { it.value }
                    logger.d("selectedValues: $selectedValues")

                    contextData.add(
                        d.customContextDataKey.forKey(ContextDataMapping.ShowChoiceDialog.choices.name)
                            .fallbackOnEmpty { ContextDataMapping.ShowChoiceDialog.choices.name },
                        selectedValues
                    )
                }

                else -> {
                    // Noop.
                }
            }
        }
    }
}