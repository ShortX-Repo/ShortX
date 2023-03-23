package tornaco.apps.shortx.services.data

import kotlinx.coroutines.*
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.GlobalVarList
import tornaco.apps.shortx.core.util.Logger

private const val WRITE_DELAY = 3000L

class GlobalVarStore(private val scope: CoroutineScope) {
    private val logger = Logger("GlobalVarStore")
    private val storeFile by lazy { Dirs.dataStoreFile("globalVars") }

    private val globalVarCaches: MutableList<GlobalVar> = mutableListOf()

    private var job: Job? = null

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addGlobalVar(globalVar: GlobalVar) {
        globalVarCaches.removeIf { it.id == globalVar.id }
        globalVarCaches.add(globalVar)
        postWrite()
    }

    fun deleteGlobalVar(varId: String) {
        globalVarCaches.removeIf { it.id == varId }
        postWrite()
    }

    fun getGlobalVarById(id: String): GlobalVar? {
        return globalVarCaches.firstOrNull { it.id == id }
    }

    fun getAllGlobalVars(): List<GlobalVar> {
        return globalVarCaches.toList()
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
                globalVarCaches.addAll(this.allVarsList)
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
            addAllAllVars(globalVarCaches)
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