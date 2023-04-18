package tornaco.apps.shortx.ui.base.dropdown

/*[MenuItem]  generic menu item with title and icon. */
class MenuItem<T : Any> {
    lateinit var id: T

    lateinit var title: String
    var description: String? = null

    var icon: String? = null

    var parent: MenuItem<T>? = null

    var children: MutableList<MenuItem<T>>? = null

    fun hasChildren(): Boolean = !children.isNullOrEmpty()

    fun hasParent(): Boolean = parent != null

    fun getChild(id: T): MenuItem<T>? =
        children?.let { items -> items.find { item -> item.id == id } }
}
