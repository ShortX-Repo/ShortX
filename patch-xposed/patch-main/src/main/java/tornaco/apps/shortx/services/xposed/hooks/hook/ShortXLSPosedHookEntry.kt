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

import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.xposed.hooks.hook.api.IPackageLoaded
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded

private lateinit var processName: String

@DoNotStrip
class ShortXLSPosedHookEntry : XposedModule() {
    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        super.onModuleLoaded(param)
        XposedRuntime.initApi101(this, param.isSystemServer)
        logAdapter = { _, tag, msg ->
            XposedLog.log("$tag $msg")
        }
        XposedLog.log("ShortXLSPosedHookEntry.onModuleLoaded ${param.processName} isSystemServer: ${param.isSystemServer}")
        processName = param.processName
        HookStub2.initZygote()
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        super.onPackageReady(param)
        val pkgParam = IPackageLoaded.Param(
            param.packageName,
            processName,
            param.classLoader
        )
        XposedLog.log("ShortXLSPosedHookEntry.onPackageReady: $pkgParam")
        HookStub2.packageLoaded(pkgParam)
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        super.onPackageLoaded(param)
        if (BuildPropInternal.APP_IS_DEBUG) {
            XposedLog.log("ShortXLSPosedHookEntry.onPackageLoaded: $param")
        }
    }

    override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        super.onSystemServerStarting(param)
        XposedLog.log("ShortXLSPosedHookEntry.onSystemServerStarting")
        HookStub2.systemServerLoaded(ISystemServerLoaded.Param(param.classLoader))
    }
}

// 在 LibXposed API 101 中，模块的生命周期被重新设计，以应对 Android 10 (API 29) 之后引入的 AppComponentFactory 等极早期组件加载机制。
// onPackageLoaded 和 onPackageReady 是模块注入目标应用时最核心的两个回调，它们的调用时机有着严格的先后顺序和应用场景区别：

// 1. onPackageLoaded (极早期调用)这是目标应用进程中最早能拿到应用 ClassLoader 的时机。调用时机：当目标应用的默认 ClassLoader 刚刚被系统创建（就绪）时触发。
// 最关键的是，此时处于 AppComponentFactory 实例化之前（针对 Android 10+ API 29+）。
// 应用状态：此时应用的任何代码（包括自定义的 Application 类、ContentProvider 等）都没有开始执行。系统环境处于非常干净的初始状态。
// 最佳用途：Hook 极早期的系统组件回调，例如拦截并替换目标应用的 AppComponentFactory 逻辑。Hook 自定义 Application 类的 <init> 构造函数或 attachBaseContext。
// 如果你发现有些早期的静态代码块或者类初始化在你执行 Hook 之前就已经运行了，把 Hook 逻辑移到 onPackageLoaded 里通常能解决“错失时机”的问题。

// 2. onPackageReady (常规早期调用)这个时机等同于传统 Legacy Xposed API（API 93 及以下）中的 IXposedHookLoadPackage.handleLoadPackage 回调时机。
// 调用时机：在应用的 ClassLoader 彻底创建完毕、并且系统已经准备好加载应用的基础组件之后触发。此时 AppComponentFactory 的早期阶段通常已经走完。
// 应用状态：应用的 Context 和基础资源准备进入就绪状态，即将（或正在）实例化 Application 并执行 onCreate。最佳用途：绝大多数的常规 Hook 操作都应该放在这里。
// Hook Activity 的生命周期（如 onCreate）。Hook 业务逻辑、网络请求工具类、数据解析等。
//
// 总结与建议生命周期对应阶段主要用途对应旧版 XposedonPackageLoaded极早期 (AppComponentFactory 前)抢占最早的 Hook 时机，Hook 顶层组件加载机制无完全对应，
// 比旧版更早onPackageReady常规早期 (应用主流程即将启动)常规业务逻辑的 Hook 和类替换handleLoadPackage开发建议： 大多数普通的逆向修改直接在 onPackageReady 中实现即可，
// 这也最符合你从 Legacy 版本迁移过来的心智模型。只有当你发现目标 App 加固了某些极早期的初始化逻辑，或者你需要干预 App 的基础组件加载过程时，才使用 onPackageLoaded。