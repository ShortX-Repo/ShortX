package tornaco.apps.shortx.core.rule;

interface IRuleObserver {
    oneway void onAddOrUpdate(String id);
    oneway void onDelete(String id);
}