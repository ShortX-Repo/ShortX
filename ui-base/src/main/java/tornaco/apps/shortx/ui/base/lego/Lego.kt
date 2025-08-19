package tornaco.apps.shortx.ui.base.lego

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.core.proto.lego.LegoButton
import tornaco.apps.shortx.core.proto.lego.LegoColumn
import tornaco.apps.shortx.core.proto.lego.LegoScreen
import tornaco.apps.shortx.core.proto.lego.LegoSwitch
import tornaco.apps.shortx.core.proto.lego.LegoText
import tornaco.apps.shortx.core.proto.lego.LegoTextField
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.thenIf

@Composable
fun LegoScreen(screen: LegoScreen) {
    Surface {
        screen.childList.forEach { child ->
            LegoChild(child)
        }
    }
}

@Composable
fun LegoChild(child: ProtoAny) {
    if (child is_ LegoColumn::class.java) {
        val column = child unpack_ LegoColumn::class.java
        LegoColumn(column)
    }
    if (child is_ LegoText::class.java) {
        val text = child unpack_ LegoText::class.java
        LegoText(text)
    }
    if (child is_ LegoTextField::class.java) {
        val textField = child unpack_ LegoTextField::class.java
        LegoTextField(textField)
    }
    if (child is_ LegoButton::class.java) {
        val button = child unpack_ LegoButton::class.java
    }
    if (child is_ LegoSwitch::class.java) {
        val sw = child unpack_ LegoSwitch::class.java
        LegoSwitch(sw)
    }
}

@Composable
fun LegoColumn(column: LegoColumn) {
    val layoutModifier = column.layoutModifier
    val horizontalAlignment = layoutModifier.horizontalAlignment.composable()
    val verticalArrangement = layoutModifier.verticalArrangement.composable()
    Column(
        modifier = Modifier
            .apply(layoutModifier.modifier)
            .thenIf(
                Modifier.verticalScroll(rememberScrollState()),
                layoutModifier.verticalScroll
            )
            .thenIf(
                Modifier.horizontalScroll(rememberScrollState()),
                layoutModifier.horizontalScroll
            ),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        column.childList.forEach { child ->
            LegoChild(child)
        }
    }
}

@Composable
fun LegoText(text: LegoText) {
    val legoModifier = text.modifier
    Text(
        text.text,
        modifier = Modifier.apply(legoModifier)
    )
}

@Composable
fun LegoTextField(textField: LegoTextField) {
    val legoModifier = textField.modifier
    var value by remember { mutableStateOf(textField.value) }
    ShortXTextField(
        value = value,
        onValueChange = { value = it },
        modifier = Modifier.apply(legoModifier)
    )
}

@Composable
fun LegoSwitch(sw: LegoSwitch) {
    val legoModifier = sw.modifier
    var isCheck by remember {
        mutableStateOf(sw.isChecked)
    }
    ShortXSwitch(
        isCheck,
        onCheckedChange = { isCheck = it },
        modifier = Modifier.apply(legoModifier)
    )
}