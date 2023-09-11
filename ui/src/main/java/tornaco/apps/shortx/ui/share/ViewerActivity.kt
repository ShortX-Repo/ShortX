package tornaco.apps.shortx.ui.share

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.parseShareContentDA
import tornaco.apps.shortx.core.rule.parseShareContentDASet
import tornaco.apps.shortx.core.rule.parseShareContentRule
import tornaco.apps.shortx.core.rule.parseShareContentRuleSet
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addda.AddDirectActionScreen
import tornaco.apps.shortx.ui.addrule.AddRuleScreen
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.LocalI18N
import java.nio.charset.Charset

@AndroidEntryPoint
class ViewerActivity : BaseActivity() {
    private val logger = Logger("ViewerActivity")

    companion object {
        @JvmStatic
        fun start(context: Context, data: SharedData) {
            val starter = Intent(context, ViewerActivity::class.java)
                .putExtra("data", data)
            context.startActivity(starter)
        }

        @JvmStatic
        fun start(context: Context, fileUrl: Uri) {
            val starter = Intent(context, ViewerActivity::class.java).setData(fileUrl)
                .setAction(Intent.ACTION_VIEW)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        val sharedData = remember {
            parseIntent()
        }
        val context = LocalContext.current
        if (sharedData == null) {
            ToastUnknownContentAndFinish(context)
        } else {

            when (sharedData.type) {
                SharedData.TYPE_DA -> {
                    val da = parseShareContentDA(sharedData.data)
                    da?.let {
                        AddDirectActionScreen(
                            da = da,
                            screenTitle = da.title + " - " + LocalI18N.current["ui.common.share.viewer.title"],
                            canSaveWithoutChanges = true,
                            saveIconName = Remix.System.download_line
                        ) {
                            finish()
                        }
                    }
                }

                SharedData.TYPE_DA_URL -> {
                    AddDirectActionScreen(
                        da = null,
                        url = sharedData.data,
                        screenTitle = "URL",
                        canSaveWithoutChanges = true,
                        saveIconName = Remix.System.download_line
                    ) {
                        finish()
                    }
                }

                SharedData.TYPE_RULE -> {
                    val rule = parseShareContentRule(sharedData.data)
                    rule?.let {
                        AddRuleScreen(
                            rule = rule,
                            screenTitle = rule.title + " - " + LocalI18N.current["ui.common.share.viewer.title"],
                            canSaveWithoutChanges = true,
                            saveIconName = Remix.System.download_line
                        ) {
                            finish()
                        }
                    }
                }

                SharedData.TYPE_RULE_URL -> {
                    AddRuleScreen(
                        rule = null,
                        url = sharedData.data,
                        screenTitle = "URL",
                        canSaveWithoutChanges = true,
                        saveIconName = Remix.System.download_line
                    ) {
                        finish()
                    }
                }

                SharedData.TYPE_RULE_SET -> {
                    val rs = parseShareContentRuleSet(sharedData.data)
                    rs?.let {
                        RuleSetViewer(it, back = {
                            finish()
                        })
                    }
                }

                SharedData.TYPE_DA_SET -> {
                    val ds = parseShareContentDASet(sharedData.data)
                    ds?.let {
                        DASetViewer(it, back = {
                            finish()
                        })
                    }
                }

                else -> {
                    ToastUnknownContentAndFinish(context)
                }
            }
        }
    }

    @Composable
    private fun ToastUnknownContentAndFinish(context: Context) {
        Toast.makeText(
            context,
            LocalI18N.current["ui.common.share.unknown.content"],
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    private fun parseIntent(): SharedData? {
        logger.d("parseIntent: $intent")
        return if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data
            logger.d("uri: $uri")
            logger.d("scheme: ${uri?.scheme}")
            uri?.let { u ->
                contentResolver.openInputStream(u)?.use { stream ->
                    val text = stream.reader(Charset.defaultCharset()).readText()
                    logger.d("text: $text")
                    val header = text.getContentHeader()
                    logger.d("header: $header")
                    val data = text.getContentData()
                    logger.d("data: $data")
                    val type = header["type"]
                    type?.let {
                        SharedData(it, data)
                    }
                }
            }
        } else intent.getParcelableExtra("data")
    }
}

@DoNotStrip
@Parcelize
data class SharedData(val type: String, val data: String) : Parcelable {
    companion object {
        const val TYPE_DA = "da"
        const val TYPE_DA_SET = "daSet"
        const val TYPE_DA_URL = "daUrl"
        const val TYPE_RULE = "rule"
        const val TYPE_RULE_SET = "ruleSet"
        const val TYPE_RULE_URL = "ruleUrl"
    }
}