package app.shortx.prebuilts_gen

import tornaco.apps.shortx.core.proto.action.ExpandNotification
import tornaco.apps.shortx.core.proto.action.PieMenuItem
import tornaco.apps.shortx.core.proto.action.ShowDanmu
import tornaco.apps.shortx.core.proto.action.ShowPieMenu
import tornaco.apps.shortx.core.proto.action.TakeScreenshot
import tornaco.apps.shortx.core.proto.action.ToggleDarkMode
import tornaco.apps.shortx.core.proto.action.ToggleFlashLight
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.NotificationUpdated
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.pack_
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger


object PrebuiltRules {
    private val idGen = AtomicInteger(0)

    private fun nextId() = "Prebuilt-${idGen.getAndIncrement()}"
    private fun uuid() = UUID.randomUUID().toString()

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
            .build(),

        Rule.newBuilder()
            .setId(nextId())
            .setCreateTime(System.currentTimeMillis() + idGen.get() * 1000)
            .setTitle("Edge swipe PIE menu")
            .setDescription("Swipe from left edge to show a PIE quick-action menu")
            .addAllFacts(
                listOf(
                    EdgeGesture.newBuilder()
                        .setEdge(Edge.LeftAll)
                        .setGesture(Gesture.SwipeFromLeft)
                        .build()
                        .pack_()
                )
            )
            .addAllConditions(listOf(True.newBuilder().build()).map { it.pack_() })
            .addAllActions(
                listOf(
                    ShowPieMenu.newBuilder()
                        .setId(uuid())
                        .setScalePercent(100)
                        .setArcSpanDegrees(160)
                        .setRingCount(1)
                        .setLabelSizeSp(11)
                        .addItems(
                            PieMenuItem.newBuilder()
                                .setLabel("Screenshot")
                                .setIconDescription("screenshot-line")
                                .setAction(TakeScreenshot.newBuilder().setId(uuid()).build().pack_())
                        )
                        .addItems(
                            PieMenuItem.newBuilder()
                                .setLabel("Flashlight")
                                .setIconDescription("flashlight")
                                .setAction(
                                    ToggleFlashLight.newBuilder()
                                        .setId(uuid())
                                        .build().pack_()
                                )
                        )
                        .addItems(
                            PieMenuItem.newBuilder()
                                .setLabel("Notifications")
                                .setIconDescription("notification-4-line")
                                .setAction(ExpandNotification.newBuilder().setId(uuid()).build().pack_())
                        )
                        .addItems(
                            PieMenuItem.newBuilder()
                                .setLabel("Dark mode")
                                .setIconDescription("moon-line")
                                .setAction(
                                    ToggleDarkMode.newBuilder()
                                        .setId(uuid())
                                        .build().pack_()
                                )
                        )
                        .build()
                        .pack_()
                )
            )
            .build(),
    )
}