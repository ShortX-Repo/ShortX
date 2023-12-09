@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.clipbord

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.theme.PrefIcon
import tornaco.apps.shortx.ui.theme.ShortXTheme

@SuppressLint("ViewConstructor")
class ClipboardView(
    context: Context,
    i18N: I18N,
    onDetach: (BaseComposeView) -> Unit,
) : BaseComposeView(context, i18N, onDetach) {

    @Composable
    override fun ComposeContent() {
        ShortXTheme {
            val interactionSource = remember { MutableInteractionSource() }
            val requester = remember { FocusRequester() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onKeyEvent {
                        if (it.key == Key.Back) {
                            detach()
                        }
                        true
                    }
                    .focusRequester(requester)
                    .focusable()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            detach()
                        }
                    ), contentAlignment = Alignment.BottomCenter
            ) {
                LaunchedEffect(Unit) {
                    requester.requestFocus()
                }

                Surface(
                    modifier = Modifier,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    ),
                    color = ColorDefaults.backgroundSurfaceColor(),
                    shadowElevation = 4.dp,
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction = 0.3f),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {

                        stickyHeader {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ColorDefaults.backgroundSurfaceColor()),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { }) {
                                    Text(text = "CLEAR ALL")
                                }

                                IconButton(onClick = {
                                    detach()
                                }) {
                                    ColoredIcon(
                                        size = 28.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        RemixIcon(
                                            Remix.Arrows.arrow_drop_down_line,
                                            modifier = Modifier.size(20.dp),
                                            tint = Color.White
                                        )
                                    }
                                }

                            }
                        }

                        items(20) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f, fill = false),
                                    text = "This is text $it"
                                )

                                IconButton(onClick = { }) {
                                    RemixIcon(
                                        modifier = Modifier.size(18.dp),
                                        remixName = Remix.System.close_fill,
                                        tint = PrefIcon
                                    )
                                }
                            }
                        }


                    }
                }


            }
        }
    }
}