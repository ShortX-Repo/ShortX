package tornaco.apps.shortx.ui.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.proto.action.RemoteExecuteMVEL

@DoNotStrip
@Parcelize
data class RemoteExecuteMVELAction(
    val title: String,
    val description: String,
    val icon: String,
    val expression: String,
) : Parcelable

fun RemoteExecuteMVELAction.toProto(): RemoteExecuteMVEL =
    RemoteExecuteMVEL
        .newBuilder()
        .setTitle(title)
        .setDescription(description)
        .setIcon(icon)
        .setExpression(expression)
        .build()

fun RemoteExecuteMVEL.toModel() = RemoteExecuteMVELAction(
    title = title,
    description = description,
    icon = icon,
    expression = expression
)
