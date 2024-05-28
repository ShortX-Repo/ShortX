package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.func.FuncParameter
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.rule.asDefaultInput
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class FromDASelectorState(
    val daId: String = "",
    val title: String? = null,
    val description: String? = null,
    val parameterInputs: List<FuncParameterInput> = emptyList(),
    val parameters: List<FuncParameter> = emptyList()
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class FromDADialogSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<FromDASelectorState>(
        context, { FromDASelectorState() }
    ) {

    fun initFrom(settings: Action.FromDA?) {
        logger.d("initFrom: $settings")
        settings?.let {
            val da = shortXManager.getDirectActionById(settings.daId)
            da?.let {
                updateState {
                    copy(
                        daId = da.id,
                        title = da.title,
                        description = da.description,
                        parameters = da.parametersList,
                        parameterInputs = settings.parameterInputs.toMutableList().apply {
                            addAll(da.parametersList.filterNot { parameter ->
                                val name = parameter.name
                                any { input -> input.name == name }
                            }.map { it.asDefaultInput() })
                        }
                    )
                }
            }
        }
    }

    fun selectDA(daId: String) {
        logger.d("selectDA: $daId")
        val da = shortXManager.getDirectActionById(daId)
        da?.let {
            updateState {
                copy(
                    daId = da.id,
                    title = da.title,
                    description = da.description,
                    parameters = da.parametersList,
                    parameterInputs = da.parametersList.map { it.asDefaultInput() }
                )
            }
        }
    }

    fun updateParamInput(update: FuncParameterInput) {
        updateState {
            copy(
                parameterInputs = parameterInputs.toMutableList().apply {
                    val index = indexOfFirst { it.name == update.name }
                    if (index >= 0) {
                        set(index, update)
                    }
                }
            )
        }
    }
}