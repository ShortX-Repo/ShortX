package tornaco.apps.shortx.ui.cv.api

import android.graphics.Point
import tornaco.apps.shortx.ui.cv.image.ImageWrapper

typealias AndroidPoint = Point

object ShortXImages {
    fun Images.findImageX(image: ImageWrapper, template: ImageWrapper): AndroidPoint {
        return findImage(image, template).let {
            AndroidPoint(it.x.toInt(), it.y.toInt())
        }
    }
}