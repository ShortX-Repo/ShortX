package tornaco.apps.shortx.core.localvar

import tornaco.apps.shortx.core.proto.gv.LocalVar
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.Pools


class LocalVars private constructor() {
    private val logger = Logger("LocalVars")

    companion object {
        val pool = Pools.SynchronizedPool<LocalVars>(100)

        fun obtain(): LocalVars =
            pool.acquire() ?: LocalVars()
    }

    private val localVarList = mutableListOf<LocalVar>()

    fun add(localVar: LocalVar) {
        localVarList.add(localVar)
    }

    fun getByName(name: String): LocalVar? = localVarList.firstOrNull { it.name == name }

    fun recycle() {
        localVarList.clear()
        pool.release(this)
    }
}

