package tornaco.apps.shortx.services

import android.os.ShellCommand
import android.util.Base64
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.BuildPropInternal
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
import tornaco.apps.shortx.core.util.clipboardManager
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

    private data class IconAndText(
        val icon: String?,
        val text: String,
    )
}
