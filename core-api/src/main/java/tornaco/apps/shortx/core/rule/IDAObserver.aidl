package tornaco.apps.shortx.core.rule;

interface IDAObserver {
    oneway void onAddOrUpdate(String id);
    oneway void onDelete(String id);

    oneway void onSetAddOrUpdate(String id);
    oneway void onSetDelete(String id);
}