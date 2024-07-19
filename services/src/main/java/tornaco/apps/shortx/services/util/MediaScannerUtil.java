package tornaco.apps.shortx.services.util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

public class MediaScannerUtil {

    /**
     * Notify the system to scan the given file so it is added to the media library.
     *
     * @param context  The context to use for the connection.
     * @param filePath The path of the file to be scanned.
     */
    public static void scanFile(Context context, String filePath) {
        MediaScannerConnection.scanFile(
                context,
                new String[]{filePath},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        // You can log the result or handle the completion here if needed
                        System.out.println("Scanned " + path + ":");
                        System.out.println("-> uri=" + uri);
                    }
                }
        );
    }
}