package tornaco.apps.shortx.core;

interface ICallback {
    oneway void receive(in ByteArrayWrapper data);
}