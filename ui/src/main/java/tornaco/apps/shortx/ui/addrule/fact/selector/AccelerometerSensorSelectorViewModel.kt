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


data class AccelerometerSensorSelectorState(
    val id: String = defaultNewFactId(),
    val constraintsX: List<ValueConstraints> = emptyList(),
    val trendX: SensorValueTrend = SensorValueTrend.SensorValueTrend_Any,

    val constraintsY: List<ValueConstraints> = emptyList(),
    val trendY: SensorValueTrend = SensorValueTrend.SensorValueTrend_Any,

    val constraintsZ: List<ValueConstraints> = emptyList(),
    val trendZ: SensorValueTrend = SensorValueTrend.SensorValueTrend_Any
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AccelerometerSensorSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<AccelerometerSensorSelectorState>(
        context, { AccelerometerSensorSelectorState() }
    ) {

    fun initFrom(currentAction: Fact.AccelerometerSensor?) {
        currentAction?.let {
            updateState {
                copy(
                    id = currentAction.id,
                    constraintsX = currentAction.constraintsX,
                    trendX = currentAction.trendX,
                    constraintsY = currentAction.constraintsY,
                    trendY = currentAction.trendY,
                    constraintsZ = currentAction.constraintsZ,
                    trendZ = currentAction.trendZ
                )
            }
        }
    }

    fun removeConstraintX(constraints: ValueConstraints) {
        updateState {
            copy(constraintsX = this.constraintsX.toMutableList().apply {
                remove(constraints)
            })
        }
    }

    fun addConstraintX(constraints: ValueConstraints) {
        updateState {
            copy(constraintsX = this.constraintsX.toMutableList().apply {
                add(constraints)
            })
        }
    }

    fun setTrendX(data: SensorValueTrend) {
        updateState {
            copy(trendX = data)
        }
    }

    fun removeConstraintY(constraints: ValueConstraints) {
        updateState {
            copy(constraintsY = this.constraintsY.toMutableList().apply {
                remove(constraints)
            })
        }
    }

    fun addConstraintY(constraints: ValueConstraints) {
        updateState {
            copy(constraintsY = this.constraintsY.toMutableList().apply {
                add(constraints)
            })
        }
    }

    fun setTrendY(data: SensorValueTrend) {
        updateState {
            copy(trendY = data)
        }
    }

    fun removeConstraintZ(constraints: ValueConstraints) {
        updateState {
            copy(constraintsZ = this.constraintsZ.toMutableList().apply {
                remove(constraints)
            })
        }
    }

    fun addConstraintZ(constraints: ValueConstraints) {
        updateState {
            copy(constraintsZ = this.constraintsZ.toMutableList().apply {
                add(constraints)
            })
        }
    }

    fun setTrendZ(data: SensorValueTrend) {
        updateState {
            copy(trendZ = data)
        }
    }
}