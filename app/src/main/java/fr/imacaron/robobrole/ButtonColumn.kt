package fr.imacaron.robobrole

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val buttonModifier = Modifier
private val textModifier = Modifier.padding(9.dp, 16.dp)

@Composable
fun RowScope.ButtonColumn(points: List<Points>, quart: Int){
    Column(Modifier.weight(1F)) {
        Spacer(Modifier.height(60.dp))
        ButtonLong({ points[quart].one++ }, { if(points[quart].one > 0) points[quart].one-- }, buttonModifier){
            Text("+1", style = MaterialTheme.typography.headlineSmall)
        }
        ButtonLong({points[quart].two++}, { if(points[quart].two > 0) points[quart].two-- }, buttonModifier){
            Text("+2", style = MaterialTheme.typography.headlineSmall)
        }
        ButtonLong({points[quart].three++}, { if(points[quart].three > 0) points[quart].three-- }, buttonModifier){
            Text("+3", style = MaterialTheme.typography.headlineSmall)
        }
        ButtonLong({points[quart].lucille++}, { if(points[quart].lucille > 0) points[quart].lucille-- }, buttonModifier){
            Text("+L", style = MaterialTheme.typography.headlineSmall)
        }
        Text("Total : ", textModifier, style = MaterialTheme.typography.headlineSmall)
    }
}