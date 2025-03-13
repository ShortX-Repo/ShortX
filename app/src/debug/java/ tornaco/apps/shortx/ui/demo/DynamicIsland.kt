package  tornaco.apps.shortx.ui.demo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun DynamicIsland(
    message: String,
    icon: Painter,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isExpanded) Color(0xFF4CAF50) else Color(0xFF2196F3)
    val cornerRadius = if (isExpanded) 32.dp else 24.dp
    val padding = if (isExpanded) 16.dp else 8.dp

    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable { onClick() }
            .animateContentSize() // 动态调整大小
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Icon(
                painter = icon,
                contentDescription = "Notification Icon",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 消息
            Text(
                text = message,
                color = Color.White,
                fontSize = if (isExpanded) 16.sp else 14.sp
            )
        }
    }
}

@Composable
fun DynamicIslandWithAnimation(
    message: String,
    icon: Painter,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = true, // 始终显示
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        DynamicIsland(
            message = message,
            icon = icon,
            isExpanded = isExpanded,
            onClick = onClick
        )
    }
}


@Composable
fun DynamicIslandDemo() {
    var isExpanded by remember { mutableStateOf(false) }
    val message = if (isExpanded) "This is a detailed notification message." else "New Notification"
    val icon =
        painterResource(id = com.absinthe.lc.rulesbundle.R.drawable.ic_lib_xiaomi) // 替换为你的图标资源

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 灵动岛
        DynamicIslandWithAnimation(
            message = message,
            icon = icon,
            isExpanded = isExpanded,
            onClick = { isExpanded = !isExpanded }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 模拟通知按钮
        Button(onClick = { isExpanded = !isExpanded }) {
            Text(text = if (isExpanded) "Collapse" else "Expand")
        }
    }
}

data class Notification(
    val id: Int, // 通知的唯一标识
    val message: String, // 通知内容
    val icon: Int, // 通知图标
    var isExpanded: Boolean = false // 是否展开
)

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val backgroundColor = if (notification.isExpanded) Color(0xFF4CAF50) else Color(0xFF2196F3)
    val cornerRadius = if (notification.isExpanded) 32.dp else 24.dp
    val padding = if (notification.isExpanded) 16.dp else 8.dp

    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable { onClick() }
            .animateContentSize() // 动态调整大小
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Icon(
                painter = painterResource(notification.icon),
                contentDescription = "Notification Icon",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 消息
            Text(
                text = notification.message,
                color = Color.White,
                fontSize = if (notification.isExpanded) 16.sp else 14.sp
            )
        }
    }
}

@Composable
fun NotificationList(
    notifications: List<Notification>,
    onNotificationClick: (Int) -> Unit
) {
    Column {
        notifications.take(3).forEach { notification ->
            NotificationItem(
                notification = notification,
                onClick = { onNotificationClick(notification.id) }
            )
        }
    }
}

@Composable
fun NotificationDemo2() {
    val notifications = remember {
        mutableStateListOf(
            Notification(
                id = 1,
                message = "Notification 1",
                icon = com.absinthe.lc.rulesbundle.R.drawable.ic_lib_xiaomi
            ),
            Notification(
                id = 2,
                message = "Notification 2",
                icon = com.absinthe.lc.rulesbundle.R.drawable.ic_lib_jd
            ),
            Notification(
                id = 3,
                message = "Notification 3",
                icon = com.absinthe.lc.rulesbundle.R.drawable.ic_lib_agora
            )
        )
    }

    // 处理通知点击事件
    val onNotificationClick = { id: Int ->
        val index = notifications.indexOfFirst { it.id == id }
        if (index != -1) {
            notifications[index] =
                notifications[index].copy(isExpanded = !notifications[index].isExpanded)
        }
    }

    // 自动收起逻辑
    LaunchedEffect(notifications) {
        notifications.forEachIndexed { index, notification ->
            if (notification.isExpanded) {
                delay(3000) // 3 秒后自动收起
                notifications[index] = notifications[index].copy(isExpanded = false)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 显示通知列表
        NotificationList(
            notifications = notifications,
            onNotificationClick = onNotificationClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 添加新通知的按钮
        Button(onClick = {
            val newId = notifications.size + 1
            notifications.add(
                Notification(
                    id = newId,
                    message = "Notification $newId",
                    icon = com.absinthe.lc.rulesbundle.R.drawable.ic_lib_jd
                )
            )
        }) {
            Text(text = "Add Notification")
        }
    }
}