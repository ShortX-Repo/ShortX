package tornaco.apps.shortx.core.phone;

import tornaco.apps.shortx.core.os.SynchronousResultReceiver;

interface ISMSProvider {
    oneway void markAsRead(long id, in SynchronousResultReceiver receiver);
    oneway void delete(long id, in SynchronousResultReceiver receiver);
}