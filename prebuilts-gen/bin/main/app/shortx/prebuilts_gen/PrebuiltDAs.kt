package app.shortx.prebuilts_gen

import tornaco.apps.shortx.core.proto.action.NoAction
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.rule.pack_
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger


object PrebuiltDAs {
    private val idGen = AtomicInteger(0)

    private fun nextId() = "Prebuilt-${idGen.getAndIncrement()}"

    val all = listOf(
        DirectAction.newBuilder().setTitle("NoAction")
            .setId(nextId())
            .setCreateTime(System.currentTimeMillis())
            .setLastUpdateTime(System.currentTimeMillis())
            .addActions(NoAction.newBuilder().setId(UUID.randomUUID().toString()).build().pack_())
            .build(),
    )
}