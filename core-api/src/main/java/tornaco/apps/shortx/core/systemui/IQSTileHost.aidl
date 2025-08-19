package tornaco.apps.shortx.core.systemui;

import tornaco.apps.shortx.core.os.SynchronousResultReceiver;

interface IQSTileHost {
    oneway void getTiles(in SynchronousResultReceiver receiver);
    oneway void click(String tileSpec);
    oneway void longClick(String tileSpec);
    int getState(String tileSpec);
}