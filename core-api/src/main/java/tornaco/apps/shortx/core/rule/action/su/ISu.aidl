package tornaco.apps.shortx.core.rule.action.su;

import tornaco.apps.shortx.core.os.SynchronousResultReceiver;

interface ISu {
    oneway void exe(String requestId, in String[] command, in SynchronousResultReceiver receiver, boolean singleShot);
    oneway void close(String requestId);
}