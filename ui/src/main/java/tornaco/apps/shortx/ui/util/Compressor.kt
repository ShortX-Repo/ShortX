package tornaco.apps.shortx.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import me.shaohui.advancedluban.Luban
import me.shaohui.advancedluban.OnCompressListener
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.ResUtils
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resumeWithException


object Compressor {
    private val logger = Logger("Compressor")

    suspend fun compressAndToBase64(
        context: Context,
        uri: Uri,
        sizeInKB: Int = 12
    ): String {
        val base64 = context.contentResolver.openInputStream(uri)?.use { rawStream ->
            val tmpImageFile =
                File(context.cacheDir, "compressor_tmp_${System.currentTimeMillis()}.png")
            rawStream.copyTo(tmpImageFile.outputStream())

            // Glide first.
            val glideCompressedFile =
                File(context.cacheDir, "compressor_scaled_${System.currentTimeMillis()}.jpg")
            val width = ResUtils.convertDpToPixel(24f, context).toInt()
            compressImageWithGlide(
                context,
                tmpImageFile,
                glideCompressedFile,
                width = width,
                height = width
            )

            // Luban then.
            val finalCompressedFile = callExternalCompressor(context, glideCompressedFile, sizeInKB)

            val byteArray = finalCompressedFile.readBytes()
            logger.i("Compressed size in KB: ${getFileSizeInKB(finalCompressedFile)}")

            kotlin.runCatching {
                tmpImageFile.deleteRecursively()
                glideCompressedFile.deleteRecursively()
                finalCompressedFile.deleteRecursively()
            }.onFailure {
                logger.e(it, "Clean up")
            }

            Base64.encodeToString(byteArray, 0)
        }
        return requireNotNull(base64) {
            "Fail to decode uri: $uri"
        }
    }

    private suspend fun callExternalCompressor(
        context: Context,
        file: File,
        sizeInKB: Int
    ): File {
        logger.d("callExternalCompressor: $file current size: ${getFileSizeInKB(file)}")
        return suspendCancellableCoroutine { con ->

            Luban.compress(context, file)
                .setMaxSize(sizeInKB)                // limit the final image size（unit：Kb）
                .putGear(Luban.CUSTOM_GEAR)     // use CUSTOM GEAR compression mode
                .launch(object : OnCompressListener {
                    override fun onStart() {
                        logger.d("onStart")
                    }

                    override fun onSuccess(file: File) {
                        logger.d("onSuccess: $file")
                        con.resume(file) {}
                    }

                    override fun onError(e: Throwable) {
                        con.resumeWithException(e)
                    }
                })
        }
    }

    suspend fun compressImageWithGlide(
        context: Context,
        inputFile: File,
        outputFile: File,
        quality: Int = 80,
        width: Int = 200,
        height: Int = 200,
    ) {
        val requestOptions = RequestOptions()
            .downsample(DownsampleStrategy.CENTER_INSIDE) // 使用居中裁剪下采样策略
            .override(width, height) // 设置目标宽高

        // 使用 withContext 切换到 IO 线程池
        val bitmap = withContext(Dispatchers.IO) {
            Glide.with(context)
                .asBitmap()
                .load(inputFile)
                .apply(requestOptions)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .submit()
                .get()
        }

        withContext(Dispatchers.IO) {
            FileOutputStream(outputFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, out) // 压缩图像并写入输出文件
            }
        }
    }

    private fun getFileSizeInKB(file: File): Long {
        if (!file.exists() || !file.isFile) {
            throw IllegalArgumentException("The provided file does not exist or is not a file.")
        }
        return file.length() / 1024
    }


}