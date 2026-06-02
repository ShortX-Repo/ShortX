/*
 * (C) Copyright 2022 Thanox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package tornaco.apps.shortx.services.xposed.hooks.hook

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.xposed.hooks.hook.api.IPackageLoaded
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded

private lateinit var module: ShortXLSPosedHook100Entry
private lateinit var processName: String

@DoNotStrip
class ShortXLSPosedHook100Entry(
    base: XposedInterface,
    moduleParam: XposedModuleInterface.ModuleLoadedParam
) :
    XposedModule(base, moduleParam) {
    init {
        XposedRuntime.init(Api100XposedAdapter(base), moduleParam.isSystemServer)
        logAdapter = { _, tag, msg ->
            XposedLog.log("$tag $msg")
        }
        XposedLog.log("ShortXLSPosedHook100Entry.init ${moduleParam.processName} isSystemServer: ${moduleParam.isSystemServer}")
        module = this
        processName = moduleParam.processName
        HookStub2.initZygote()
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        super.onPackageLoaded(param)
        val pkgParam = IPackageLoaded.Param(
            param.packageName,
            processName,
            param.classLoader
        )
        XposedLog.log("ShortXLSPosedHookEntry.onPackageLoaded: $pkgParam")
        HookStub2.packageLoaded(pkgParam)
    }

    override fun onSystemServerLoaded(param: XposedModuleInterface.SystemServerLoadedParam) {
        super.onSystemServerLoaded(param)
        XposedLog.log("ShortXLSPosedHookEntry.onSystemServerLoaded")
        HookStub2.systemServerLoaded(ISystemServerLoaded.Param(param.classLoader))
    }
}