package tornaco.apps.shortx.core.location;

import tornaco.apps.shortx.core.os.SynchronousResultReceiver;

interface ILocationProvider {
    oneway void getCurrentLocationInfo(String requestId, in SynchronousResultReceiver receiver, in ByteArrayWrapper requestData);
    oneway void getCurrentLocationAddress(String requestId, in SynchronousResultReceiver receiver, in ByteArrayWrapper requestData);
}