# Xposed API 101 升级手册

本文面向需要将 Xposed 模块升级到 **Xposed API 101** 的项目，重点覆盖工程配置、入口迁移、Hook 写法、混淆规则、兼容策略和常见问题排查。

> 说明：不同框架、模板或 `libxposed` 版本的具体类名可能存在细微差异。本文以现代 Xposed / libxposed API 的迁移思路为主，实际落地时请以项目当前依赖和框架文档为准。

## 1. 升级目标

Xposed 模块常见 API 体系主要有两类：

| 类型 | 典型包名 | 常见入口 | 说明 |
| --- | --- | --- | --- |
| 传统 Xposed API | `de.robv.android.xposed.*` | `IXposedHookLoadPackage` | 老式模块常用，依赖 `XposedBridge`、`XposedHelpers`、`XC_MethodHook` |
| 现代 Xposed API | `io.github.libxposed.api.*` | `XposedModule` / 模块生命周期回调 | 面向新一代框架，API 100/101 通常属于这一类 |

升级到 API 101 通常不是简单修改依赖版本，而是需要完成以下迁移：

- 模块入口迁移
- Hook 注册方式迁移
- Hook 回调写法迁移
- 日志与配置读取迁移
- 混淆规则更新
- 多进程与 ClassLoader 逻辑复查

## 2. 推荐升级流程

建议按以下顺序升级，避免一次性大重构导致问题难以定位：

1. 记录当前所有 Hook 点、目标包名、进程名和资源 Hook。
2. 升级 Gradle 依赖到 API 101。
3. 新增现代 API 入口，先迁移一个最小 Hook 点验证加载链路。
4. 逐个迁移旧的 `XposedHelpers.findAndHookMethod` 调用。
5. 迁移日志、配置读取、资源 Hook 等辅助能力。
6. 更新 R8 / ProGuard keep 规则。
7. 分别验证 debug 包、release 包、多进程和目标 App 多版本。

## 3. Gradle 配置

Xposed API 一般应使用 `compileOnly`，运行时由 Xposed 框架提供。

```kotlin
dependencies {
    compileOnly("io.github.libxposed:api:101")
}
```

如果项目仍需兼容传统 API，可临时保留旧依赖：

```kotlin
dependencies {
    compileOnly("de.robv.android.xposed:api:82")
    compileOnly("io.github.libxposed:api:101")
}
```

如果你的目标和本项目一样，是彻底迁移到 LSPosed API 101 并废弃 legacy 支持，则不要再保留传统 API 依赖，也不要继续携带本地旧版 API jar。

推荐优先使用上游 Maven Central 依赖，例如：

```kotlin
dependencies {
    compileOnly("io.github.libxposed:api:101.0.1")
}
```

注意：`io.github.libxposed:api:101.0.1` 发布物是 AAR。Android 模块可以直接消费；纯 Kotlin/JVM 模块如果需要 `compileOnly`，可能需要在构建中提取其中的 `classes.jar` 参与编译。

长期维护时建议将传统入口和现代入口隔离，避免在同一个类中混写两套 API。

推荐结构：

```text
src/main/java/your/package/module/
  legacy/
    LegacyXposedEntry.kt
  modern/
    ModernXposedModule.kt
  common/
    TargetPackages.kt
    Reflect.kt
    ModuleConfig.kt
```

## 4. Manifest 配置

传统模块常见配置如下：

```xml
<application>
    <meta-data
        android:name="xposedmodule"
        android:value="true" />

    <meta-data
        android:name="xposeddescription"
        android:value="Your module description" />

    <meta-data
        android:name="xposedminversion"
        android:value="82" />
</application>
```

如果模块只支持 API 101，可以将最低版本提升到对应版本：

```xml
<application>
    <meta-data
        android:name="xposedmodule"
        android:value="true" />

    <meta-data
        android:name="xposeddescription"
        android:value="Your module description" />

    <meta-data
        android:name="xposedminversion"
        android:value="101" />
</application>
```

注意事项：

- 如果仍需兼容旧框架，不要直接把 `xposedminversion` 改成 `101`。
- 如果现代 API 需要额外声明模块入口，请按所使用模板或框架文档补充对应 `meta-data`。
- Manifest 变更后需要重新安装模块 APK，并在框架中重新启用模块。

## 5. 模块入口迁移

传统 API 写法通常如下：

```kotlin
class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.example.target") return

        XposedHelpers.findAndHookMethod(
            "com.example.target.MainActivity",
            lpparam.classLoader,
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    XposedBridge.log("before onCreate")
                }
            }
        )
    }
}
```

现代 API 的核心思路是：

- 入口类继承现代模块基类。
- 在包加载回调中判断目标包名和进程名。
- 使用目标 App 的 `ClassLoader` 加载类。
- 通过反射拿到 `Method` 或 `Constructor`。
- 调用现代 Hook API 绑定 Hooker 类。

示例：

```kotlin
class MainModule(
    base: XposedInterface,
    param: XposedModuleInterface.ModuleLoadedParam
) : XposedModule(base, param) {

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        if (param.packageName != "com.example.target") return
        if (param.processName != "com.example.target") return

        runCatching {
            val clazz = param.classLoader.loadClass("com.example.target.MainActivity")
            val method = clazz.getDeclaredMethod("onCreate", Bundle::class.java)

            hook(method, MainActivityOnCreateHooker::class.java)
        }.onFailure {
            base.log("Failed to hook MainActivity.onCreate", it)
        }
    }
}
```

Hooker 示例：

```kotlin
class MainActivityOnCreateHooker : XposedInterface.Hooker {
    companion object {
        @JvmStatic
        fun before(param: XposedInterface.BeforeHookCallback) {
            param.xposed.log("before MainActivity.onCreate")
        }

        @JvmStatic
        fun after(param: XposedInterface.AfterHookCallback) {
            param.xposed.log("after MainActivity.onCreate")
        }
    }
}
```

## 6. API 对照表

| 传统 API | API 101 迁移方向 |
| --- | --- |
| `IXposedHookLoadPackage` | `XposedModule` / 模块生命周期回调 |
| `XC_LoadPackage.LoadPackageParam` | `PackageLoadedParam` |
| `XposedHelpers.findAndHookMethod` | 反射获取 `Method` 后调用 `hook(...)` |
| `XposedHelpers.findAndHookConstructor` | 反射获取 `Constructor` 后调用 `hook(...)` |
| `XC_MethodHook` | `Hooker` 类 |
| `beforeHookedMethod` | `before` 回调 |
| `afterHookedMethod` | `after` 回调 |
| `XposedBridge.log(...)` | `xposed.log(...)` / `base.log(...)` |
| `XposedHelpers.findClass(...)` | `param.classLoader.loadClass(...)` |
| `XposedHelpers.getObjectField(...)` | Java / Kotlin 反射 |

## 7. Hook 普通方法

旧写法：

```kotlin
XposedHelpers.findAndHookMethod(
    clazz,
    "getUserName",
    object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            param.result = "patched"
        }
    }
)
```

新写法：

```kotlin
val method = clazz.getDeclaredMethod("getUserName")
hook(method, GetUserNameHooker::class.java)
```

```kotlin
class GetUserNameHooker : XposedInterface.Hooker {
    companion object {
        @JvmStatic
        fun after(param: XposedInterface.AfterHookCallback) {
            param.result = "patched"
        }
    }
}
```

## 8. Hook 构造方法

旧写法：

```kotlin
XposedHelpers.findAndHookConstructor(
    clazz,
    String::class.java,
    object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            XposedBridge.log("constructor hooked")
        }
    }
)
```

新写法：

```kotlin
val constructor = clazz.getDeclaredConstructor(String::class.java)
hook(constructor, ConstructorHooker::class.java)
```

```kotlin
class ConstructorHooker : XposedInterface.Hooker {
    companion object {
        @JvmStatic
        fun after(param: XposedInterface.AfterHookCallback) {
            param.xposed.log("constructor hooked")
        }
    }
}
```

## 9. 修改参数与返回值

修改入参：

```kotlin
class UpdateArgumentHooker : XposedInterface.Hooker {
    companion object {
        @JvmStatic
        fun before(param: XposedInterface.BeforeHookCallback) {
            param.args[0] = "new value"
        }
    }
}
```

修改返回值：

```kotlin
class UpdateResultHooker : XposedInterface.Hooker {
    companion object {
        @JvmStatic
        fun after(param: XposedInterface.AfterHookCallback) {
            param.result = "patched result"
        }
    }
}
```

阻止原方法执行并直接返回：

```kotlin
class SkipOriginalHooker : XposedInterface.Hooker {
    companion object {
        @JvmStatic
        fun before(param: XposedInterface.BeforeHookCallback) {
            param.result = false
        }
    }
}
```

注意：不同 API 版本可能使用 `setResult`、`returnAndSkip` 或类似方法表达“设置返回值并跳过原方法”。迁移时必须确认当前 API 101 的真实语义。

## 10. ClassLoader 规则

Hook 目标 App 的类时，必须使用目标 App 的 ClassLoader。

错误示例：

```kotlin
Class.forName("com.example.target.SomeClass")
```

推荐示例：

```kotlin
val clazz = param.classLoader.loadClass("com.example.target.SomeClass")
```

原因：

- 目标 App 类不在模块自身 ClassLoader 中。
- 多进程下不同进程加载的类可能不同。
- 插件化或加固 App 可能延迟加载类。
- 目标 App 升级后类名和方法签名可能变化。

## 11. 包名与进程过滤

不要只判断包名。很多目标 App 存在多进程，错误进程中 Hook 可能导致类找不到、重复执行或崩溃。

只 Hook 主进程：

```kotlin
override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
    if (param.packageName != "com.example.target") return
    if (param.processName != "com.example.target") return

    // Hook main process only.
}
```

Hook 远程进程：

```kotlin
if (param.processName != "com.example.target:remote") return
```

常见现象：

| 现象 | 可能原因 |
| --- | --- |
| Hook 没生效 | Hook 代码运行在错误进程 |
| App 启动崩溃 | 所有进程都执行了同一段 Hook |
| `ClassNotFoundException` | 类只在特定进程加载 |
| 日志重复 | 多进程重复初始化 |

## 12. 日志迁移

旧写法：

```kotlin
XposedBridge.log("message")
XposedBridge.log(throwable)
```

新写法：

```kotlin
base.log("message")
base.log("message", throwable)
```

建议封装统一日志入口：

```kotlin
object XLog {
    private var xposed: XposedInterface? = null

    fun init(base: XposedInterface) {
        xposed = base
    }

    fun d(message: String) {
        xposed?.log("[MyModule] $message")
    }

    fun e(message: String, throwable: Throwable) {
        xposed?.log("[MyModule] $message", throwable)
    }
}
```

初始化：

```kotlin
class MainModule(
    base: XposedInterface,
    param: XposedModuleInterface.ModuleLoadedParam
) : XposedModule(base, param) {

    init {
        XLog.init(base)
    }
}
```

## 13. 配置读取迁移

传统模块常用：

```kotlin
val prefs = XSharedPreferences("your.package", "config")
prefs.reload()
```

现代 Android 版本下，跨进程文件权限和 SELinux 限制会让 `XSharedPreferences` 的可靠性下降。升级时建议重新评估配置读取方式。

| 方案 | 适用场景 | 备注 |
| --- | --- | --- |
| `SharedPreferences` 快照 | 简单配置 | 目标进程读取可能受权限限制 |
| `ContentProvider` | 需要动态读取配置 | 更清晰，但需要处理权限和进程通信 |
| 文件配置 | 少量只读配置 | 注意文件权限和原子写入 |
| 框架提供的新配置 API | 新 API 支持时 | 优先考虑 |

如果继续使用 `XSharedPreferences`，需要重点测试 Android 12 及以上版本。

## 14. 资源 Hook 迁移

传统资源 Hook 常见入口：

```kotlin
IXposedHookInitPackageResources
```

以及：

```kotlin
resparam.res.setReplacement(...)
```

升级到 API 101 时，资源 Hook 需要单独验证。很多场景可以改为 Hook 业务方法或 View 创建逻辑。

| 原资源修改类型 | 推荐迁移方向 |
| --- | --- |
| 替换字符串 | Hook 业务方法返回值 |
| 替换布局 | 尽量避免，改 Hook View 构建逻辑 |
| 修改颜色 / 尺寸 | Hook 主题、样式读取或 View 属性设置 |
| 替换 Drawable | Hook 资源读取或运行时设置逻辑 |

资源 Hook 是传统 API 迁移到现代 API 时风险较高的部分，应单独列测试用例。

## 15. 反射工具替代

传统 API 常用工具：

```kotlin
XposedHelpers.findClass(...)
XposedHelpers.findMethodExact(...)
XposedHelpers.callMethod(...)
XposedHelpers.getObjectField(...)
XposedHelpers.setObjectField(...)
```

升级后可直接使用 Java / Kotlin 反射：

```kotlin
val clazz = classLoader.loadClass("com.example.User")
val field = clazz.getDeclaredField("name").apply {
    isAccessible = true
}

val value = field.get(instance)
field.set(instance, "new name")
```

可以封装少量项目内工具方法：

```kotlin
fun ClassLoader.load(name: String): Class<*> {
    return loadClass(name)
}

fun Class<*>.declaredMethod(name: String, vararg types: Class<*>): Method {
    return getDeclaredMethod(name, *types).apply {
        isAccessible = true
    }
}

fun Class<*>.declaredField(name: String): Field {
    return getDeclaredField(name).apply {
        isAccessible = true
    }
}
```

建议只封装实际需要的能力，不要重新实现一整套 `XposedHelpers`。

## 16. R8 / ProGuard 配置

Xposed 入口类、Hooker 类和静态回调方法不能被 R8 删除或错误混淆。

推荐直接 keep Xposed 相关包：

```proguard
-keep class your.package.module.xposed.** { *; }
```

如果需要更精细的规则：

```proguard
-keep class * implements io.github.libxposed.api.XposedInterface$Hooker { *; }

-keepclassmembers class * {
    public static void before(...);
    public static void after(...);
}

-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations
```

Release 包失效时，优先检查：

- 模块入口是否被混淆。
- Hooker 类是否被删除。
- `before` / `after` 方法签名是否被改变。
- API 101 所需注解或元信息是否被移除。

## 17. Kotlin 目标方法注意事项

Hook Kotlin 编写的目标 App 时，需要注意编译产物和源码写法不一定一一对应。

常见规则：

| Kotlin 源码 | JVM 侧表现 |
| --- | --- |
| `val name: String` | `getName()` |
| `var name: String` | `getName()` / `setName(String)` |
| 默认参数 | 可能生成 `xxx$default` |
| `suspend fun` | 最后一个参数通常是 `Continuation` |
| `object` | 通过 `INSTANCE` 访问实例 |
| `companion object` | 生成 `Companion` 类或静态桥接方法 |
| 顶层函数 | 编译到 `FileNameKt` 类 |

排查方法：

```text
1. 反编译目标 APK。
2. 查看实际 JVM 类名和方法签名。
3. 确认参数类型是否为基本类型或装箱类型。
4. 确认方法是否为静态方法。
```

## 18. 防重复 Hook

多进程或热重载场景下，Hook 逻辑可能重复执行。可以用包名和进程名做最小去重。

```kotlin
private val hookedProcesses = mutableSetOf<String>()

override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
    val key = "${param.packageName}:${param.processName}"
    if (!hookedProcesses.add(key)) return

    // Register hooks.
}
```

注意：如果目标 App 存在动态加载插件或多个 ClassLoader，过早去重可能导致后续 ClassLoader 中的类没有被 Hook。此时应按具体 ClassLoader 或具体 Hook 点设计去重 key。

## 19. 常见错误排查

### 19.1 `ClassNotFoundException`

可能原因：

- 使用了模块自己的 ClassLoader。
- 目标类只在其他进程加载。
- 目标类被插件化框架延迟加载。
- 目标 App 升级后类名变化。

建议处理：

```kotlin
runCatching {
    param.classLoader.loadClass("com.example.target.SomeClass")
}.onFailure {
    base.log("Class not found", it)
}
```

### 19.2 `NoSuchMethodException`

可能原因：

- 方法名写错。
- 参数类型不匹配。
- Kotlin 默认参数或 `suspend` 改变了 JVM 签名。
- 目标 App 混淆或版本变化。

建议检查：

```text
1. 反编译目标 APK。
2. 对照真实 JVM 方法签名。
3. 确认基本类型，例如 Int::class.java 与 java.lang.Integer.TYPE。
4. 确认方法是否定义在父类。
```

### 19.3 Hook 没执行

可能原因：

- 模块没有在框架中启用。
- 作用域没有选择目标 App。
- 包名或进程名过滤错误。
- 目标方法没有在当前流程中被调用。
- Hook 注册时机太早或太晚。

### 19.4 Release 包正常安装但 Hook 失效

可能原因：

- R8 删除了 Hooker。
- 入口类被混淆。
- 回调方法签名被优化。
- Manifest 元信息被变体配置覆盖。

优先处理：

```proguard
-keep class your.package.module.xposed.** { *; }
```

## 20. 测试清单

升级完成后至少验证以下内容：

| 测试项 | 检查内容 |
| --- | --- |
| 模块识别 | 框架模块列表中可以看到模块 |
| 模块启用 | 启用后重启目标 App 生效 |
| 作用域 | 只作用于目标 App |
| 主进程 | 主进程 Hook 正常 |
| 子进程 | 不误 Hook 或按预期 Hook |
| 冷启动 | 无类找不到或时机问题 |
| 热启动 | 不重复 Hook，不重复日志 |
| 配置变更 | 配置读取符合预期 |
| Debug 包 | Hook 正常 |
| Release 包 | 混淆后 Hook 正常 |
| Android 版本 | 覆盖项目支持的最低版本和最新版本 |
| 目标 App 版本 | 覆盖当前支持的目标版本 |

## 21. 最小迁移模板

```kotlin
package your.package.module.xposed

import android.os.Bundle
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class MainModule(
    base: XposedInterface,
    param: XposedModuleInterface.ModuleLoadedParam
) : XposedModule(base, param) {

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        if (param.packageName != "com.example.target") return
        if (param.processName != "com.example.target") return

        runCatching {
            val clazz = param.classLoader.loadClass("com.example.target.MainActivity")
            val method = clazz.getDeclaredMethod("onCreate", Bundle::class.java)

            hook(method, MainActivityOnCreateHooker::class.java)
        }.onFailure {
            base.log("Failed to hook target", it)
        }
    }
}

class MainActivityOnCreateHooker : XposedInterface.Hooker {
    companion object {
        @JvmStatic
        fun before(param: XposedInterface.BeforeHookCallback) {
            param.xposed.log("before MainActivity.onCreate")
        }

        @JvmStatic
        fun after(param: XposedInterface.AfterHookCallback) {
            param.xposed.log("after MainActivity.onCreate")
        }
    }
}
```

## 22. 升级原则

迁移时建议遵守以下原则：

- 不要用模块自身 ClassLoader 加载目标 App 类。
- 不要在所有进程无差别执行 Hook。
- 不要假设目标类和方法一定存在。
- 不要忽略 release 混淆测试。
- 不要把传统 API 和现代 API 混写在同一个入口类中。
- 优先做最小可运行迁移，再逐步迁移复杂 Hook。
- 对每个 Hook 点保留清晰日志，方便定位目标 App 版本变化导致的问题。

## 23. 交付前检查表

发布升级版本前建议确认：

- Gradle 依赖已使用 API 101。
- Manifest 元信息与目标框架要求一致。
- Xposed 入口类能够被框架加载。
- 所有 Hooker 类已被 keep。
- Debug 与 Release 构建均已验证。
- 目标 App 主进程和子进程行为符合预期。
- 配置读取方式在目标 Android 版本上可靠。
- 日志中没有重复 Hook、异常吞掉或无意义刷屏。
- 已记录不再兼容的旧框架或旧 Android 版本。
