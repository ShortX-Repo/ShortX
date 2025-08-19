package tornaco.apps.shortx.core.rule;

interface IToggleObserver {
    oneway void onAddOrUpdate(String id);
    oneway void onDelete(String id);
    oneway void onEnableStateMayChange(String id);


    oneway void onSetAddOrUpdate(String id);
    oneway void onSetDelete(String id);
}