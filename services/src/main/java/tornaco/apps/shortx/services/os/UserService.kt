package tornaco.apps.shortx.services.os

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.UserHandle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.userManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope

sealed interface UserEvent {
    data class Added(val userId: Int) : UserEvent
    data class Removed(val userId: Int) : UserEvent
}

class UserService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val _userEvent = MutableSharedFlow<UserEvent>()
    val userEvent = _userEvent.asSharedFlow()

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
        systemServiceScope.launchSafety {
            _userEvent.emit(UserEvent.Removed(userId))
        }
    }

    private fun onUserAdded(userId: Int) {
        systemServiceScope.launchSafety {
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

        context.registerReceiver(userBroadcastReceiver, userFilter)
    }
}