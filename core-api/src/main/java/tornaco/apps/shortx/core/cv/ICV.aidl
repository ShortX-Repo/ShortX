package tornaco.apps.shortx.core.cv;

import tornaco.apps.shortx.core.os.SynchronousResultReceiver;

interface ICV {
    Point findImage(in Bitmap image, in Bitmap template);
    List<Point> findPointsByColor(in Bitmap image, int color, int threshold, in Rect rect);

    Point findImagePFD(in ParcelFileDescriptor imagePFD, in ParcelFileDescriptor templatePFD);
    List<Point> findPointsByColorPFD(in ParcelFileDescriptor pfd, int color, int threshold, in Rect rect);
}