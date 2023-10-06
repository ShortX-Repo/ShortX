package tornaco.apps.shortx.services.rule.actor.impl

import android.os.Bundle
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.proto.common.BiometricVerifyResultCase
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.bundleToString
import tornaco.apps.shortx.services.app.ActivityStartResultOneTimeListener
import tornaco.apps.shortx.services.app.RequestCodeFactory
import tornaco.apps.shortx.services.rule.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.ui.biometric.BiometricVerifyActivity

object BiometricVerify : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.BiometricVerify::class.java
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
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
                            result.getString(BiometricVerifyActivity.RESULT_CASE)
                        )
                        continuation.resume(case) {}
                    }
                }
            })

            BiometricVerifyActivity.start(params.androidContext, actionRequestCode, d)
        }

        logger.d("Got case: $case")
        require(case != BiometricVerifyResultCase.UNRECOGNIZED) {
            "UNRECOGNIZED BiometricVerifyResultCase"
        }

        val allowed = case == BiometricVerifyResultCase.BiometricVerifyResult_Allow_Verified
                || case == BiometricVerifyResultCase.BiometricVerifyResult_Allow_BiometricNotReady

        if (allowed) {
            run loop@{
                if (d.allowActionsList.isNullOrEmpty().not()) {
                    d.allowActionsList.forEach {
                        val evaRes = recursiveExecute(evaluateContext, it, contextData, localVars)
                        if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                            logger.e(evaRes.error, "evaluate fail")
                            if (evaRes.shouldContinue) {
                                logger.d("Action on error is continue execute.")
                            } else {
                                return@loop
                            }
                        } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                            val scope = evaRes.scope
                            logger.w("evaluate break! $scope")
                            scope.rethrowOrReturnLoop {
                                return@loop
                            }
                        }
                    }
                }
            }
        } else {
            run loop@{
                if (d.denyActionsList.isNullOrEmpty().not()) {
                    d.denyActionsList.forEach {
                        logger.d("about to execute action: ${it.typeUrl}")
                        val evaRes =
                            recursiveExecute(evaluateContext, it, contextData, localVars)
                        if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                            logger.e(evaRes.error, "evaluate fail")
                            if (evaRes.shouldContinue) {
                                logger.d("Action on error is continue execute.")
                            } else {
                                return@loop
                            }
                        } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                            val scope = evaRes.scope
                            logger.w("evaluate break! $scope")
                            scope.rethrowOrReturnLoop {
                                return@loop
                            }
                        }
                    }
                }
            }

        }

    }
}