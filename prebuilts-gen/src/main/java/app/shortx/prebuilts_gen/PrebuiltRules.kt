package app.shortx.prebuilts_gen

import tornaco.apps.shortx.core.proto.action.ShowDanmu
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.fact.NotificationUpdated
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.pack_
import java.util.concurrent.atomic.AtomicInteger


object PrebuiltRules {
    private val idGen = AtomicInteger(0)

    private fun nextId() = "Prebuilt-${idGen.getAndIncrement()}"

    val all = mutableListOf<Rule>(
        Rule.newBuilder()
            .setId(nextId())
            .setCreateTime(System.currentTimeMillis() + idGen.get() * 1000)
            .setTitle("Danmu notification")
            .addAllFacts(
                listOf(
                    NotificationUpdated.newBuilder().build().pack_()
                )
            )
            .addAllConditions(listOf(True.newBuilder().build()).map { it.pack_() })
            .addAllActions(
                listOf(
                    ShowDanmu.newBuilder()
                        .setIcon("app://{pkgName}")
                        .setText("{title}\n{contentText}").build()
                        .pack_()
                )
            )
            .build()
    )
}