package tornaco.apps.shortx.ui.main.da

import tornaco.apps.shortx.ui.main.DASortBy
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import java.text.Collator
import java.util.Locale

fun sortDirectActions(sortBy: DASortBy): (List<DirectActionUM>) -> List<DirectActionUM> {
    return { das ->
        when (sortBy) {
            DASortBy.Name -> {
                das.sortedWith { o1, o2 ->
                    Collator.getInstance(Locale.CHINESE).compare(o1.title, o2.title)
                }
            }
            DASortBy.NameReverse -> {
                das.sortedWith { o1, o2 ->
                    Collator.getInstance(Locale.CHINESE).compare(o1.title, o2.title)
                }.reversed()
            }
            DASortBy.CreateTime -> {
                das.sortedByDescending { it.directAction.createTime }
            }
            DASortBy.CreateTimeReverse -> {
                das.sortedBy { it.directAction.createTime }
            }
            DASortBy.LastUpdateTime -> {
                das.sortedByDescending { it.directAction.lastUpdateTime }
            }
            DASortBy.LastUpdateTimeReverse -> {
                das.sortedBy { it.directAction.lastUpdateTime }
            }
        }
    }
}