package tornaco.apps.shortx.services.input

import android.view.KeyEvent
import com.android.internal.view.IInputContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.clipboard.ClipboardItem
import tornaco.apps.shortx.core.proto.fact.IMEVisibilityChange
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.android.ServiceRetriever
import tornaco.apps.shortx.services.data.ClipboardStore
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.watchdog.postSafety
import util.Reflections
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

class InputService(shortXContext: ShortXContext) : SystemService(shortXContext, "InputService") {
    private val imeService by lazy {
        ServiceRetriever(context.classLoader).getInputMethodManagerService()
    }

    private val _onClipboardListener = mutableListOf<(String) -> Unit>()

    private val isIMEShown = AtomicBoolean(false)
    private var hasEverReceivedInputFilterCallbackBeforeInstallIF = false
    private var isInputFilterInstallCalled = false

    private val inputFilterInstaller by lazy {
        InputFilterInstaller(ShortXService)
    }

    private val clipboardStore by lazy {
        ClipboardStore(
            scope = systemServiceScope,
            encKey = requireNotNull(
                store.read(
                    Settings.StringSettings.ServiceIns
                )
            ) {
                "ServiceIns is null"
            })
    }

    // Code-Time
    private val _lastInputKey =
        MutableStateFlow(KeyEvent.KEYCODE_UNKNOWN to System.currentTimeMillis())
    val lastInputKey get() = _lastInputKey.value

    fun updateLastInputKey(keycode: Int, source: String) {
        logger.d("updateLastInputKey- $keycode $source")
        _lastInputKey.update { keycode to System.currentTimeMillis() }
    }

    override fun systemReady() {
        super.systemReady()
        // Install ServiceIns store
        if (store.read(Settings.StringSettings.ServiceIns).isNullOrBlank()) {
            store.write(Settings.StringSettings.ServiceIns, UUID.randomUUID().toString())
            logger.i("Service Ins installed.")
        }

        clipboardStore.readBlocking()

        context.clipboardManager.apply {
            addPrimaryClipChangedListener {
                val text = primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
                logger.d("ClipChangedListener, onPrimaryClipChanged, text: $text")
                onClipboardChange(text)
            }
        }
    }

    override fun userPresent(isFirstTime: Boolean) {
        super.userPresent(isFirstTime)
        logger.w("userPresent. isFirstTime- $isFirstTime")
        if (isFirstTime) {
            systemServiceScope.launchSafety("InputService.userPresent") {
                delay(2000)
                checkInstallInputFilter()
            }
        }
    }

    private var onInputFilterStateChangedHandleTimes = 0
    fun onInputFilterStateChanged(isEnabled: Boolean) {
        logger.w("onInputFilterStateChanged: $isEnabled")
        // Some how, input filter was disabled....
        // 2024-12-22 08:37:43.350 W/ShortX-Core: ShortXHook.InputManagerHook [main] setInputFilterEnabled: [false] java.lang.Throwable
        //	at tornaco.apps.shortx.services.xposed.hooks.hook.InputManagerHook.debugHooks_IoAF18A$lambda$13$lambda$12(Unknown Source:62)
        //	at tornaco.apps.shortx.services.xposed.hooks.hook.InputManagerHook.d(Unknown Source:0)
        //	at fxewqk.bzpmjm.nwagnr.kY0.m(Unknown Source:2)
        //	at tornaco.apps.shortx.services.xposed.hooks.hook.HooksKt$afterMethod$1.afterHookedMethod(Unknown Source:11)
        //	at de.robv.android.xposed.XposedBridge$LegacyApiSupport.handleAfter(Unknown Source:33)
        //	at org.lsposed.lspd.impl.LSPosedBridge$NativeHooker.callback(Unknown Source:279)
        //	at LSPHooker_.setInputFilterEnabled(Unknown Source:15)
        //	at com.android.server.input.InputManagerService.setInputFilter(InputManagerService.java:884)
        //	at java.lang.reflect.Method.invoke(Native Method)
        //	at org.lsposed.lspd.nativebridge.HookBridge.invokeOriginalMethod(Native Method)
        //	at org.lsposed.lspd.impl.LSPosedBridge$NativeHooker.callback(Unknown Source:187)
        //	at LSPHooker_.setInputFilter(Unknown Source:11)
        //	at com.android.server.wm.WindowManagerService$LocalService.setInputFilter(WindowManagerService.java:8762)
        //	at com.android.server.accessibility.AccessibilityManagerService.updateInputFilter(AccessibilityManagerService.java:3023)
        //	at com.android.server.accessibility.AccessibilityManagerService.$r8$lambda$c2sb4fePdTUn6QD12AOuX4CLSEw(Unknown Source:0)
        //	at com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda76.accept(D8$$SyntheticClass:0)
        //	at com.android.internal.util.function.pooled.PooledLambdaImpl.doInvoke(PooledLambdaImpl.java:281)
        //	at com.android.internal.util.function.pooled.PooledLambdaImpl.invoke(PooledLambdaImpl.java:204)
        //	at com.android.internal.util.function.pooled.OmniFunction.run(OmniFunction.java:87)
        //	at android.os.Handler.handleCallback(Handler.java:959)
        //	at android.os.Handler.dispatchMessage(Handler.java:100)
        //	at android.os.Looper.loopOnce(Looper.java:249)
        //	at android.os.Looper.loop(Looper.java:337)
        //	at com.android.server.SystemServer.run(SystemServer.java:1081)
        //	at com.android.server.SystemServer.main(SystemServer.java:727)
        //	at java.lang.reflect.Method.invoke(Native Method)
        //	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:593)
        //	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:913)
        logger.w("onInputFilterStateChangedHandleTimes: $onInputFilterStateChangedHandleTimes")
        if (!isEnabled) {
            if (onInputFilterStateChangedHandleTimes > 100) {
                shortXContext.watchDog.onFatalError(
                    Throwable(),
                    "Too many times: onInputFilterStateChangedHandleTimes"
                )
                return
            }
            onInputFilterStateChangedHandleTimes += 1
            systemServiceScope.launchSafety("onInputFilterStateChanged") {
                checkInstallInputFilter()
            }
        }
    }

    private fun checkInstallInputFilter() {
        logger.w("checkInstallInputFilter: hasEverReceivedInputFilterCallbackBeforeInstallIF = $hasEverReceivedInputFilterCallbackBeforeInstallIF")
        logger.w("checkInstallInputFilter: isInputFilterInstallCalled = $isInputFilterInstallCalled")
        if (!hasEverReceivedInputFilterCallbackBeforeInstallIF) {
            inputFilterInstaller.installInputFilter()
            isInputFilterInstallCalled = true
        } else {
            logger.w("Won't install InputFilter since hasEverReceivedInputFilterCallback is true")
        }
    }

    fun setHasReceivedInputFilterCallback() {
        if (!isInputFilterInstallCalled && !hasEverReceivedInputFilterCallbackBeforeInstallIF) {
            logger.w("setHasReceivedInputFilterCallback")
            hasEverReceivedInputFilterCallbackBeforeInstallIF = true
        }
    }

    fun addClipboardText(string: String) {
        logger.logOnDebug("addClipboardText: $string")
        string.takeIf { it.isNotBlank() }?.let {
            clipboardStore.addText(
                ClipboardItem.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setValue(string)
                    .setCreateAt(System.currentTimeMillis())
                    .build()
            )
        }
    }

    fun addClipboardText(text: ClipboardItem) {
        logger.logOnDebug("addClipboardText: $text")
        clipboardStore.addText(text)
    }

    fun removeClipboardText(id: String) {
        logger.logOnDebug("removeClipboardText: $id")
        clipboardStore.deleteText(id)
    }

    fun getAllClipboardText() = clipboardStore.getAllText()

    fun clearAllClipboardText() {
        logger.w("clearAllClipboardText")
        clipboardStore.clearAllText()
    }

    fun addOnClipboardListener(listener: (String) -> Unit) {
        _onClipboardListener.add(listener)
    }

    fun onClipboardChange(text: String) {
        postSafety("onClipboardChange") {
            _onClipboardListener.forEach {
                it(text)
            }
        }
    }

    fun performContextMenuAction(action: Int) {
        if (OsUtils.isUOrAbove()) {
            performContextMenuActionAndroid14(action)
        } else {
            performContextMenuActionAndroid14Below(action)
        }
    }

    private fun performContextMenuActionAndroid14Below(action: Int) {
        logger.d("performContextMenuActionAndroid14Below, imeService: $imeService")
        val mCurInputContext =
            Reflections.getObjectField(imeService, "mCurInputContext")
        logger.d("performContextMenuActionAndroid14Below, mCurInputContext: $mCurInputContext")
        val inputContext: IInputContext =
            requireNotNull(mCurInputContext) { "No Input context. (no focused TextField?)" } as IInputContext
        logger.d("performContextMenuActionAndroid14Below, inputContext: $inputContext")
        InputContextCompat.performContextMenuAction(inputContext, action)
    }

    private fun performContextMenuActionAndroid14(action: Int) {
        logger.d("performContextMenuActionAndroid14Below, imeService: $imeService")
        val mCurInputConnection =
            Reflections.getObjectField(imeService, "mCurInputConnection")
        logger.d("performContextMenuActionAndroid14Below, mCurInputConnection: $mCurInputConnection")
        val inputConnection =
            requireNotNull(mCurInputConnection) { "No Input context. (no focused TextField?)" }
        logger.d("performContextMenuActionAndroid14Below, inputConnection: $inputConnection")
        InputConnectionCompat.performContextMenuAction(inputConnection, action)
    }

    fun submitText(text: String) {
        if (OsUtils.isUOrAbove()) {
            submitTextAndroid14(text)
        } else {
            submitTextAndroid14Below(text)
        }
    }

    private fun submitTextAndroid14Below(text: String) {
        logger.d("submitText, imeService: $imeService")
        val mCurInputContext =
            Reflections.getObjectField(imeService, "mCurInputContext")
        logger.d("submitText, mCurInputContext: $mCurInputContext")
        val inputContext: IInputContext =
            requireNotNull(mCurInputContext) { "No Input context. (no focused TextField?)" } as IInputContext
        logger.d("submitText, inputContext: $inputContext")
        InputContextCompat.commitText(inputContext, text)
    }

    // IRemoteInputConnection mCurInputConnection;
    // https://github.com/ProjectMatrixx/android_frameworks_base/commit/7165fae4a959efa5023f4a66d23e53de65b3ed02
    private fun submitTextAndroid14(text: String) {
        logger.d("submitText, imeService: $imeService")
        val mCurInputConnection =
            Reflections.getObjectField(imeService, "mCurInputConnection")
        logger.d("submitText, mCurInputConnection: $mCurInputConnection")
        val inputConnection =
            requireNotNull(mCurInputConnection) { "No Input context. (no focused TextField?)" }
        logger.d("submitText, inputConnection: $inputConnection")
        InputConnectionCompat.commitText(inputConnection, text)
    }

    fun onHideCurrentInput() {
        if (isIMEShown.compareAndSet(true, false)) {
            imeVisibilityChanged()
        }
    }

    fun onShowCurrentInput() {
        if (isIMEShown.compareAndSet(false, true)) {
            imeVisibilityChanged()
        }
    }

    fun isIMEShown() = isIMEShown.get()

    private fun imeVisibilityChanged() {
        logger.d("imeVisibilityChanged, isIMEShown? ${isIMEShown.get()}")
        shortXContext.rule.publishFactChecked(IMEVisibilityChange.getDefaultInstance()) {
            FactPublishData(
                reason = "imeVisibilityChanged",
                fact = IMEVisibilityChange.newBuilder().setIsShown(isIMEShown.get()).build()
            )
        }
    }
}