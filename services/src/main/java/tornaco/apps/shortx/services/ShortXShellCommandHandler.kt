package tornaco.apps.shortx.services

import android.app.ActivityManager
import android.content.pm.ApplicationInfo
import android.os.ShellCommand
import android.util.Base64
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.common.AppCategory
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.action.InputText
import tornaco.apps.shortx.core.proto.action.ShowDanmu
import tornaco.apps.shortx.core.proto.action.ShowToast
import tornaco.apps.shortx.core.proto.action.WriteClipboard
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.toLogString
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.core.util.activityManager
import tornaco.apps.shortx.services.rule.nextEvaluateContextSerialNumber
import java.io.PrintWriter

internal class ShortXShellCommandHandler(
    private val service: SystemService,
) {
    private val shortXContext = service.shortXContext
    private val context = service.context
    private val logger = service.logger

    fun onCommand(shellCommand: ShellCommand): Int {
        service.enforceCallingPermissions()

        val pw = shellCommand.outPrintWriter
        val nextArgs: String? = shellCommand.nextArg
        if (nextArgs == null) {
            printHelp(pw)
            return 0
        } else if (nextArgs == "run") {
            return runDirectAction(shellCommand, pw)
        } else if (nextArgs == "toast") {
            val text = shellCommand.readRemainingArgsAsText()
            if (text.isBlank()) {
                pw.println("Missing toast text")
                pw.flush()
                return 1
            }
            return runActionCommand(
                pw = pw,
                actionName = "toast",
                action = ShowToast.newBuilder()
                    .setId("Shell#toast")
                    .setMessage(text)
                    .build()
                    .pack_()
            )
        } else if (nextArgs == "danmu") {
            val iconAndText = shellCommand.readOptionalIconAndRemainingText()
            if (iconAndText.text.isBlank()) {
                pw.println("Missing danmu text")
                pw.flush()
                return 1
            }
            return runActionCommand(
                pw = pw,
                actionName = "danmu",
                action = ShowDanmu.newBuilder()
                    .setId("Shell#danmu")
                    .setText(iconAndText.text)
                    .apply {
                        iconAndText.icon?.let { setIcon(it) }
                    }
                    .build()
                    .pack_()
            )
        } else if (nextArgs == "clip") {
            val text = shellCommand.readRemainingArgsAsText()
            if (text.isBlank()) {
                pw.println("Missing clipboard text")
                pw.flush()
                return 1
            }
            return runActionCommand(
                pw = pw,
                actionName = "clip",
                action = WriteClipboard.newBuilder()
                    .setId("Shell#clip")
                    .setText(text)
                    .setNote("shell")
                    .build()
                    .pack_()
            )
        } else if (nextArgs == "text") {
            val text = shellCommand.readRemainingArgsAsText()
            if (text.isBlank()) {
                pw.println("Missing input text")
                pw.flush()
                return 1
            }
            return runActionCommand(
                pw = pw,
                actionName = "text",
                action = InputText.newBuilder()
                    .setId("Shell#text")
                    .setText(text)
                    .build()
                    .pack_()
            )
        } else if (nextArgs == "getclip") {
            return printClipboardText(pw)
        } else if (nextArgs == "current-app") {
            return printCurrentApp(shellCommand, pw)
        } else if (nextArgs == "clear") {
            return clearCommand(shellCommand, pw)
        } else if (nextArgs == "kill-current-app") {
            return killCurrentApp(shellCommand, pw)
        } else if (nextArgs == "apkpath") {
            return printApkPath(shellCommand, pw)
        } else if (nextArgs == "action-b64") {
            return runBase64Action(shellCommand, pw)
        } else if (nextArgs == "dump") {
            pw.println("Running actions:")
            shortXContext.rule.getEvaluatingActions().forEach {
                pw.println(it)
            }
            pw.flush()
            return 0
        }

        return 1
    }

    private fun printHelp(pw: PrintWriter) {
        pw.println("Welcome to ShortX(${BuildPropInternal.APP_VERSION_NAME}). Command usage:")
        pw.println("am shortx run [ID] [arg1]=[value] [arg2]=[value] ...")
        pw.println("Example: am shortx run 7da392dxfjysl size=24 color=#ffffff")
        pw.println("      Run a Direct Action.")
        pw.println("am shortx toast [TEXT]")
        pw.println("      Show a toast.")
        pw.println("am shortx danmu [--icon ICON] [TEXT]")
        pw.println("      Show a danmu overlay.")
        pw.println("am shortx clip [TEXT]")
        pw.println("      Write text to clipboard.")
        pw.println("am shortx text [TEXT]")
        pw.println("      Input text to the current focused field.")
        pw.println("am shortx getclip")
        pw.println("      Print clipboard text to stdout.")
        pw.println("am shortx current-app [-app PKG] [-a|-s|-3]")
        pw.println("      Print app info as 包名#名称#用户#uid.")
        pw.println("am shortx clear recents-app [PKG USER_ID]")
        pw.println("      Clear all recents app cards or the specified one.")
        pw.println("am shortx kill-current-app [PKG USER_ID]")
        pw.println("      Kill current foreground app or the specified app.")
        pw.println("am shortx apkpath [PKG]")
        pw.println("      Print current foreground app apk path or the specified package path.")
        pw.println("am shortx action-b64 [BASE64_PROTO_ANY]")
        pw.println("      Execute any packed ProtoAny action.")
        pw.println("am shortx dump")
        pw.println("      Dump ShortX states.")
        pw.flush()
    }

    private fun runDirectAction(shellCommand: ShellCommand, pw: PrintWriter): Int {
        val id: String? = shellCommand.nextArg
        if (id == null) {
            pw.println("Missing id argument")
            pw.flush()
            return 1
        }

        pw.println("Checking id: $id")
        val da = shortXContext.rule.getDirectActionById(id)
        if (da == null) {
            pw.println("Rule not exists.")
            pw.flush()
            return 1
        }
        pw.println("About to run: ${da.title}")
        pw.flush()

        val args = mutableListOf<FuncParameterInput>()
        var nextArgParam = shellCommand.nextArg
        while (nextArgParam != null) {
            val kv = nextArgParam.split("=")
            args.add(FuncParameterInput.newBuilder().setName(kv[0]).setValue(kv[1]).build())
            nextArgParam = shellCommand.nextArg
        }
        pw.println("args: ${args.map { it.toLogString() }}")

        shortXContext.rule.executeDirectionActionById(
            context = EvaluateContext.newBuilder()
                .setSrcId(id)
                .setSrcTitle("Shell command")
                .setSrcEvent(ContextSrcEvent.ContextSrc_DirectAction)
                .setSerialNumber(nextEvaluateContextSerialNumber())
                .build(),
            id = id,
            parameterInputs = args
        )
        pw.flush()
        return 0
    }

    private fun printClipboardText(pw: PrintWriter): Int {
        var result = 1
        runBlocking {
            service.systemServiceScope.launch {
                result = kotlin.runCatching {
                    val text = context.clipboardManager.primaryClip
                        ?.getItemAt(0)
                        ?.text
                        ?.toString()
                        .orEmpty()
                    pw.print(text)
                    pw.flush()
                    0
                }.getOrElse {
                    logger.e(it, "shell getclip")
                    pw.println("Failed to read clipboard: ${it.message}")
                    pw.flush()
                    1
                }
            }.join()
        }
        return result
    }

    private fun printCurrentApp(shellCommand: ShellCommand, pw: PrintWriter): Int {
        val installedApps = shortXContext.appState.getInstalledApps()
        val apps = when (val query = parseCurrentAppQuery(shellCommand, pw)) {
            null -> return 1
            is CurrentAppQuery.Current -> shortXContext.appState.currentFrontPkgs()
            is CurrentAppQuery.ByPkg -> installedApps
                .filter { it.pkg.pkgName == query.pkgName }
                .map { it.pkg }
            is CurrentAppQuery.ByCategory -> installedApps
                .filter { query.category == null || it.category == query.category }
                .map { it.pkg }
        }

        if (apps.isEmpty()) {
            pw.println("No app found.")
            pw.flush()
            return 1
        }

        apps.distinct().forEach { appPkg ->
            pw.println(formatAppPkg(appPkg))
        }
        pw.flush()
        return 0
    }

    private fun clearCommand(shellCommand: ShellCommand, pw: PrintWriter): Int {
        return when (shellCommand.nextArg) {
            "recents-app" -> clearRecentsApp(shellCommand, pw)
            null -> {
                pw.println("Missing clear target")
                pw.flush()
                1
            }

            else -> {
                pw.println("Unknown clear target")
                pw.flush()
                1
            }
        }
    }

    private fun clearRecentsApp(shellCommand: ShellCommand, pw: PrintWriter): Int {
        val pkgName = shellCommand.nextArg
        if (pkgName == null) {
            val tasks = context.activityManager.getRecentTasks(
                200,
                ActivityManager.RECENT_WITH_EXCLUDED
            )
            val activityManager = android.app.IActivityManager.Stub.asInterface(
                android.os.ServiceManager.getService(android.content.Context.ACTIVITY_SERVICE)
            )
            tasks.orEmpty().forEach { task ->
                val taskId = try {
                    if (OsUtils.isQOrAbove()) task.taskId else task.persistentId
                } catch (_: Throwable) {
                    0
                }
                if (taskId > 0) {
                    activityManager.removeTask(taskId)
                }
            }
            pw.println("OK")
            pw.flush()
            return 0
        }

        val userId = shellCommand.nextArg?.toIntOrNull()
        if (userId == null) {
            pw.println("Missing userId")
            pw.flush()
            return 1
        }
        val appPkg = buildAppPkg(pkgName, userId)
        shortXContext.appState.removeTasksForPackage(listOf(appPkg))
        pw.println(formatAppPkg(appPkg))
        pw.flush()
        return 0
    }

    private fun killCurrentApp(shellCommand: ShellCommand, pw: PrintWriter): Int {
        val pkgName = shellCommand.nextArg
        if (pkgName.isNullOrBlank()) {
            val currentApps = shortXContext.appState.currentFrontPkgs()
            if (currentApps.isEmpty()) {
                pw.println("No current foreground app.")
                pw.flush()
                return 1
            }
            currentApps.forEach { appPkg ->
                pw.println(formatAppPkg(appPkg))
            }
            pw.flush()
            shortXContext.appState.stopForegroundApp()
            return 0
        }

        val userId = shellCommand.nextArg?.toIntOrNull()
        if (userId == null) {
            pw.println("Missing userId")
            pw.flush()
            return 1
        }
        val appPkg = buildAppPkg(pkgName, userId)
        shortXContext.appState.forceStopApp(appPkg, "shell kill-current-app")
        pw.println(formatAppPkg(appPkg))
        pw.flush()
        return 0
    }

    private fun printApkPath(shellCommand: ShellCommand, pw: PrintWriter): Int {
        val pkgName = shellCommand.nextArg
        val targets = if (pkgName.isNullOrBlank()) {
            shortXContext.appState.currentFrontPkgs()
        } else {
            shortXContext.appState.getInstalledApps()
                .filter { it.pkg.pkgName == pkgName }
                .map { it.pkg }
                .distinct()
        }

        if (targets.isEmpty()) {
            pw.println(if (pkgName.isNullOrBlank()) "No current foreground app." else "App not found.")
            pw.flush()
            return 1
        }

        targets.forEach { appPkg ->
            val path = getApkPath(appPkg)
            if (path != null) {
                pw.println(path)
            }
        }
        pw.flush()
        return 0
    }

    private fun runBase64Action(shellCommand: ShellCommand, pw: PrintWriter): Int {
        val base64 = shellCommand.nextArg
        if (base64.isNullOrBlank()) {
            pw.println("Missing base64 action payload")
            pw.flush()
            return 1
        }
        return kotlin.runCatching {
            val action = ProtoAny.parseFrom(Base64.decode(base64, Base64.DEFAULT))
            runActionCommand(
                pw = pw,
                actionName = "action-b64",
                action = action,
                logSuccess = false
            )
        }.getOrElse {
            logger.e(it, "shell action-b64")
            pw.println("Invalid action payload: ${it.message}")
            pw.flush()
            1
        }
    }

    private fun runActionCommand(
        pw: PrintWriter,
        actionName: String,
        action: ProtoAny,
        logSuccess: Boolean = true,
    ): Int {
        var result = 1
        runBlocking {
            service.systemServiceScope.launch {
                result = kotlin.runCatching {
                    shortXContext.rule.executeActionsBlocking(
                        context = EvaluateContext.newBuilder()
                            .setSrcId("Shell#$actionName#${nextEvaluateContextSerialNumber()}")
                            .setSrcTitle("Shell command")
                            .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                            .setSerialNumber(nextEvaluateContextSerialNumber())
                            .build(),
                        actions = listOf(action)
                    )
                    if (logSuccess) {
                        pw.println("OK")
                    }
                    pw.flush()
                    0
                }.getOrElse {
                    logger.e(it, "runActionCommand: $actionName")
                    pw.println("Command failed: ${it.message}")
                    pw.flush()
                    1
                }
            }
        }
        return result
    }

    private fun ShellCommand.readRemainingArgsAsText(): String {
        val parts = mutableListOf<String>()
        var arg = nextArg
        while (arg != null) {
            parts.add(arg)
            arg = nextArg
        }
        return parts.joinToString(" ").trim()
    }

    private fun ShellCommand.readOptionalIconAndRemainingText(): IconAndText {
        var icon: String? = null
        val parts = mutableListOf<String>()
        var arg = nextArg
        while (arg != null) {
            if (arg == "--icon") {
                icon = nextArg
            } else {
                parts.add(arg)
            }
            arg = nextArg
        }
        return IconAndText(icon = icon, text = parts.joinToString(" ").trim())
    }

    private fun parseCurrentAppQuery(
        shellCommand: ShellCommand,
        pw: PrintWriter,
    ): CurrentAppQuery? {
        val firstArg = shellCommand.nextArg ?: return CurrentAppQuery.Current
        return when (firstArg) {
            "-a" -> CurrentAppQuery.ByCategory(null)
            "-s" -> CurrentAppQuery.ByCategory(AppCategory.System)
            "-3" -> CurrentAppQuery.ByCategory(AppCategory.User)
            "-app" -> {
                val pkgName = shellCommand.nextArg
                if (pkgName.isNullOrBlank()) {
                    pw.println("Missing package name for -app")
                    pw.flush()
                    null
                } else {
                    CurrentAppQuery.ByPkg(pkgName)
                }
            }

            else -> {
                pw.println("Unknown current-app option: $firstArg")
                pw.flush()
                null
            }
        }
    }

    private fun formatAppPkg(appPkg: AppPkg): String {
        val label = shortXContext.appState.getAppLabel(appPkg).orEmpty()
        val uid = shortXContext.appState.pkgToApp(appPkg)?.uid
            ?: PkgUtils.getApplicationInfoAsUser(context, appPkg.pkgName, appPkg.userId)?.uid
            ?: -1
        return "${appPkg.pkgName}#$label#${appPkg.userId}#$uid"
    }

    private fun buildAppPkg(pkgName: String, userId: Int): AppPkg {
        return AppPkg.newBuilder()
            .setPkgName(pkgName)
            .setUserId(userId)
            .build()
    }

    private fun getApkPath(appPkg: AppPkg): String? {
        val appInfo: ApplicationInfo? = PkgUtils.getApplicationInfoAsUser(
            context,
            appPkg.pkgName,
            appPkg.userId
        )
        return appInfo?.publicSourceDir
    }

    private sealed interface CurrentAppQuery {
        data object Current : CurrentAppQuery
        data class ByPkg(val pkgName: String) : CurrentAppQuery
        data class ByCategory(val category: AppCategory?) : CurrentAppQuery
    }

    private data class IconAndText(
        val icon: String?,
        val text: String,
    )
}
