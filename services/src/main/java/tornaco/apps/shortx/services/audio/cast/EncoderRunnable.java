package tornaco.apps.shortx.services.audio.cast;


import android.media.MediaCodec;

import tornaco.apps.shortx.core.util.Logger;

public abstract class EncoderRunnable implements Runnable {
    private final Logger logger = new Logger("EncoderRunnable");

    MediaCodec venc;

    EncoderRunnable(MediaCodec venc) {
        this.venc = venc;
    }

    abstract void encode() throws Exception;

    protected void cleanup() {
        venc = null;
    }

    @Override
    final public void run() {
        try {
            encode();
        } catch (Exception e) {
            logger.e(e, "Encoder error");
        }
        cleanup();
        logger.i("=======ENCODING COMPLETE=======");
    }
}