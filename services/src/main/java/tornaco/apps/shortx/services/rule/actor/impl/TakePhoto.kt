package tornaco.apps.shortx.services.rule.actor.impl

import android.os.Bundle
import com.google.protobuf.Message
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.app.ActivityStartResultOneTimeListener
import tornaco.apps.shortx.services.app.RequestCodeFactory
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.util.MediaUtil
import tornaco.apps.shortx.ui.takephoto.TakePhotoActivityStarter

object TakePhoto : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.TakePhoto::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.TakePhoto::class.java

        val actionRequestCode = RequestCodeFactory.getNext()
        val contextAndVars = RuleContextAndVars(params)
        val compiledTargetPath = d.targetFilePath.fallbackOnEmpty("").compileContextAndVars(contextAndVars)

        data class PhotoResult(val filePath: String, val fileUri: String)

        val result = suspendCancellableCoroutine { continuation ->
            shortXContext.appState.registerActivityStartResultOneTimeListeners(object :
                ActivityStartResultOneTimeListener {
                override fun onStartActivityResult(
                    requestCode: Int,
                    resultCode: Int,
                    result: Bundle
                ) {
                    if (requestCode == actionRequestCode) {
                        val filePath = result.getString(TakePhotoActivityStarter.RESULT_FILE_PATH).orEmpty()
                        val fileUri = result.getString(TakePhotoActivityStarter.RESULT_FILE_URI).orEmpty()
                        continuation.resume(PhotoResult(filePath, fileUri)) {}
                    }
                }
            })

            TakePhotoActivityStarter.start(
                context = params.androidContext,
                requestCode = actionRequestCode,
                targetFilePath = compiledTargetPath
            )
        }

        if (result.filePath.isEmpty()) {
            error("Photo was not taken or was cancelled.")
        }

        val fileAndUri = MediaUtil.scanFile(androidContext, result.filePath)
        logger.i("TakePhoto fileAndUri: $fileAndUri")

        val finalPath = fileAndUri.first
        val finalUri = fileAndUri.second.toString()

        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.TakePhoto.photoFilePath.name)
                .fallbackOnEmpty { ContextDataMapping.TakePhoto.photoFilePath.name },
            finalPath
        )
        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.TakePhoto.photoFileUri.name)
                .fallbackOnEmpty { ContextDataMapping.TakePhoto.photoFileUri.name },
            finalUri
        )

        Actor.Res(finalPath)
    }
}
