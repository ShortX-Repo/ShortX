package tornaco.apps.shortx.ui.base.dropdown

import androidx.compose.ui.graphics.Color
import tornaco.apps.shortx.ui.theme.NoteTextColor


/** DropDownMenuBuilder to build the menu. */

class DropDownMenuBuilder<T : Any> {
    var menu: MenuItem<T> = MenuItem<T>()

    fun icon(value: String, color: Color = NoteTextColor) {
        menu.icon = value
        menu.iconColor = color
    }

    fun item(
        id: T,
        title: String,
        description: String? = null,
        init: (DropDownMenuBuilder<T>.() -> Unit)? = null
    ) {
        val menuBuilder = DropDownMenuBuilder<T>()
        val child = menuBuilder.menu.apply {
            this.id = id
            this.title = title
            this.description = description
        }
        init?.let {
            menuBuilder.init()
        }
        menu.children = menu.children ?: mutableListOf()
        child.parent = menu
        menu.children!!.add(child)
    }

    fun item(
        id: T,
        titleAndDescription: Pair<String, String?>,
        init: (DropDownMenuBuilder<T>.() -> Unit)? = null
    ) {
        val menuBuilder = DropDownMenuBuilder<T>()
        val child = menuBuilder.menu.apply {
            this.id = id
            this.title = titleAndDescription.first
            this.description = titleAndDescription.second
        }
        init?.let {
            menuBuilder.init()
        }
        menu.children = menu.children ?: mutableListOf()
        child.parent = menu
        menu.children!!.add(child)
    }
}


fun <T : Any> dropDownMenu(init: DropDownMenuBuilder<T>.() -> Unit): MenuItem<T> {
    val menuBuilder = DropDownMenuBuilder<T>()
    menuBuilder.init()
    return menuBuilder.menu
}
