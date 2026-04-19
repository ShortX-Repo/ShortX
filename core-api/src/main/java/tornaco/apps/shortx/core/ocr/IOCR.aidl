package tornaco.apps.shortx.core.ocr;

import tornaco.apps.shortx.core.os.SynchronousResultReceiver;

interface IOCR {
    List<ByteArrayWrapper> findAllContinuousTextPositions(int engine, in Bitmap image, String text);
    String recognizeText(int engine, in Bitmap image);

    // List<tornaco.apps.shortx.core.proto.common.Rect>
    // List<ByteArrayWrapper>
    oneway void findAllContinuousTextPositionsPFD(int engine, in ParcelFileDescriptor pfd, String text, in SynchronousResultReceiver receiver);
    // StringValue
    oneway void recognizeTextPFD(int engine, in ParcelFileDescriptor pfd, in SynchronousResultReceiver receiver);

    String recognizeTextJson(int engine, in Bitmap image);
    oneway void recognizeTextJsonPFD(int engine, in ParcelFileDescriptor pfd, in SynchronousResultReceiver receiver);
}