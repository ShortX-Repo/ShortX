package tornaco.apps.shortx.core;

interface IActionEvaluateListener {
    oneway void onStart(String id);
    oneway void onComplete(String id);
}