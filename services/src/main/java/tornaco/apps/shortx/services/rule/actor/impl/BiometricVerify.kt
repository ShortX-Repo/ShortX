package tornaco.apps.shortx.services.rule.actor.impl

import android.os.Bundle
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.proto.action.BiometricVerify
import tornaco.apps.shortx.core.proto.common.BiometricVerifyResultCase
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.bundleToString
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.app.ActivityStartResultOneTimeListener
import tornaco.apps.shortx.services.app.RequestCodeFactory
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.util.runSubActions
import tornaco.apps.shortx.ui.biometric.BiometricVerifyActivityStarter

object BiometricVerify : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.BiometricVerify::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.BiometricVerify::class.java

        val actionRequestCode = RequestCodeFactory.getNext()

        val case = suspendCancellableCoroutine { continuation ->
            shortXContext.appState.registerActivityStartResultOneTimeListeners(object :
                ActivityStartResultOneTimeListener {
                override fun onStartActivityResult(
                    requestCode: Int,
                    resultCode: Int,
                    result: Bundle
                ) {
                    logger.d("onStartActivityResult: $requestCode $resultCode ${result.bundleToString()}")
                    if (requestCode == actionRequestCode) {
                        val case = BiometricVerifyResultCase.valueOf(
                            result.getString(BiometricVerifyActivityStarter.RESULT_CASE)
                        )
                        continuation.resume(case) {}
                    }
                }
            })

            val contextAndVars = RuleContextAndVars(params)

            BiometricVerifyActivityStarter.start(
                context = params.androidContext,
                requestCode = actionRequestCode,
                action = BiometricVerify
                    .newBuilder(d)
                    .setTitle(d.title.fallbackOnEmpty("").compileContextAndVars(contextAndVars))
                    .setSubTitle(d.subTitle.compileContextAndVars(contextAndVars))
                    .build()
            )
        }

        logger.d("Got case: $case")
        require(case != BiometricVerifyResultCase.UNRECOGNIZED) {
            "UNRECOGNIZED BiometricVerifyResultCase"
        }

        val allowed = case == BiometricVerifyResultCase.BiometricVerifyResult_Allow_Verified
                || case == BiometricVerifyResultCase.BiometricVerifyResult_Allow_BiometricNotReady

        if (allowed) {
            if (d.allowActionsList.isNullOrEmpty().not()) {
                runSubActions(d.allowActionsList)
            }
        } else {
            if (d.denyActionsList.isNullOrEmpty().not()) {
                runSubActions(d.denyActionsList)
            }
        }

        Actor.Res.EMPTY
    }
}