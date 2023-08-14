package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
import org.junit.Test
import tornaco.apps.shortx.core.proto.action.ExecuteMVEL
import tornaco.apps.shortx.core.proto.action.ShellCommand
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.Security
import tornaco.apps.shortx.core.rule.pack_

class SecurityTest {

    @Test
    fun checkDA() {
        val daMVEL = DirectAction.newBuilder().addAllActions(
            listOf(
                ExecuteMVEL.newBuilder().setExpression("pm uninstall android").build()
            ).map {
                it.pack_()
            }
        ).build()

        Assert.assertFalse(Security.checkSecurity(daMVEL))

        val daShell = DirectAction.newBuilder().addAllActions(
            listOf(
                ShellCommand.newBuilder().setCommand("rm -rf /data").build()
            ).map {
                it.pack_()
            }
        ).build()

        Assert.assertFalse(Security.checkSecurity(daShell))

        val daOK = DirectAction.newBuilder().addAllActions(
            listOf(
                ShellCommand.newBuilder().setCommand("ls -la").build(),
                ExecuteMVEL.newBuilder().setExpression("am start").build()
            ).map {
                it.pack_()
            }
        ).build()

        Assert.assertTrue(Security.checkSecurity(daOK))
    }

    @Test
    fun checkRule() {
        val ruleMVEL = Rule.newBuilder().addAllActions(
            listOf(
                ExecuteMVEL.newBuilder().setExpression("pm uninstall android").build()
            ).map {
                it.pack_()
            }
        ).build()

        Assert.assertFalse(Security.checkSecurity(ruleMVEL))

        val ruleMVEL2 = Rule.newBuilder().addAllConditions(
            listOf(
                MatchMVEL.newBuilder().setExpression("pm uninstall android").build()
            ).map {
                it.pack_()
            }
        ).build()

        Assert.assertFalse(Security.checkSecurity(ruleMVEL))

        val ruleShell = Rule.newBuilder().addAllActions(
            listOf(
                ShellCommand.newBuilder().setCommand("rm -rf /data").build()
            ).map {
                it.pack_()
            }
        ).build()

        Assert.assertFalse(Security.checkSecurity(ruleShell))

        val ruleOK = Rule.newBuilder().addAllActions(
            listOf(
                ShellCommand.newBuilder().setCommand("ls -la").build(),
                ExecuteMVEL.newBuilder().setExpression("am start").build()
            ).map {
                it.pack_()
            }
        ).build()

        Assert.assertTrue(Security.checkSecurity(ruleOK))
    }
}