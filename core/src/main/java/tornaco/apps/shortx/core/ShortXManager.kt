package tornaco.apps.shortx.core

import android.content.Context
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.util.Logger
import java.lang.reflect.Method
import java.lang.reflect.Proxy

const val PROXY_SERVICE_NAME = Context.PRINT_SERVICE
val IPC_CODE = "tornaco.apps.shortx.services.SHORTX_SERVER".hashCode()

private val defaultServiceStub = IShortX.Default()
private val logger = Logger("ShortXManager")

val shortXManager
    get() = ShortXManager(
        (ShortXManagerNative.service
            ?: Proxy.newProxyInstance(
                IShortX::class.java.classLoader,
                arrayOf(IShortX::class.java),
                fun(_: Any, method: Method, args: Array<out Any>): Any? {
                    logger.w("Invoke defaultServiceStub: ${method.name}")
                    return method.invoke(defaultServiceStub, args)
                }
            )) as IShortX
    )

class ShortXManager(private val service: IShortX) {
    fun isInstalled(): Boolean =
        kotlin.runCatching { service.fingerprint() != null }.getOrElse { false }

    fun version(): Version {
        return invokeService(Version("?", 0)) {
            version()
        }
    }

    fun fingerprint(): String {
        return invokeService("?") {
            fingerprint()
        }
    }

    fun addRule(rule: Rule) {
        invokeService(Unit) {
            addRule(ByteArrayWrapper(rule.toByteArray()))
        }
    }

    fun deleteRule(ruleId: String) {
        invokeService(Unit) {
            deleteRule(ruleId)
        }
    }

    fun getRuleById(id: String): Rule? {
        return invokeService(null) {
            getRuleById(id)?.byte?.let {
                Rule.parseFrom(it)
            }
        }
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        invokeService(Unit) {
            setRuleEnabled(ruleId, enable)
        }
    }

    fun getAllRules(): List<Rule> {
        return invokeService(emptyList()) {
            allRules.map {
                Rule.parseFrom(it.byte)
            }
        }
    }

    fun getInstalledApps(): List<App> {
        return invokeService(emptyList()) {
            installedApps
        }
    }

    fun getAppLabel(pkg: Pkg): String {
        return invokeService("Unknown") {
            getAppLabel(pkg) ?: "Unknown"
        }
    }

    fun showEdgeHint(edge: Edge) {
        invokeService(Unit) {
            showEdgeHint(edge.number)
        }
    }

    private inline fun <T> invokeService(defaultValue: T, onService: IShortX.() -> T): T {
        return kotlin.runCatching {
            onService(service)
        }.getOrElse {
            logger.e(it, "invokeService error")
            defaultValue
        }
    }

    private inline fun <T> invokeService(
        defaultValue: T,
        vararg logTokens: Any,
        onService: IShortX.() -> T,
    ): T {
        return kotlin.runCatching {
            onService(service)
        }.getOrElse {
            logger.e(it, "invokeService error, arg: ${logTokens.joinToString(", ")}")
            defaultValue
        }
    }
}

object ShortXManagerNative {
    private val logger = Logger("ShortXManagerNative")

    var binder: IBinder? = null

    val service: IShortX? by lazy {
        getServiceOrNull()
    }

    private fun getServiceOrNull(): IShortX? {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            val proxyBinder = binder
            logger.d("proxyBinder: $proxyBinder")
            if (proxyBinder == null) {
                null
            } else {
                data.writeInterfaceToken(IShortX::class.java.name)
                proxyBinder.transact(IPC_CODE, data, reply, 0)
                val binder = reply.readStrongBinder()
                logger.d("shortx binder: $binder")
                IShortX.Stub.asInterface(binder)
            }
        } catch (e: RemoteException) {
            logger.e(e, "Get err")
            null
        } finally {
            data.recycle()
            reply.recycle()
        }
    }
}