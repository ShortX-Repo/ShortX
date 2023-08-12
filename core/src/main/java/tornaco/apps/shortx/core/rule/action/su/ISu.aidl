package tornaco.apps.shortx.core.rule.action.su;

import tornaco.apps.shortx.core.os.SynchronousResultReceiver;

interface ISu {
    oneway void exe(in String[] command, in SynchronousResultReceiver receiver);
    oneway void close(in SynchronousResultReceiver receiver);
}