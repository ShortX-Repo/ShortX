package tornaco.apps.shortx.core.rule

import io.kotest.matchers.shouldBe
import org.junit.Test
import tornaco.apps.shortx.core.proto.action.IfThenElse
import tornaco.apps.shortx.core.proto.action.NoAction
import tornaco.apps.shortx.core.proto.action.ShowToast
import tornaco.apps.shortx.core.proto.containsAnyTypeUrl
import tornaco.apps.shortx.core.proto.containsTypeUrl
import tornaco.apps.shortx.core.proto.da.DirectAction
import java.util.UUID

class UtilTest {
    @Test
    fun testContainsTypeUrl() {
        val da = DirectAction.newBuilder().setTitle("NoAction")
            .setId("1")
            .setCreateTime(System.currentTimeMillis())
            .setLastUpdateTime(System.currentTimeMillis())
            .addActions(NoAction.newBuilder().setId(UUID.randomUUID().toString()).build().pack_())
            .addActions(
                IfThenElse.newBuilder()
                    .addAllElseActions(listOf(ShowToast.getDefaultInstance().pack_()))
                    .build().pack_()
            )
            .build()

        da.containsTypeUrl(ShowToast.getDefaultInstance().pack_().typeUrl) shouldBe true
    }

    @Test
    fun testContainsAnyTypeUrl() {
        val da = DirectAction.newBuilder().setTitle("NoAction")
            .setId("1")
            .setCreateTime(System.currentTimeMillis())
            .setLastUpdateTime(System.currentTimeMillis())
            .addActions(NoAction.newBuilder().setId(UUID.randomUUID().toString()).build().pack_())
            .addActions(
                IfThenElse.newBuilder()
                    .addAllElseActions(listOf(ShowToast.getDefaultInstance().pack_()))
                    .build().pack_()
            )
            .build()

        da.containsAnyTypeUrl(listOf(ShowToast.getDefaultInstance().pack_().typeUrl)) shouldBe true
    }
}