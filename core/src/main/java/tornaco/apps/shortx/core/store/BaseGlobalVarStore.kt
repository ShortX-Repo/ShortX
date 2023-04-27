package tornaco.apps.shortx.core.store

import kotlinx.coroutines.*
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.GlobalVarList
import tornaco.apps.shortx.core.util.Logger
import java.io.File

open class BaseGlobalVarStore(
    private val scope: CoroutineScope,
    private val storeFile: File,
) {
    private val logger = Logger("GlobalVarStore")

    private val globalVarCaches: MutableMap<String, GlobalVar> = mutableMapOf()

    private var job: Job? = null

    fun readBlocking() = runBlocking {
        readInternal()
    }

    open fun addGlobalVar(globalVar: GlobalVar) {
        globalVarCaches[globalVar.name] = globalVar
        postWrite()
    }

    open fun deleteGlobalVar(varId: String): GlobalVar? {
        val deleted: GlobalVar? = globalVarCaches.remove(varId)
        postWrite()
        return deleted
    }

    fun getGlobalVarByName(name: String): GlobalVar? {
        return globalVarCaches[name]
    }

    fun getAllGlobalVars(): List<GlobalVar> {
        return globalVarCaches.values.toList()
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!storeFile.exists()) {
            logger.d("skip read no-exists file: $storeFile")
            return@withContext
        }
        kotlin.runCatching {
            val list = storeFile.inputStream().use {
                GlobalVarList.parseDelimitedFrom(it)
            }
            list.run {
                globalVarCaches.clear()
                globalVarCaches.putAll(this.allVarsList.associateBy { it.name })
            }
        }.onFailure {
            logger.e(it, "read error")
        }
    }

    private fun postWrite() {
        job?.cancel()
        job = scope.launch {
            delay(WRITE_DELAY)
            write()
        }
    }

    private suspend fun write() = withContext(Dispatchers.IO) {
        logger.d("write.")
        val list = GlobalVarList.newBuilder().apply {
            addAllAllVars(globalVarCaches.values.toList())
        }.build()
        kotlin.runCatching {
            storeFile.apply { parentFile.mkdirs() }.outputStream().use {
                list.writeDelimitedTo(it)
                logger.d("write to: $storeFile")
            }
        }.onFailure {
            logger.e(it, "write error")
        }
    }
}