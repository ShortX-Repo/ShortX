package tornaco.apps.shortx.services.rule.actor.impl

import android.os.Bundle
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.DisplayImage
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.bundleToString
import tornaco.apps.shortx.services.app.ActivityStartResultOneTimeListener
import tornaco.apps.shortx.services.app.RequestCodeFactory
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.ui.media.DisplayImageActivityStarter

@OptIn(ExperimentalCoroutinesApi::class)
object DisplayImage : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.DisplayImage::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.Main) {
            val d = action unpack_ tornaco.apps.shortx.core.proto.action.DisplayImage::class.java
            val cv = RuleContextAndVars(params)

            val url = d.imageUrl.compileContextAndVars(cv)
            val actionRequestCode = RequestCodeFactory.getNext()

            suspendCancellableCoroutine { continuation ->
                shortXContext.appState.registerActivityStartResultOneTimeListeners(object :
                    ActivityStartResultOneTimeListener {
                    override fun onStartActivityResult(
                        requestCode: Int,
                        resultCode: Int,
                        result: Bundle
                    ) {
                        logger.d("onStartActivityResult: $requestCode $resultCode ${result.bundleToString()}")
                        if (requestCode == actionRequestCode) {
                            continuation.resume(Unit) {}
                        }
                    }
                })

                DisplayImageActivityStarter.start(
                    context = params.androidContext,
                    requestCode = actionRequestCode,
                    action = DisplayImage.newBuilder().setImageUrl(url).build()
                )
            }
        }
    }
}