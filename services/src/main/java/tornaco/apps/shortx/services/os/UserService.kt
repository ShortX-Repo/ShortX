package tornaco.apps.shortx.services.os

import android.Manifest
import android.annotation.SuppressLint
import android.appwidget.AppWidgetProviderInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.os.UserHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.common.IntOp
import tornaco.apps.shortx.core.proto.condition.TheXXTimeTodayScope
import tornaco.apps.shortx.core.proto.fact.ScreenOnTime
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.userManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.rule.cond.impl.matchIntOp
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.watchdog.launchSafety
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

sealed interface UserEvent {
    data class Added(val userId: Int) : UserEvent
    data class Removed(val userId: Int) : UserEvent
}

class UserService(shortXContext: ShortXContext) : SystemService(shortXContext, "UserService") {
    private val _userEvent = MutableSharedFlow<UserEvent>()
    val userEvent = _userEvent.asSharedFlow()

    private val _systemReadyTriggered = AtomicBoolean(false)
    val isSystemReadyTriggered get() = _systemReadyTriggered.get()

    val requestPinShortcuts = mutableListOf<ShortcutInfo>()
    val requestPinAppWidgets = mutableListOf<AppWidgetProviderInfo>()

    val screenOnTimeFromSystemReadyInSeconds = AtomicLong(0L)
    val screenOnTimeFromLastScreenOffInSeconds = AtomicLong(0L)
    private var screenTimeCalculatorJob: Job? = null

    private val userBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context == null || intent == null) return
            logger.w("User event: ${intent.action}")
            if (Intent.ACTION_USER_ADDED == intent.action) {
                val userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, UserHandle.USER_NULL)
                if (userId == UserHandle.USER_NULL) {
                    logger.e("UserManagerService $intent sent to us without EXTRA_USER_HANDLE")
                    return
                }
                onUserAdded(userId)
            }
            if (Intent.ACTION_USER_REMOVED == intent.action) {
                val userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, UserHandle.USER_NULL)
                if (userId == UserHandle.USER_NULL) {
                    logger.e("UserManagerService $intent sent to us without EXTRA_USER_HANDLE")
                    return
                }
                onUserRemoved(userId)
            }
        }

    }

    private fun onUserRemoved(userId: Int) {
        systemServiceScope.launchSafety("onUserRemoved") {
            _userEvent.emit(UserEvent.Removed(userId))
        }
    }

    private fun onUserAdded(userId: Int) {
        systemServiceScope.launchSafety("onUserAdded") {
            _userEvent.emit(UserEvent.Added(userId))
        }
    }

    @SuppressLint("MissingPermission")
    fun getUsers(): List<android.content.pm.UserInfo> {
        val hasManageUserPerm =
            context.checkCallingOrSelfPermission(Manifest.permission.MANAGE_USERS) == PackageManager.PERMISSION_GRANTED
        logger.w("getUsers, hasManageUserPerm? $hasManageUserPerm")
        val um = context.userManager
        return if (hasManageUserPerm) {
            um.getUsers(true) ?: emptyList()
        } else {
            listOf(um.getUserInfo(UserHandle.USER_SYSTEM))
        }
    }

    override fun systemReady() {
        super.systemReady()
        registerReceivers()
    }

    override fun shutdown() {
        super.shutdown()
        context.unregisterReceiver(userBroadcastReceiver)
    }

    override fun screenOn() {
        super.screenOn()
        startScreenTimeJob()
    }

    private fun startScreenTimeJob() {
        logger.d("startScreenTimeJob")
        resetScreenOnTimeJob()
        screenTimeCalculatorJob = systemServiceScope.launch {
            while (isActive) {
                delay(1000)
                screenOnTimeFromLastScreenOffInSeconds.incrementAndGet()
                screenOnTimeFromSystemReadyInSeconds.incrementAndGet()
                logger.v { "Screen on time +1 total: SystemReady-${screenOnTimeFromSystemReadyInSeconds.get()} ScreenOn-${screenOnTimeFromLastScreenOffInSeconds.get()}" }

                // Publish
                shortXContext.rule.publishFactChecked(
                    factDefaultInstance = ScreenOnTime.getDefaultInstance()
                ) {
                    FactPublishData(
                        reason = "Screen on time",
                        fact = ScreenOnTime.newBuilder()
                            .setPublishDataFromScreenOffTime(screenOnTimeFromLastScreenOffInSeconds.get())
                            .setPublishDataFromSystemReadyTime(screenOnTimeFromSystemReadyInSeconds.get())
                            .build()
                    )
                }
            }
        }
    }

    override fun userPresent(isFirstTime: Boolean) {
        super.userPresent(isFirstTime)
        if (isFirstTime) {
            val job = screenTimeCalculatorJob
            if (job == null || !job.isActive) {
                logger.w("userPresent, screenTimeCalculatorJob is not running, will start.")
                startScreenTimeJob()
            }
        }
    }

    override fun screenOff() {
        super.screenOff()
        resetScreenOnTimeJob()
    }

    private fun resetScreenOnTimeJob() {
        screenTimeCalculatorJob?.cancel()
        screenTimeCalculatorJob = null
        screenOnTimeFromLastScreenOffInSeconds.set(0)
        logger.v { "Screen on time reset." }
    }

    private fun registerReceivers() {
        val userFilter = IntentFilter()
        userFilter.addAction(Intent.ACTION_USER_INITIALIZE)
        userFilter.addAction(Intent.ACTION_USER_FOREGROUND)
        userFilter.addAction(Intent.ACTION_USER_BACKGROUND)
        userFilter.addAction(Intent.ACTION_USER_ADDED)
        userFilter.addAction(Intent.ACTION_USER_STARTED)
        userFilter.addAction(Intent.ACTION_USER_STARTING)
        userFilter.addAction(Intent.ACTION_USER_STOPPING)
        userFilter.addAction(Intent.ACTION_USER_STOPPED)
        userFilter.addAction(Intent.ACTION_USER_REMOVED)
        userFilter.addAction(Intent.ACTION_USER_SWITCHED)
        if (OsUtils.isNOrAbove()) {
            userFilter.addAction(Intent.ACTION_USER_UNLOCKED)
        }
        userFilter.addAction(Intent.ACTION_USER_INFO_CHANGED)

        ContextCompat.registerReceiver(
            context,
            userBroadcastReceiver,
            userFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    fun publishSystemReady() {
        logger.i("publishSystemReady?")
        if (!_systemReadyTriggered.get()) {
            logger.i("publishSystemReady in.")

            shortXContext.rule.publishFact(
                reason = "SystemReady",
                fact = SystemReady.newBuilder().build(),
                initialContextDataLazy = { emptyMap() }
            )

            _systemReadyTriggered.set(true)
        }
    }

    fun isSomethingTheXXTimeToady(
        what: String,
        scope: TheXXTimeTodayScope,
        time: Int,
        op: IntOp,
    ): Boolean {
        val dateLabel = formatTheXXTimeTodayScope(scope)
        val currentTime = store.read(Settings.IntSettings.DidSomethingAtDay(dateLabel, what))
        logger.d("isSomethingTheXXTimeToady, what: $what, time:$time, dateLabel: $dateLabel, currentTime: $currentTime")
        return matchIntOp(op = op, currentValue = currentTime, expectedValue = time)
    }

    fun didSomethingToday(what: String, scope: TheXXTimeTodayScope) {
        val dateLabel = formatTheXXTimeTodayScope(scope)
        val currentTime = store.read(Settings.IntSettings.DidSomethingAtDay(dateLabel, what))
        store.write(
            Settings.IntSettings.DidSomethingAtDay(dateLabel, what),
            (currentTime + 1).also {
                logger.d("didSomethingToday, $what $dateLabel update to: $it")
            })
    }

    fun onRequestPinShortcut(shortcutInfo: ShortcutInfo) {
        requestPinShortcuts.add(shortcutInfo)
    }

    fun onRequestPinAppWidget(appWidgetProviderInfo: AppWidgetProviderInfo) {
        requestPinAppWidgets.add(appWidgetProviderInfo)
    }
}