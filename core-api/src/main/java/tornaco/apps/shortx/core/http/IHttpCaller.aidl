package tornaco.apps.shortx.core.http;

import tornaco.apps.shortx.core.os.SynchronousResultReceiver;

interface IHttpCaller {
    oneway void call(String requestId, in SynchronousResultReceiver receiver, String url, String method, in Map<String, String> headers, in ByteArrayWrapper requestBodyData, boolean withCookieJar, boolean trustAllCerts);
    oneway void close(String requestId);
}