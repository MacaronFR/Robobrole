package fr.imacaron.robobrole

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val textModifier = Modifier.padding(9.dp)

@Composable
fun RowScope.QuartColumn(points: Points, selected: Boolean, text: String, onSelect: () -> Unit) {
    Column(Modifier.weight(1F), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.clickable(onClick = onSelect).height(60.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected, onSelect)
            Text(text, style =  MaterialTheme.typography.headlineMedium)
        }
        Text(points.one.toString(), textModifier, style = MaterialTheme.typography.headlineMedium)
        Text(points.two.toString(), textModifier, style = MaterialTheme.typography.headlineMedium)
        Text(points.three.toString(), textModifier, style = MaterialTheme.typography.headlineMedium)
        Text(points.lucille.toString(), textModifier, style = MaterialTheme.typography.headlineMedium)
        Text("${points.tot()}", textModifier, style = MaterialTheme.typography.headlineMedium)
    }
}