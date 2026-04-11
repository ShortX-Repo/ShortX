package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.func.Function as ShortXFunction
import tornaco.apps.shortx.core.proto.func.FunctionList
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseFunctionSettingsStore(
    private val scope: CoroutineScope,
    storeFile: File
) {
    private val logger = Logger("FunctionSettingsStore")
    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val functionsCache: MutableList<ShortXFunction> = mutableListOf()

    private var job: Job? = null

    fun readBlocking() = runBlocking {
        readInternal()
    }

    suspend fun read() = readInternal()

    fun addFunction(function: ShortXFunction) {
        synchronized(functionsCache) {
            functionsCache.removeIf { it.id == function.id }
            functionsCache.add(function)
            postWrite()
        }
    }

    fun addFunctions(functions: List<ShortXFunction>, write: Boolean = true) {
        synchronized(functionsCache) {
            functions.forEach { function ->
                functionsCache.removeIf { it.id == function.id }
                functionsCache.add(function)
            }
            if (write) postWrite()
        }
    }

    fun deleteFunction(id: String) {
        synchronized(functionsCache) {
            functionsCache.removeIf { it.id == id }
            postWrite()
        }
    }

    fun getFunctionById(id: String): ShortXFunction? {
        synchronized(functionsCache) {
            return functionsCache.firstOrNull { it.id == id }
        }
    }

    fun getAllFunctions(): List<ShortXFunction> {
        synchronized(functionsCache) {
            return functionsCache.toList()
        }
    }

    fun getFunctionCount() = synchronized(functionsCache) {
        functionsCache.size
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        synchronized(functionsCache) {
            if (!atomicStoreFile.exists()) {
                logger.d("skip read no-exists file: $atomicStoreFile")
                return@withContext
            }
            kotlin.runCatching {
                val functionList = atomicStoreFile.read {
                    FunctionList.parseDelimitedFrom(it)
                }
                functionList.run {
                    functionsCache.clear()
                    functionsCache.addAll(this.functionsList)
                }
            }.onFailure {
                logger.e(it, "read error")
            }
        }
    }

    private fun postWrite() {
        job?.cancel()
        job = scope.launch {
            delay(WRITE_DELAY)
            write()
        }
    }

    suspend fun write() = withContext(Dispatchers.IO) {
        synchronized(functionsCache) {
            logger.d("write.")
            val functionList = FunctionList.newBuilder().apply {
                addAllFunctions(functionsCache)
            }.build()
            kotlin.runCatching {
                atomicStoreFile.writeInlined {
                    functionList.writeDelimitedTo(it)
                    logger.d("write to: $atomicStoreFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}