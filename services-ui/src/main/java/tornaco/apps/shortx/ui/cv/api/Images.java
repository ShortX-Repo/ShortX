package tornaco.apps.shortx.ui.cv.api;

import org.autojs.autojs.core.image.ImageWrapper;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import shortx.plugin.cv.api.ScreenMetrics;
import tornaco.apps.shortx.ui.cv.image.TemplateMatching;
import tornaco.apps.shortx.ui.cv.opencv.OpenCVHelper;

/**
 * Created by Stardust on May 20, 2017.
 */
@SuppressWarnings("unused")
public class Images {
    private final ScreenMetrics mScreenMetrics;

    public Images(ScreenMetrics metrics) {
        this.mScreenMetrics = metrics;
    }

    private static final String TAG = Images.class.getSimpleName();

    public Point findImage(ImageWrapper image, ImageWrapper template) {
        return findImage(image, template, 0.9f, null);
    }

    public Point findImage(ImageWrapper image, ImageWrapper template, float threshold) {
        return findImage(image, template, threshold, null);
    }

    public Point findImage(ImageWrapper image, ImageWrapper template, float threshold, Rect rect) {
        return findImage(image, template, 0.7f, threshold, rect, TemplateMatching.MAX_LEVEL_AUTO);
    }

    public Point findImage(ImageWrapper image, ImageWrapper template,
                           float weakThreshold, float threshold, Rect rect, int maxLevel) {
        Mat src = image.getMat();
        if (rect != null) {
            src = new Mat(src, rect);
        }
        Point point = TemplateMatching.fastTemplateMatching(
                src,
                template.getMat(),
                TemplateMatching.MATCHING_METHOD_DEFAULT,
                weakThreshold,
                threshold,
                maxLevel
        );

        if (src != image.getMat()) {
            OpenCVHelper.release(src);
        }
        image.shoot();
        template.shoot();

        if (point != null) {
            if (rect != null) {
                point.x += rect.x;
                point.y += rect.y;
            }
            point.x = mScreenMetrics.scaleX((int) point.x);
            point.y = mScreenMetrics.scaleX((int) point.y);
        }
        return point;
    }

    public List<TemplateMatching.Match> matchTemplate(ImageWrapper image, ImageWrapper template,
                                                      float weakThreshold, float threshold,
                                                      Rect rect, int maxLevel, int limit) {
        Mat src = image.getMat();
        if (rect != null) {
            src = new Mat(src, rect);
        }
        List<TemplateMatching.Match> result = TemplateMatching.fastTemplateMatching(
                src,
                template.getMat(),
                Imgproc.TM_CCOEFF_NORMED,
                weakThreshold,
                threshold,
                maxLevel,
                limit
        );

        if (src != image.getMat()) {
            OpenCVHelper.release(src);
        }
        image.shoot();
        template.shoot();

        for (TemplateMatching.Match match : result) {
            Point point = match.point;
            if (rect != null) {
                point.x += rect.x;
                point.y += rect.y;
            }
            point.x = mScreenMetrics.scaleX((int) point.x);
            point.y = mScreenMetrics.scaleX((int) point.y);
        }
        return result;
    }

    public Mat newMat() {
        return new Mat();
    }

    public Mat newMat(Mat mat, Rect roi) {
        return new Mat(mat, roi);
    }
}