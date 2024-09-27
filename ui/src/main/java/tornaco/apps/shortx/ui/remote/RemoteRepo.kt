package tornaco.apps.shortx.ui.remote

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import java.io.File
import javax.inject.Inject

class RemoteRepo @Inject constructor(@ApplicationContext private val context: Context) {
    private val api by lazy {
        RemoteApi.create(context)
    }
    private val cache = DiskCache(context)

    suspend fun getActions(): Flow<List<RemoteExecuteMVELAction>> {
        return flow {
            if (hasActionCache()) {
                emit(cache.read())
            }
            emit(api.getActions().apply {
                cache.save(this)
            })
        }
    }

    suspend fun hasActionCache() = cache.hasCache()

    internal class DiskCache(private val context: Context) {
        companion object {
            private val gson = Gson()
        }

        private val logger = Logger("DiskCache")

        suspend fun save(data: List<RemoteExecuteMVELAction>) {
            withContext(Dispatchers.IO) {
                val cacheFile = File(context.cacheDir, "remote_actions.json")
                val json = gson.toJson(RemoteExecuteMVELActionCache(data))
                cacheFile.deleteRecursively()
                cacheFile.writeText(json)
                logger.d("save actions to: $cacheFile")
            }
        }

        suspend fun read(): List<RemoteExecuteMVELAction> {
            val cacheFile = File(context.cacheDir, "remote_actions.json")
            return withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    val json = cacheFile.readText()
                    gson.fromJson(json, RemoteExecuteMVELActionCache::class.java).actions
                }
            }.getOrElse {
                logger.e(it, "read error")
                cacheFile.deleteRecursively()
                emptyList()
            }
        }

        fun hasCache() = File(context.cacheDir, "remote_actions.json").let {
            it.isFile && it.exists() && it.canRead()
        }
    }

    internal data class RemoteExecuteMVELActionCache(val actions: List<RemoteExecuteMVELAction>)
}