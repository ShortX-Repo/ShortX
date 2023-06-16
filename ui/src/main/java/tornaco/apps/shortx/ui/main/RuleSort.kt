package tornaco.apps.shortx.ui.main

import tornaco.apps.shortx.core.proto.rule.Rule
import java.text.Collator
import java.util.Locale

fun sort(ruleSortBy: RuleSortBy): (List<Rule>) -> List<Rule> {
    return { rules ->
        when (ruleSortBy) {
            RuleSortBy.Name -> {
                rules.sortedWith { o1, o2 ->
                    Collator.getInstance(Locale.CHINESE).compare(o1.title, o2.title)
                }
            }

            RuleSortBy.NameReverse -> {
                rules.sortedWith { o1, o2 ->
                    Collator.getInstance(Locale.CHINESE).compare(o1.title, o2.title)
                }.reversed()
            }

            RuleSortBy.LastUpdateTime -> {
                rules.sortedBy { it.lastUpdateTime }
            }

            RuleSortBy.LastUpdateTimeReverse -> {
                rules.sortedBy { it.lastUpdateTime }.reversed()
            }

            RuleSortBy.CreateTime -> {
                rules.sortedByDescending { it.createTime }
            }

            RuleSortBy.CreateTimeReverse -> {
                rules.sortedByDescending { it.createTime }.reversed()
            }

            RuleSortBy.EnableState -> {
                rules.sortedBy { it.isEnabled }
            }

            RuleSortBy.EnableStateReverse -> {
                rules.sortedBy { it.isEnabled }.reversed()
            }
        }
    }
}