package app.shortx.prebuilts_gen

import tornaco.apps.shortx.core.proto.action.ShowDanmu
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.fact.*
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.pack_
import java.util.concurrent.atomic.AtomicInteger


object DevRules {
    private val idGen = AtomicInteger(0)

    private fun nextId() = "DevRule-${idGen.getAndIncrement()}"

    val all = mutableListOf<Rule>().apply {
        listOf(OnOffAny.On, OnOffAny.Off, OnOffAny.Any).forEach { onOffAny ->
            add(
                Rule.newBuilder()
                    .setId(nextId())
                    .setTitle("Wifi ${onOffAny.name}")
                    .setFact(WifiStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    .addAllConditions(listOf(True.newBuilder().build()).map { it.pack_() })
                    .addAllActions(
                        listOf(
                            ShowDanmu.newBuilder().setText("Wifi ${onOffAny.name}").build().pack_()
                        )
                    )
                    .build()
            )

            add(
                Rule.newBuilder()
                    .setId(nextId())
                    .setTitle("BT ${onOffAny.name}")
                    .setFact(BTStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    .addAllConditions(listOf(True.newBuilder().build()).map { it.pack_() })
                    .addAllActions(
                        listOf(
                            ShowDanmu.newBuilder().setText("BT ${onOffAny.name}").build().pack_()
                        )
                    )
                    .build()
            )

            add(
                Rule.newBuilder()
                    .setId(nextId())
                    .setTitle("NFC ${onOffAny.name}")
                    .setFact(NFCStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    .addAllConditions(listOf(True.newBuilder().build()).map { it.pack_() })
                    .addAllActions(
                        listOf(
                            ShowDanmu.newBuilder().setText("NFC ${onOffAny.name}").build().pack_()
                        )
                    )
                    .build()
            )

            add(
                Rule.newBuilder()
                    .setId(nextId())
                    .setTitle("DarkMode ${onOffAny.name}")
                    .setFact(DarkModeStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    .addAllConditions(listOf(True.newBuilder().build()).map { it.pack_() })
                    .addAllActions(
                        listOf(
                            ShowDanmu.newBuilder().setText("DarkMode ${onOffAny.name}").build()
                                .pack_()
                        )
                    )
                    .build()
            )

            add(
                Rule.newBuilder()
                    .setId(nextId())
                    .setTitle("DND ${onOffAny.name}")
                    .setFact(DNDStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    .addAllConditions(listOf(True.newBuilder().build()).map { it.pack_() })
                    .addAllActions(
                        listOf(
                            ShowDanmu.newBuilder().setText("DND ${onOffAny.name}").build().pack_()
                        )
                    )
                    .build()
            )

            add(
                Rule.newBuilder()
                    .setId(nextId())
                    .setTitle("Location ${onOffAny.name}")
                    .setFact(LocationStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    .addAllConditions(listOf(True.newBuilder().build()).map { it.pack_() })
                    .addAllActions(
                        listOf(
                            ShowDanmu.newBuilder().setText("Location ${onOffAny.name}").build()
                                .pack_()
                        )
                    )
                    .build()
            )

            add(
                Rule.newBuilder()
                    .setId(nextId())
                    .setTitle("MobileData ${onOffAny.name}")
                    .setFact(
                        MobileDataStatusChanged.newBuilder().setOoa(onOffAny).build().pack_()
                    )
                    .addAllConditions(listOf(True.newBuilder().build()).map { it.pack_() })
                    .addAllActions(
                        listOf(
                            ShowDanmu.newBuilder().setText("MobileData ${onOffAny.name}").build()
                                .pack_()
                        )
                    )
                    .build()
            )

            add(
                Rule.newBuilder()
                    .setId(nextId())
                    .setTitle("FlashLight ${onOffAny.name}")
                    .setFact(
                        FlashLightStatusChanged.newBuilder().setOoa(onOffAny).build().pack_()
                    )
                    .addAllConditions(listOf(True.newBuilder().build()).map { it.pack_() })
                    .addAllActions(
                        listOf(
                            ShowDanmu.newBuilder().setText("FlashLight ${onOffAny.name}").build()
                                .pack_()
                        )
                    )
                    .build()
            )
        }
    }
}