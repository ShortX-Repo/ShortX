package tornaco.apps.shortx.core.rule;

interface IPkgSetObserver {
    oneway void onAddOrUpdate(String id);
    oneway void onDelete(String id);
}