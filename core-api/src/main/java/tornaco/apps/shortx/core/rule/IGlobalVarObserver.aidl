package tornaco.apps.shortx.core.rule;

interface IGlobalVarObserver {
    oneway void onAddOrUpdate(String id);
    oneway void onDelete(String id);
}