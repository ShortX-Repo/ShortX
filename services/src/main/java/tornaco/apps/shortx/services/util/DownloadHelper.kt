package tornaco.apps.shortx.services.util

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import tornaco.apps.shortx.core.proto.common.DownloadSubject

object DownloadHelper {

    sealed interface DownloadStatus {
        data class Success(val localUri: String, val path: String) : DownloadStatus
        data object Fail : DownloadStatus
        data object Running : DownloadStatus
        data object Pending : DownloadStatus
        data object Paused : DownloadStatus
    }

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

    fun getDownloadStatus(context: Context, downloadId: Long): DownloadStatus {
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cursor: Cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val status: Int = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

            when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val localUri: String =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    val localPath = Uri.parse(localUri).path
                    return DownloadStatus.Success(localUri, localPath)
                }

                DownloadManager.STATUS_FAILED -> {
                    return DownloadStatus.Fail
                }

                DownloadManager.STATUS_PAUSED -> {
                    return DownloadStatus.Paused
                }

                DownloadManager.STATUS_PENDING -> {
                    return DownloadStatus.Pending
                }

                DownloadManager.STATUS_RUNNING -> {
                    return DownloadStatus.Running
                }
            }
        }

        cursor.close()
        return DownloadStatus.Fail
    }
}