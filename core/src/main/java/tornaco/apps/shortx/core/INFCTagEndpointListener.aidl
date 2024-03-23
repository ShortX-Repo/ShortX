package tornaco.apps.shortx.core;

interface INFCTagEndpointListener {
    oneway void onDispatchTagEndpoint(in byte[] uid);
}