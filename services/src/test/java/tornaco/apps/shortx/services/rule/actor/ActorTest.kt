package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
import org.junit.Test
import tornaco.apps.shortx.core.proto.action.OuterActions

class ActorTest {

    @Test
    fun `can handle class should not dup`() {
        Assert.assertEquals(
            PrebuiltActor.entries.size,
            PrebuiltActor.entries.map { it.actor.actionsCanHandle() }.size
        )
    }

    @Test
    fun `should handle all proto actions`() {
        val allActionNames = OuterActions.getDescriptor().messageTypes.map { it.name }.filter {
            "HttpRequestHeaderBodyJsonMapAdapter" != it && "HttpRequestJsonMapAdapter" != it && "HttpRequestRawAdapter" != it && "DialogMenuItem" != it
                    && "NotificationButton" != it && "SwitchCase_Case" != it && "SwitchCase_Default" != it
        }

        allActionNames.forEach { actionProtoName ->
            Assert.assertTrue(
                "Should contains Actor for Action: $actionProtoName",
                PrebuiltActor.entries.map { it.actor.actionsCanHandle().simpleName }
                    .contains(actionProtoName)
            )
        }
    }
}