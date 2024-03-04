package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.onDebugBuild
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.GlobalVarList
import tornaco.apps.shortx.core.rule.setValue
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.enc.AESCrypt
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseGlobalVarStore(
    private val scope: CoroutineScope,
    storeFile: File,
    private val encKey: String? = null
) {
    private val logger = Logger("GlobalVarStore")

    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val globalVarCaches: MutableMap<String, GlobalVar> = mutableMapOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addAllGlobalVars(globalVars: List<GlobalVar>, write: Boolean = true) {
        synchronized(globalVarCaches) {
            globalVars.forEach { globalVar ->
                globalVarCaches[globalVar.name] = globalVar
            }
            if (write) postWrite()
        }
    }

    open fun addGlobalVar(globalVar: GlobalVar) {
        synchronized(globalVarCaches) {
            globalVarCaches[globalVar.name] = globalVar
            postWrite()
        }
    }

    open fun deleteGlobalVar(varId: String): GlobalVar? {
        synchronized(globalVarCaches) {
            val deleted: GlobalVar? = globalVarCaches.remove(varId)
            postWrite()
            return deleted
        }
    }

    open fun getGlobalVarByName(name: String): GlobalVar? {
        synchronized(globalVarCaches) {
            return globalVarCaches[name]
        }
    }

    open fun getAllGlobalVars(): List<GlobalVar> {
        synchronized(globalVarCaches) {
            return globalVarCaches.values.toList()
        }
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        synchronized(globalVarCaches) {
            kotlin.runCatching {
                val list = atomicStoreFile.read {
                    GlobalVarList.parseDelimitedFrom(it)
                }
                list.run {
                    globalVarCaches.clear()
                    globalVarCaches.putAll(this.allVarsList
                        .map { globalVar ->
                            if (globalVar.isSecret && encKey != null) {
                                val currentValueEnc = globalVar.valueList { emptyList() }
                                onDebugBuild { logger.d("currentValueEnc: $currentValueEnc") }
                                val currentValueDec = currentValueEnc.map {
                                    AESCrypt.decrypt(encKey, it)
                                }
                                onDebugBuild { logger.d("currentValueDec: $currentValueDec") }
                                val decGV = globalVar.setValue(currentValueDec)
                                decGV
                            } else {
                                globalVar
                            }
                        }
                        .associateBy { it.name })
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
        synchronized(globalVarCaches) {
            logger.d("write.")
            val list = GlobalVarList.newBuilder().apply {
                addAllAllVars(globalVarCaches.values.toList().map { globalVar ->
                    if (globalVar.isSecret && encKey != null) {
                        val currentValue = globalVar.valueList { emptyList() }
                        onDebugBuild { logger.d("currentValue: $currentValue") }
                        val encValue = currentValue.map {
                            AESCrypt.encrypt(encKey, it)
                        }
                        onDebugBuild { logger.d("encValue: $encValue") }
                        val encGV = globalVar.setValue(encValue)
                        encGV
                    } else {
                        globalVar
                    }
                })
            }.build()
            kotlin.runCatching {
                atomicStoreFile.writeInlined {
                    list.writeDelimitedTo(it)
                    logger.d("write to: $atomicStoreFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}