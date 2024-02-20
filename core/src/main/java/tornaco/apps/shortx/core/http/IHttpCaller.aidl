package tornaco.apps.shortx.core.http;

interface IHttpCaller {
    ParcelableResponseData call( String url, String method, in Map<String, String> headers, in ByteArrayWrapper requestBodyData, boolean withCookieJar, boolean trustAllCerts);
}