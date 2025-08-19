package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.WebDavProfile
import tornaco.apps.shortx.core.proto.common.WebDavProfileList
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.enc.AESCrypt
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseWebDavProfileStore(
    private val scope: CoroutineScope,
    private val encKey: String? = null,
    storeFile: File
) {
    private val logger = Logger("WebDavProfileStore")

    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val webDavProfileCache: MutableList<WebDavProfile> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addAllProfiles(records: List<WebDavProfile>, write: Boolean = true) {
        synchronized(webDavProfileCache) {
            records.forEach { record ->
                synchronized(webDavProfileCache) {
                    webDavProfileCache.removeIf { it.profileId == record.profileId }
                    webDavProfileCache.add(record)
                }
            }
            if (write) postWrite()
        }
    }

    fun addProfile(profile: WebDavProfile) {
        synchronized(webDavProfileCache) {
            webDavProfileCache.removeIf { profile.profileId == it.profileId }
            webDavProfileCache.add(profile)
            postWrite()
        }
    }

    fun removeProfile(profileId: String) {
        synchronized(webDavProfileCache) {
            webDavProfileCache.removeIf { it.profileId == profileId }
            postWrite()
        }
    }

    fun getAllProfiles(): List<WebDavProfile> {
        synchronized(webDavProfileCache) {
            return webDavProfileCache.toList()
        }
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        kotlin.runCatching {
            val settingList = atomicStoreFile.read {
                WebDavProfileList.parseDelimitedFrom(it)
            }
            synchronized(webDavProfileCache) {
                webDavProfileCache.clear()
                webDavProfileCache.addAll(settingList.profilesList.map {
                    val profile = WebDavProfile.newBuilder(it)
                        .setUserName(AESCrypt.decrypt(encKey, it.userName))
                        .setPassword(AESCrypt.decrypt(encKey, it.password))
                        .build()
                    profile
                })
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

    suspend fun write() = withContext(Dispatchers.IO) {
        logger.d("write.")

        synchronized(webDavProfileCache) {
            val settingList = WebDavProfileList.newBuilder().apply {
                addAllProfiles(webDavProfileCache.map {
                    val profile = WebDavProfile.newBuilder(it)
                        .setUserName(AESCrypt.encrypt(encKey, it.userName))
                        .setPassword(AESCrypt.encrypt(encKey, it.password))
                        .build()
                    profile
                })
            }.build()
            kotlin.runCatching {
                atomicStoreFile.writeInlined {
                    settingList.writeDelimitedTo(it)
                    logger.d("write to: $atomicStoreFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}