package tornaco.apps.shortx.ui.ocr

import android.content.Context
import com.equationl.paddleocr4android.CpuPowerMode
import com.equationl.paddleocr4android.OCR
import com.equationl.paddleocr4android.OcrConfig
import com.equationl.paddleocr4android.callback.OcrInitCallback
import tornaco.apps.shortx.core.util.Logger

class Paddle(private val context: Context) {
    private val logger = Logger("Paddle")

    fun downloadModels() {

    }

    fun init() {
        // 配置
        val config = OcrConfig()
        //config.labelPath = null
        config.modelPath =
            "models/ch_PP-OCRv2" // 不使用 "/" 开头的路径表示安装包中 assets 目录下的文件，例如当前表示 assets/models/ocr_v2_for_cpu
        // config.modelPath = "/sdcard/Android/data/com.equationl.paddleocr4android.app/files/models" // 使用 "/" 表示手机储存路径，测试时请将下载的三个模型放置于该目录下
        config.clsModelFilename = "cls.nb" // cls 模型文件名
        config.detModelFilename = "det_db.nb" // det 模型文件名
        config.recModelFilename = "rec_crnn.nb" // rec 模型文件名

        // 运行全部模型
        // 请根据需要配置，三项全开识别率最高；如果只开识别几乎无法正确识别，至少需要搭配检测或分类其中之一
        // 也可单独运行 检测模型 获取文本位置
        config.isRunDet = true
        config.isRunCls = true
        config.isRunRec = true
        // 使用所有核心运行
        config.cpuPowerMode = CpuPowerMode.LITE_POWER_FULL
        // 绘制文本位置
        config.isDrwwTextPositionBox = true

        // 2.异步初始化
        val ocr = OCR(context)
        ocr.initModel(config, object : OcrInitCallback {
            override fun onSuccess() {
                logger.i("Init success")
            }
            override fun onFail(e: Throwable) {
                logger.e(e, "Init fail")
            }
        })
    }
}