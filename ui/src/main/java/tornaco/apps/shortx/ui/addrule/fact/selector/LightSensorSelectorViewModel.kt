package tornaco.apps.shortx.ui.addrule.fact.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.fact.SensorValueTrend
import tornaco.apps.shortx.core.proto.fact.ValueConstraints
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.defaultNewFactId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject


data class LightSensorSelectorState(
    val id: String = defaultNewFactId(),
    val constraints: List<ValueConstraints> = emptyList(),
    val trend: SensorValueTrend = SensorValueTrend.SensorValueTrend_Any
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class LightSensorSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<LightSensorSelectorState>(
        context, { LightSensorSelectorState() }
    ) {

    fun initFrom(currentAction: Fact.LightSensor?) {
        currentAction?.let {
            updateState {
                copy(
                    id = currentAction.id,
                    constraints = currentAction.constraints,
                    trend = currentAction.trend
                )
            }
        }
    }

    fun removeConstraint(constraints: ValueConstraints) {
        updateState {
            copy(constraints = this.constraints.toMutableList().apply {
                remove(constraints)
            })
        }
    }

    fun addConstraint(constraints: ValueConstraints) {
        updateState {
            copy(constraints = this.constraints.toMutableList().apply {
                add(constraints)
            })
        }
    }

    fun setTrend(data: SensorValueTrend) {
        updateState {
            copy(trend = data)
        }
    }
}