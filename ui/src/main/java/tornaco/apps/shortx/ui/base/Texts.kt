package tornaco.apps.shortx.ui.base

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.theme.NoteTextColor

@Composable
fun LargeTitle(text: String) {
    Text(
        modifier = Modifier.padding(vertical = 12.dp),
        text = text,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun MediumTitle(text: String) {
    Text(
        modifier = Modifier.padding(vertical = 12.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.W500,
            fontSize = 17.sp
        )
    )
}

@Composable
fun MediumTitleWithTip(text: String, tipText: String) {
    val tipDialogState = rememberTipDialogState(title = text, tip = tipText)

    Row(
        modifier = Modifier
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.W500,
                fontSize = 17.sp
            )
        )

        IconButton(onClick = {
            tipDialogState.show()
        }) {
            RemixIcon(
                remixName = Remix.System.question_line,
                modifier = Modifier.size(16.dp)
            )
        }
    }

    TipDialog(tipDialogState)
}

@Composable
fun SmallTitle(text: String, color: Color = Color.Unspecified) {
    Text(
        modifier = Modifier.padding(vertical = 12.dp),
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.W500,
            fontSize = 15.sp
        ),
        color = color
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
fun TipBody(text: String) {
    Text(
        modifier = Modifier.padding(vertical = 4.dp),
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 10.sp
        )
    )
}

@Composable
fun TipRow(text: String, color: Color = Color.Unspecified) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RemixIcon(modifier = Modifier.size(14.dp), remixName = Remix.Others.lightbulb_line)
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
            .padding(top = 24.dp)
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