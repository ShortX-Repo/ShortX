package tornaco.apps.shortx.core.rule

open class DAObserver : IDAObserver.Stub() {
    override fun onAddOrUpdate(id: String) {
        onDAChanged(id)
    }

    override fun onDelete(id: String) {
        onDAChanged(id)
    }

    override fun onSetAddOrUpdate(id: String) {
        onDASetChanged(id)
    }

    override fun onSetDelete(id: String) {
        onDASetChanged(id)
    }

    open fun onDAChanged(id: String) {}
    open fun onDASetChanged(id: String) {}
}