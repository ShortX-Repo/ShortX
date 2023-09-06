package tornaco.apps.shortx.core.rule;

interface IRuleObserver {
    oneway void onAddOrUpdate(String id);
    oneway void onDelete(String id);
    oneway void onEnabled(String id);
    oneway void onDisabled(String id);


    oneway void onSetAddOrUpdate(String id);
    oneway void onSetDelete(String id);
}