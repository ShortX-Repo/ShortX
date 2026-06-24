package tornaco.apps.shortx.j

import android.util.Log

/**
 * 资源配置加载器
 *
 * 在应用启动时加载资源包的渠道配置信息，
 * 用于确定当前资源包的分发渠道并初始化对应的资源加载策略。
 *
 * 这是资源加载器初始化流程的一部分，
 * 由 EntryPoint 在启动阶段异步调用。
 */
object ResourceConfigLoader {

    private const val TAG = "ResConfig"

    // 日志开关，默认关闭；调试时改为 true
    private const val LOG_ENABLED = false

    init {
        System.loadLibrary("services")
    }

    /**
     * 加载结果码
     */
    object Result {
        /** 配置加载成功，已识别渠道 */
        const val OK = 0

        /** 资源包路径获取失败 */
        const val ERR_PATH = 1

        /** 打包参数提取失败 */
        const val ERR_PARSE = 2

        /** 渠道指纹未匹配（未知渠道，使用默认配置） */
        const val ERR_NO_MATCH = 3
    }

    /**
     * 加载资源配置并匹配渠道指纹
     *
     * @param apkPath 指定资源包路径，传 null 则自动从内存映射获取
     * @return 结果码，[Result.OK] 表示成功
     */
    @JvmOverloads
    fun load(apkPath: String? = null): Int {
        return try {
            val code = nativeLoadConfig(apkPath)
            if (LOG_ENABLED) {
                when (code) {
                    Result.OK -> Log.d(TAG, "资源配置加载成功")
                    Result.ERR_PATH -> Log.w(TAG, "资源配置加载失败: 无法获取资源包路径")
                    Result.ERR_PARSE -> Log.w(TAG, "资源配置加载失败: 打包参数提取失败")
                    Result.ERR_NO_MATCH -> Log.w(TAG, "资源配置加载: 渠道指纹未匹配，使用默认配置")
                }
            }
            code
        } catch (e: Throwable) {
            if (LOG_ENABLED) Log.e(TAG, "资源配置加载异常", e)
            -1
        }
    }

    @JvmStatic
    private external fun nativeLoadConfig(apkPath: String?): Int
}
