package tornaco.apps.shortx.core.proto

import io.kotest.matchers.shouldBe
import org.junit.Test
import tornaco.apps.shortx.core.proto.action.ShowDanmu
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.rule.pack_

class ProtoBuffTest {
    @Test
    fun testTypeUrl() {
        val proto = ActivityStarted.getDefaultInstance()
        proto.getTypeUrl() shouldBe ActivityStarted.getDefaultInstance().pack_().typeUrl

        val proto2 = ShowDanmu.getDefaultInstance()
        proto2.getTypeUrl() shouldBe ShowDanmu.getDefaultInstance().pack_().typeUrl
    }
}