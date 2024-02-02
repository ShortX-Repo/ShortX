package tornaco.apps.shortx.services.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import tornaco.apps.shortx.core.proto.common.DownloadSubject

object DownloadHelper {

    fun downloadFile(
        context: Context,
        subject: DownloadSubject
    ): Long {
        with(subject) {
            val request = DownloadManager.Request(Uri.parse(url))

            // 设置下载标题和描述
            request.setTitle(title)
            request.setDescription(description)

            // 设置下载目录和文件名
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            // 允许下载时使用的网络类型
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

            // 允许漫游时下载
            request.setAllowedOverRoaming(true)

            // 允许下载的最大文件大小
            request.setAllowedOverMetered(true)

            // 显示下载通知
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            // 获取DownloadManager
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            // 将下载请求加入队列
            return downloadManager.enqueue(request)
        }
    }
}