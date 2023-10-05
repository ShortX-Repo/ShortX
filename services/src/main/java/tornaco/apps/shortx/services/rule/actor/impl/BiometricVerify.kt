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

        when (case) {
            BiometricVerifyResultCase.BiometricVerifyResult_Allow_BiometricNotReady -> {

            }

            BiometricVerifyResultCase.BiometricVerifyResult_Allow_Verified -> {

            }

            BiometricVerifyResultCase.BiometricVerifyResult_Deny_VerifyFail -> {

            }

            BiometricVerifyResultCase.BiometricVerifyResult_Deny_UserCanceled -> {

            }

            BiometricVerifyResultCase.UNRECOGNIZED -> {
                throw Error("Bad case: UNRECOGNIZED")
            }
        }

    }
}