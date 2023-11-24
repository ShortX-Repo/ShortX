package tornaco.apps.shortx.core.rule

open class ToggleObserver : IToggleObserver.Stub() {
    override fun onAddOrUpdate(id: String) {
        onToggleChanged(id)
    }

    override fun onDelete(id: String) {
        onToggleChanged(id)
    }

    override fun onEnableStateMayChange(id: String) {
        onToggleChanged(id)
    }

    override fun onSetAddOrUpdate(id: String) {
        onToggleSetChanged(id)
    }

    override fun onSetDelete(id: String) {
        onToggleSetChanged(id)
    }

    open fun onToggleChanged(id: String) {}
    open fun onToggleSetChanged(id: String) {}
}