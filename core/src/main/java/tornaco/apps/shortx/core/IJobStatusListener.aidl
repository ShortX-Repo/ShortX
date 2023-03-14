package tornaco.apps.shortx.core;

interface IJobStatusListener {
    oneway void onJobStarted(String id);
    oneway void onJobCompleted(String id);
    oneway void onJobCancelled(String id);
}