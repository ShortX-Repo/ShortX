package tornaco.apps.shortx.services.rule.plugin

import shortx.plugin.ParamsData
import shortx.plugin.wrapper.ParamsDataWrapper

fun ParamsDataWrapper.toParamsData(): ParamsData {
    return ParamsData.newBuilder()
        .putAllStringData(this.stringDataMap)
        .putAllIntData(this.intDataMap)
        .putAllLongData(this.longDataMap)
        .putAllFloatData(this.floatDataMap)
        .putAllBoolData(this.boolDataMap)
        .putAllStringListData(this.stringListDataMap.mapValues {
            shortx.plugin.StringList.newBuilder().addAllString(it.value.stringList).build()
        })
        // TODO Add More.
        .build()
}