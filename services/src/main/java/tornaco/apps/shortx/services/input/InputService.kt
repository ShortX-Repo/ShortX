package tornaco.apps.shortx.services.input

import android.os.SystemClock
import android.view.KeyEvent
import android.view.WindowManagerPolicyConstants.FLAG_INTERACTIVE
import com.android.internal.view.IInputContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.input.InternalInputFlags.FLAG_SHORTX_INJECTION
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.clipboard.ClipboardItem
import tornaco.apps.shortx.core.proto.fact.IMEVisibilityChange
import tornaco.apps.shortx.core.proto.feature.AppFeature
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.android.ServiceHolder
import tornaco.apps.shortx.services.android.ServiceRetriever
import tornaco.apps.shortx.services.data.ClipboardStore
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.systemServiceScope
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
                // logger.d("ClipChangedListener, onPrimaryClipChanged, text: $text")
                onClipboardChange(text)
            }
        }

        // Test dex maker.
        BuildPropInternal.APP_IS_DEBUG.apply {
            if (this) {
                val ruleIns = SingleKeyRuleInsMaker.make()
                ruleIns?.let {
                    Reflections.callMethod(ServiceHolder.singleKeyGestureDetector, "addRule", it)
                    logger.w("singleKeyGestureDetector rule added.")
                }
            }
        }
    }

    override fun userPresent(isFirstTime: Boolean) {
        super.userPresent(isFirstTime)
        logger.w("userPresent. isFirstTime- $isFirstTime")
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

    // https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/input/InputManagerService.java;l=2504?q=interceptKeyBeforeQueueing&ss=android
    fun interceptKeyBeforeQueueing(event: KeyEvent, policyFlags: Int): Int? {
        if (!shortXContext.rule.isRuleFeatureEnabled()) return null
        val isEdgeGestureDisabled =
            !(ShortXService.serviceBinder.isAppFeatureEnabled(AppFeature.AppFeature_KeyEvent.number)
                .also {
                    logger.d("isAppFeatureEnabled-AppFeature_KeyEvent: $it")
                })
        if (isEdgeGestureDisabled) return null

        val interactive = (policyFlags and FLAG_INTERACTIVE) != 0
        if ((event.flags and KeyEvent.FLAG_FALLBACK) == 0) {
            // Pass interactive to true, to prevent un-expected KeyEvent inject issue on screen-off state.
            val res =
                ShortXService.shortXContext.gesture.interceptKeyBeforeQueueing(event, true)
            logger.logOnDebug {
                "interceptKeyBeforeQueueing, interactive: $interactive, res: $res ${
                    KeyCodeUtil.keyEventToCodeAndActionString(
                        event
                    )
                }"
            }
            val isWakeupKey = !interactive && event.keyCode == KeyEvent.KEYCODE_POWER
            if (isWakeupKey) {
                logger.w("isWakeupKey is true.")
            }
            if (res == true && !isWakeupKey) {
                logger.w("XXX block $event")
                // If key gesture gets triggered, we send the event to policy with KEY_GESTURE flag
                // indicating, the event is used in triggering a key gesture. We can't block event
                // like Power or volume keys since policy might still want to handle it to change
                // certain states.
                // val policyFlagsNew =
                //    policyFlags or WindowManagerPolicyConstants.FLAG_KEY_GESTURE_TRIGGERED
                return 0
            }
        }
        return null
    }

    fun interceptKeyBeforeDispatching(event: KeyEvent): Long? {
        if (!shortXContext.rule.isRuleFeatureEnabled()) return null
        val isEdgeGestureDisabled =
            !(ShortXService.serviceBinder.isAppFeatureEnabled(AppFeature.AppFeature_KeyEvent.number)
                .also {
                    logger.d("interceptKeyBeforeDispatching isAppFeatureEnabled-AppFeature_KeyEvent: $it")
                })
        if (isEdgeGestureDisabled) return null

        // Check if event accessibility service.
        if ((event.flags and FLAG_SHORTX_INJECTION) != 0) {
            logger.logOnDebug("interceptKeyBeforeDispatching InternalInputFlags.FLAG_SHORTX_INJECTION event: $event")
            return null
        }

        val keyCode = event.keyCode
        val flags = event.flags
        val keyConsumed = -1L
        val keyNotConsumed = 0L

        if (shortXContext.rule.keyGestureController.keyCombinationTrigger.isKeyConsumed(event)
        ) {
            logger.w("interceptKeyBeforeDispatching keyCombinationTrigger keyConsumed is true: $event")
            return keyConsumed
        }

        if ((flags and KeyEvent.FLAG_FALLBACK) == 0) {
            val now = SystemClock.uptimeMillis()
            val interceptTimeout: Long =
                shortXContext.rule.keyGestureController.keyCombinationTrigger.getKeyInterceptTimeout(
                    keyCode
                )
            if (now < interceptTimeout) {
                logger.w("interceptKeyBeforeDispatching now < interceptTimeout is true: $event")
                return interceptTimeout - now
            }
        }
        return null
    }
}