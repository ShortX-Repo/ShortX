package tornaco.apps.shortx.core.rule

import io.kotest.matchers.shouldBe
import org.junit.Test
import tornaco.apps.shortx.core.proto.action.TakeScreenshot
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleList
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.toJson
import java.util.UUID

class ParserTest {
    @Test
    fun testParseRuleSet() {
        val rs = RuleSet.newBuilder()
            .setTitle("Set1")
            .setId(UUID.randomUUID().toString())
            .setRuleList(
                RuleList.newBuilder()
                    .addAllRules(
                        listOf(
                            Rule.newBuilder().setTitle("Rule1")
                                .setId(UUID.randomUUID().toString())
                                .addAllFacts(
                                    listOf(
                                        ActivityStarted.newBuilder()
                                            .setId(UUID.randomUUID().toString()).build().pack_()
                                    )
                                ).addAllActions(
                                    listOf(
                                        TakeScreenshot.newBuilder()
                                            .setId(UUID.randomUUID().toString()).build().pack_()
                                    )
                                )

                                .build(),
                            Rule.newBuilder().setTitle("Rule2")
                                .setId(UUID.randomUUID().toString())
                                .addAllFacts(
                                    listOf(
                                        ActivityStarted.newBuilder()
                                            .setId(UUID.randomUUID().toString()).build().pack_()
                                    )
                                ).addAllActions(
                                    listOf(
                                        TakeScreenshot.newBuilder()
                                            .setId(UUID.randomUUID().toString()).build().pack_()
                                    )
                                )

                                .build(),
                            Rule.newBuilder().setTitle("Rule3")
                                .setId(UUID.randomUUID().toString())
                                .addAllFacts(
                                    listOf(
                                        ActivityStarted.newBuilder()
                                            .setId(UUID.randomUUID().toString()).build().pack_()
                                    )
                                ).addAllActions(
                                    listOf(
                                        TakeScreenshot.newBuilder()
                                            .setId(UUID.randomUUID().toString()).build().pack_()
                                    )
                                )

                                .build(),
                        )
                    )
                    .build()
            )
            .build()

        val json = rs.toJson()
        println(json)

        val rsParsed = parseShareContentRuleSet(json)

        rsParsed shouldBe rs
    }
}