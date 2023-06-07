package tornaco.apps.shortx.ui.base.dropdown

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon

// https://github.com/AndroidPoet/Dropdown

/*[animateContent]  enter and exit animation for the menu content. */
@ExperimentalAnimationApi
internal fun animateContent(
    animationProp: AnimationProp, enterAnimation: EnterAnimation, exitAnimation: ExitAnimation
): ContentTransform {
    return getMetaphorEnterAnimation(animationProp, enterAnimation) with getMetaphorExitAnimation(
        animationProp,
        exitAnimation
    )
}

/*[isNavigatingBack]  check if menu is navigating back. */
fun <T : Any> isNavigatingBack(
    currentMenu: MenuItem<T>, nextMenu: MenuItem<T>
): Boolean {
    return currentMenu.hasParent() && nextMenu == currentMenu.parent!!
}

/*[Dropdown]  DropdownMenu wrapper claas to add cascade effect and animations . */

@ExperimentalAnimationApi
@Composable
fun <T : Any> Dropdown(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    menu: MenuItem<T>,
    colors: DropDownMenuColors = dropDownMenuColors(),
    offset: DpOffset = DpOffset.Zero,
    enter: EnterAnimation = EnterAnimation.FadeIn,
    exit: ExitAnimation = ExitAnimation.FadeOut,
    easing: Easing = Easing.FastOutLinearInEasing,
    enterDuration: Int = 500,
    exitDuration: Int = 500,
    onItemSelected: (T) -> Unit,
    onDismiss: () -> Unit,
) {

    androidx.compose.material3.DropdownMenu(
        modifier = modifier.width(192.dp),
        expanded = isOpen,
        onDismissRequest = onDismiss,
        offset = offset
    ) {

        val state by remember { mutableStateOf(MenuState(menu)) }
        AnimatedContent(targetState = state.currentMenuItem, transitionSpec = {
            if (isNavigatingBack(initialState, targetState)) {
                animateContent(
                    AnimationProp(
                        enterDuration,
                        exitDuration,
                        0,
                        easing,
                    ), enter, exit
                )
            } else {
                animateContent(
                    AnimationProp(
                        enterDuration,
                        exitDuration,
                        0,
                        easing,
                    ), enter, exit
                )
            }
        }, label = "DropdownAnim") { targetMenu ->
            DropdownContent(
                state = state,
                targetMenu = targetMenu,
                onItemSelected = onItemSelected,
                colors = colors,
            )
        }
    }
}

/*[DropdownContent] represents menu content properties  . */
@Composable
fun <T : Any> DropdownContent(
    state: MenuState<T>,
    targetMenu: MenuItem<T>,
    onItemSelected: (T) -> Unit,
    colors: DropDownMenuColors,
) {
    Column(modifier = Modifier.width(192.dp)) {
        if (targetMenu.hasParent()) {
            CascadeHeaderItem(
                targetMenu.title, colors.contentColor
            ) {
                state.currentMenuItem = targetMenu.parent!!
            }
        }
        if (targetMenu.hasChildren()) {
            for (item in targetMenu.children!!) {
                if (item.hasChildren()) {
                    ParentItem(
                        item.id,
                        item.title,
                        item.description,
                        item.icon,
                        item.iconColor,
                        colors.contentColor
                    ) { id ->
                        val child = targetMenu.getChild(id)
                        if (child != null) {
                            state.currentMenuItem = child
                        } else {
                            throw IllegalStateException("Invalid item id : $id")
                        }
                    }
                } else {
                    ChildItem(
                        id = item.id,
                        title = item.title,
                        description = item.description,
                        icon = item.icon,
                        iconColor = item.iconColor,
                        contentColor = colors.contentColor,
                        onClick = onItemSelected
                    )
                }
            }
        }
    }
}

/*[Space] add space content . */
@Composable
fun Space() {
    Spacer(modifier = Modifier.width(12.dp))
}

/*[MenuItemIcon] icon properties. */
@Composable
fun MenuItemIcon(icon: String, tint: Color) {
    ColoredIcon(size = 24.dp, color = tint.copy(alpha = ActionColorBgAlpha)) {
        RemixIcon(
            modifier = Modifier.size(12.dp), remixName = icon, tint = tint
        )
    }
}

@Composable
fun ParentItemArrowIcon(icon: String, tint: Color) {
    RemixIcon(
        modifier = Modifier.size(18.dp), remixName = icon, tint = tint
    )
}

/*[MenuItemText] text properties. */
@Composable
fun MenuItemText(
    modifier: Modifier,
    text: String,
    color: Color,
    isHeaderText: Boolean = false,
) {
    val style = if (isHeaderText) {
        MaterialTheme.typography.titleSmall
    } else {
        MaterialTheme.typography.titleSmall
    }

    Text(
        modifier = modifier,
        text = text,
        style = style,
        color = color,
    )
}

@Composable
fun MenuItemDescription(
    modifier: Modifier,
    text: String,
    color: Color,
) {
    val style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)

    Text(
        modifier = modifier,
        text = text,
        style = style,
        color = color
    )
}

/*[MenuItem] DropdownMenuItem  wrapper to handle onclick and user interaction */
@Composable
fun MenuItem(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    DropdownMenuItem(
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                content()
            }
        }
    )
}

/*[CascadeHeaderItem] represent header item of the menu */
@Composable
fun CascadeHeaderItem(
    title: String,
    contentColor: Color,
    onClick: () -> Unit,
) {
    MenuItem(onClick = { onClick() }) {
        ParentItemArrowIcon(
            icon = Remix.Arrows.arrow_left_s_line,
            tint = contentColor.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        MenuItemText(
            modifier = Modifier.weight(1f),
            text = title,
            color = contentColor.copy(alpha = 0.8f),
            isHeaderText = true,
        )
    }
}

/*[ParentItem] parentItem of the menu*/
@Composable
fun <T> ParentItem(
    id: T,
    title: String,
    description: String?,
    icon: String?,
    iconColor: Color,
    contentColor: Color,
    onClick: (T) -> Unit,
) {
    MenuItem(onClick = { onClick(id) }) {
        if (icon != null) {
            MenuItemIcon(icon = icon, tint = iconColor)
            Space()
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            MenuItemText(
                modifier = Modifier,
                text = title,
                color = contentColor,
            )
            description?.let {
                MenuItemDescription(
                    modifier = Modifier,
                    text = it,
                    color = contentColor,
                )
            }
        }
        Space()
        ParentItemArrowIcon(icon = Remix.Arrows.arrow_right_s_line, tint = contentColor)
    }
}

/*[ChildItem] childItem of the menu*/
@Composable
fun <T> ChildItem(
    id: T,
    title: String,
    description: String?,
    icon: String?,
    iconColor: Color = Color.Unspecified,
    contentColor: Color,
    onClick: (T) -> Unit,
) {
    MenuItem(onClick = { onClick(id) }) {
        if (icon != null) {
            MenuItemIcon(icon = icon, tint = iconColor)
            Space()
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            MenuItemText(
                modifier = Modifier,
                text = title,
                color = contentColor,
            )
            description?.let {
                MenuItemDescription(
                    modifier = Modifier,
                    text = it,
                    color = contentColor,
                )
            }
        }
    }
}
