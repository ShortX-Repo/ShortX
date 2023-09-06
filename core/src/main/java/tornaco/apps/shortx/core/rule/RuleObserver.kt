package tornaco.apps.shortx.core.rule

open class RuleObserver : IRuleObserver.Stub() {
    override fun onAddOrUpdate(id: String) {
        onRuleChanged(id)
    }

    override fun onDelete(id: String) {
        onRuleChanged(id)
    }

    override fun onEnabled(id: String) {
        onRuleChanged(id)
    }

    override fun onDisabled(id: String) {
        onRuleChanged(id)
    }

    override fun onSetAddOrUpdate(id: String) {
        onRuleSetChanged(id)
    }

    override fun onSetDelete(id: String) {
        onRuleSetChanged(id)
    }

    open fun onRuleChanged(id: String) {}
    open fun onRuleSetChanged(id: String) {}
}