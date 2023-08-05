package app.shortx.prebuilts_gen

import tornaco.apps.shortx.core.proto.action.ShowDanmu
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.fact.BTStatusChanged
import tornaco.apps.shortx.core.proto.fact.DNDStatusChanged
import tornaco.apps.shortx.core.proto.fact.DarkModeStatusChanged
import tornaco.apps.shortx.core.proto.fact.FlashLightStatusChanged
import tornaco.apps.shortx.core.proto.fact.LocationStatusChanged
import tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged
import tornaco.apps.shortx.core.proto.fact.NFCStatusChanged
import tornaco.apps.shortx.core.proto.fact.NotificationUpdated
import tornaco.apps.shortx.core.proto.fact.WifiStatusChanged
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.pack_
import java.util.concurrent.atomic.AtomicInteger


object DevRules {
    private val idGen = AtomicInteger(0)

    private fun nextId() = "Dev-${idGen.getAndIncrement()}"

    val all = mutableListOf<Rule>().apply {
        listOf(OnOffAny.On, OnOffAny.Off, OnOffAny.Any).forEach { onOffAny ->
            add(
                Rule.newBuilder()
                    .setId(nextId())
                    .setCreateTime(System.currentTimeMillis() + idGen.get() * 1000)
                    .setTitle("Danmu notification")
                    .addAllFacts(
                        listOf(NotificationUpdated.newBuilder().build().pack_())
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


            add(
                Rule.newBuilder()
                    .setId(nextId())
                    .setCreateTime(System.currentTimeMillis() + idGen.get() * 1000)
                    .setTitle("Wifi ${onOffAny.name}")
                    .addAllFacts(
                        listOf
                            (WifiStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    )
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
                    .setCreateTime(System.currentTimeMillis() + idGen.get() * 1000)
                    .setTitle("BT ${onOffAny.name}")
                    .addAllFacts(
                        listOf
                            (BTStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    )
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
                    .setCreateTime(System.currentTimeMillis() + idGen.get() * 1000)
                    .setTitle("NFC ${onOffAny.name}")
                    .addAllFacts(
                        listOf
                            (NFCStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    )
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
                    .setCreateTime(System.currentTimeMillis() + idGen.get() * 1000)
                    .setTitle("DarkMode ${onOffAny.name}")
                    .addAllFacts(
                        listOf
                            (DarkModeStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    )
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
                    .setCreateTime(System.currentTimeMillis() + idGen.get() * 1000)
                    .setTitle("DND ${onOffAny.name}")
                    .addAllFacts(
                        listOf
                            (DNDStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    )
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
                    .setCreateTime(System.currentTimeMillis() + idGen.get() * 1000)
                    .setTitle("Location ${onOffAny.name}")
                    .addAllFacts(
                        listOf
                            (LocationStatusChanged.newBuilder().setOoa(onOffAny).build().pack_())
                    )
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
                    .setCreateTime(System.currentTimeMillis() + idGen.get() * 1000)
                    .setTitle("MobileData ${onOffAny.name}")
                    .addAllFacts(
                        listOf(
                            MobileDataStatusChanged.newBuilder().setOoa(onOffAny).build().pack_()
                        )
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
                    .setCreateTime(System.currentTimeMillis() + idGen.get() * 1000)
                    .setTitle("FlashLight ${onOffAny.name}")
                    .addAllFacts(
                        listOf(
                            FlashLightStatusChanged.newBuilder().setOoa(onOffAny).build().pack_()
                        )
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