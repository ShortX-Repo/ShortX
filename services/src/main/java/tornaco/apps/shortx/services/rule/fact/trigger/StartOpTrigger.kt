package tornaco.apps.shortx.services.rule.fact.trigger

import android.app.AppOpsManager
import android.content.Context
import android.os.UserHandle
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.OnStartOp
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.ops.AppOpsManagerCompat

class StartOpTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("StartOpTrigger")
    private var isRegistered: Boolean = false

    override fun register() {
        checkAndRegister()
    }

    override fun unRegister() {

    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        checkAndRegister()
    }

    override fun onAddRule(rule: Rule) {
        super.onAddRule(rule)
        checkAndRegister()
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)
        checkAndRegister()
    }

    private fun checkAndRegister() {
        if (isRegistered) return

        val hasFact = shortXContext.rule.getAllEnabledRules().any { rule ->
            rule.factsList.any {
                it is_ OnStartOp::class.java
            } || rule.quit.factsList.any {
                it is_ OnStartOp::class.java
            }
        }

        if (hasFact) {
            AppOpsManagerCompat.startWatchingActive(
                context,
                intArrayOf(
                    AppOpsManager.OP_CAMERA,
                    AppOpsManager.OP_RECORD_AUDIO,
                    AppOpsManager.OP_COARSE_LOCATION,
                    AppOpsManager.OP_FINE_LOCATION,
                ),
                object : AppOpsManagerCompat.OnOpActiveChangedListenerCompat {
                    override fun onOpActiveChanged(
                        opCode: Int,
                        uid: Int,
                        packageName: String,
                        active: Boolean
                    ) {
                        logger.i("onOpActiveChanged: $opCode $uid $packageName $active")
                        val app = shortXContext.appState.pkgToApp(
                            Pkg(
                                packageName,
                                UserHandle.getUserId(uid)
                            )
                        )
                        app?.let {
                            shortXContext.rule.publishFact(
                                "onOpActiveChanged",
                                OnStartOp.newBuilder()
                                    .addAllApps(listOf(app.pkg))
                                    .addAllOpCodes(listOf(opCode))
                                    .build(),
                                mapOf(
                                    ContextDataMapping.OnStartOp.opCode.name to opCode,
                                    ContextDataMapping.OnStartOp.opAppLabel.name to app.label,
                                    ContextDataMapping.OnStartOp.opAppPkgName.name to app.pkg.pkgName,
                                    ContextDataMapping.OnStartOp.opAppUid.name to app.uid,
                                    ContextDataMapping.OnStartOp.opAppUserId.name to app.pkg.userId,
                                )
                            )
                        }


                    }
                })
            isRegistered = true
        }
    }
}