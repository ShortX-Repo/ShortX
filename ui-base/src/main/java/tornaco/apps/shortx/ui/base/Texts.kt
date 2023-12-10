package tornaco.apps.shortx.ui.base

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.theme.NoteTextColor
import tornaco.apps.shortx.ui.theme.WarningYellow


@Composable
fun fontFamilyProductSansRegular() = FontFamily(
    Font(assetManager = LocalContext.current.assets, path = "fonts/google/ProductSansRegular.ttf")
)

@Composable
fun fontFamilyProductSansBold() = FontFamily(
    Font(assetManager = LocalContext.current.assets, path = "fonts/google/ProductSansBold.ttf")
)

@Composable
fun LargeTitle(text: String) {
    Text(
        modifier = Modifier.padding(vertical = 12.dp),
        text = text,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun MediumTitle(text: String, color: Color = Color.Unspecified) {
    Text(
        modifier = Modifier.padding(vertical = 12.dp),
        text = text,
        color = color,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.W500,
            fontSize = 17.sp
        )
    )
}


@Composable
fun SmallTitle(
    modifier: Modifier = Modifier,
    text: String, color: Color = Color.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.W500,
            fontSize = 15.sp
        ),
        color = color
    )
}

@Composable
fun FieldTitle(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.secondary
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.W500,
            fontSize = 16.sp,
            color = color
        )
    )
}

@Composable
fun DialogTitle(text: String) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = text,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun DialogMessage(text: String) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = text,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun TipBody(text: String, maxLines: Int = 100) {
    Text(
        modifier = Modifier.padding(vertical = 4.dp),
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 11.sp
        ),
        maxLines = maxLines,
    )
}

@Composable
fun TipRow(
    text: String,
    icon: String = Remix.Others.lightbulb_line,
    color: Color = Color.Unspecified
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ColoredIcon(size = 16.dp) {
            RemixIcon(modifier = Modifier.size(12.dp), remixName = icon)
        }
        SmallSpacer()
        Text(
            modifier = Modifier,
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp
            ),
            color = color
        )
    }
}


@Composable
fun TipRow(
    text: String,
    icon: @Composable () -> Unit,
    color: Color = Color.Unspecified
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        SmallSpacer()
        Text(
            modifier = Modifier,
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp
            ),
            color = color
        )
    }
}

@Composable
fun LinkText(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    icon: @Composable () -> Unit = {
        RemixIcon(
            modifier = Modifier.size(12.dp),
            remixName = Remix.System.external_link_line,
            tint = color
        )
    },
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(ShortXCardRoundedCornerShape)
            .clickable {
                onClick()
            }
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Text(
            modifier = Modifier,
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                textDecoration = TextDecoration.Underline
            ),
            color = color
        )
    }
}


@Composable
fun ErrorTipRow(text: String, color: Color = MaterialTheme.colorScheme.error) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RemixIcon(
            modifier = Modifier.size(14.dp),
            remixName = Remix.System.error_warning_line,
            tint = color
        )
        SmallSpacer()
        Text(
            modifier = Modifier,
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp
            ),
            color = color
        )
    }
}


@Composable
fun WarnTipRow(text: String, color: Color = WarningYellow) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RemixIcon(
            modifier = Modifier.size(14.dp),
            remixName = Remix.System.error_warning_line,
            tint = color
        )
        SmallSpacer()
        Text(
            modifier = Modifier,
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp
            ),
            color = color
        )
    }
}

@Composable
fun NoteRow(modifier: Modifier = Modifier, text: String) {
    var textHeight by remember {
        mutableStateOf(16.dp)
    }
    val density = LocalDensity.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .height(textHeight)
                .width(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                .animateContentSize()
        )
        TinySpacer()
        Text(
            modifier = Modifier.onSizeChanged {
                textHeight = with(density) {
                    (it.height * 0.88f).toDp()
                }
            },
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp
            ),
            color = NoteTextColor,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
fun CategoryTitle(title: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
fun UnderlineSmallText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            textDecoration = TextDecoration.Underline
        )
    )
}

@Preview
@Composable
private fun Previews() {
    TipRow(text = "Hello")
}
