package tornaco.apps.shortx.ui.base.lego

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.lego.LegoModifier
import tornaco.apps.shortx.core.proto.lego.LegoModifier_Size_Fill_Max
import tornaco.apps.shortx.core.proto.lego.LegoModifier_Size_Static
import tornaco.apps.shortx.core.proto.lego.LegoModifier_Size_Wrap_Content
import tornaco.apps.shortx.core.proto.lego.Padding
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_

fun tornaco.apps.shortx.core.proto.lego.Arrangement.composable(): Arrangement.Vertical {
    return when (this) {
        tornaco.apps.shortx.core.proto.lego.Arrangement.ARRANGEMENT_TOP -> {
            Arrangement.Top
        }

        tornaco.apps.shortx.core.proto.lego.Arrangement.ARRANGEMENT_CENTER -> {
            Arrangement.Center
        }

        tornaco.apps.shortx.core.proto.lego.Arrangement.ARRANGEMENT_BOTTOM -> {
            Arrangement.Bottom
        }

        tornaco.apps.shortx.core.proto.lego.Arrangement.ARRANGEMENT_SPACE_BETWEEN -> {
            Arrangement.SpaceBetween
        }

        tornaco.apps.shortx.core.proto.lego.Arrangement.ARRANGEMENT_SPACE_EVENLY -> {
            Arrangement.SpaceEvenly
        }

        tornaco.apps.shortx.core.proto.lego.Arrangement.ARRANGEMENT_SPACE_AROUND -> {
            Arrangement.SpaceAround
        }

        tornaco.apps.shortx.core.proto.lego.Arrangement.UNRECOGNIZED -> {
            Arrangement.Top
        }
    }
}

fun tornaco.apps.shortx.core.proto.lego.Alignment.composable(): Alignment.Horizontal {
    return when (this) {
        tornaco.apps.shortx.core.proto.lego.Alignment.ALIGNMENT_START -> {
            Alignment.Start
        }

        tornaco.apps.shortx.core.proto.lego.Alignment.ALIGNMENT_END -> {
            Alignment.End
        }

        tornaco.apps.shortx.core.proto.lego.Alignment.ALIGNMENT_CENTER_HORIZONTALLY -> {
            Alignment.CenterHorizontally
        }

        tornaco.apps.shortx.core.proto.lego.Alignment.UNRECOGNIZED -> {
            Alignment.Start
        }
    }
}

fun Modifier.applyHeight(legoSize: ProtoAny): Modifier {
    if (legoSize is_ LegoModifier_Size_Fill_Max::class.java) {
        val height = legoSize unpack_ LegoModifier_Size_Fill_Max::class.java
        val fraction = height.fraction
        return if (fraction > 0 && fraction <= 1) {
            this.fillMaxHeight(fraction)
        } else {
            this.fillMaxHeight()
        }
    } else if (legoSize is_ LegoModifier_Size_Wrap_Content::class.java) {
        return this.wrapContentHeight()
    } else if (legoSize is_ LegoModifier_Size_Static::class.java) {
        val ss = legoSize unpack_ LegoModifier_Size_Static::class.java
        return this.height(height = ss.size.dp)
    } else {
        return this
    }
}

fun Modifier.applyWidth(legoSize: ProtoAny): Modifier {
    if (legoSize is_ LegoModifier_Size_Fill_Max::class.java) {
        val height = legoSize unpack_ LegoModifier_Size_Fill_Max::class.java
        val fraction = height.fraction
        return if (fraction > 0 && fraction <= 1) {
            this.fillMaxWidth(fraction)
        } else {
            this.fillMaxWidth()
        }
    } else if (legoSize is_ LegoModifier_Size_Wrap_Content::class.java) {
        return this.wrapContentWidth()
    } else if (legoSize is_ LegoModifier_Size_Static::class.java) {
        val ss = legoSize unpack_ LegoModifier_Size_Static::class.java
        return this.width(width = ss.size.dp)
    } else {
        return this
    }
}

fun Modifier.applyPadding(padding: Padding): Modifier {
    return this.padding(
        top = padding.top.dp,
        bottom = padding.bottom.dp,
        start = padding.start.dp,
        end = padding.end.dp
    )
}

fun Modifier.apply(legoModifier: LegoModifier): Modifier {
    return this
        .applyPadding(legoModifier.margin)
        .applyWidth(legoModifier.width)
        .applyHeight(legoModifier.height)
        .applyPadding(legoModifier.padding)
}