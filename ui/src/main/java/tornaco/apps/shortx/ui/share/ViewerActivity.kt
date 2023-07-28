package tornaco.apps.shortx.ui.share

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.rule.parseShareContentDA
import tornaco.apps.shortx.core.rule.parseShareContentRule
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addda.AddDirectActionScreen
import tornaco.apps.shortx.ui.addrule.AddRuleScreen
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalNavHostController
import java.nio.charset.Charset

@OptIn(ExperimentalAnimationApi::class)
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
    }

    @Composable
    override fun Content() {
        val navController = rememberAnimatedNavController()
        CompositionLocalProvider(LocalNavHostController provides navController) {
            val sharedData = remember {
                parseIntent()
            }
            val context = LocalContext.current
            if (sharedData == null) {
                ToastUnknownContentAndFinish(context)
            } else {

                when (sharedData.type) {
                    "da" -> {
                        val da = parseShareContentDA(sharedData.data)
                        da?.let {
                            AddDirectActionScreen(
                                da = da,
                                screenTitle = LocalI18N.current["ui.common.share.viewer.title"],
                                canSaveWithoutChanges = true
                            ) {
                                finish()
                            }
                        }
                    }

                    "daUrl" -> {
                        AddDirectActionScreen(
                            da = null,
                            url = sharedData.data,
                            screenTitle = "URL",
                            canSaveWithoutChanges = true
                        ) {
                            finish()
                        }
                    }

                    "rule" -> {
                        val rule = parseShareContentRule(sharedData.data)
                        rule?.let {
                            AddRuleScreen(
                                rule = rule,
                                screenTitle = LocalI18N.current["ui.common.share.viewer.title"],
                                canSaveWithoutChanges = true
                            ) {
                                finish()
                            }
                        }
                    }

                    "ruleUrl" -> {
                        AddRuleScreen(
                            rule = null,
                            url = sharedData.data,
                            screenTitle = "URL",
                            canSaveWithoutChanges = true
                        ) {
                            finish()
                        }
                    }

                    else -> {
                        ToastUnknownContentAndFinish(context)
                    }
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
                contentResolver.openInputStream(u)?.use {
                    val text = it.reader(Charset.defaultCharset()).readText()
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
data class SharedData(val type: String, val data: String) : Parcelable