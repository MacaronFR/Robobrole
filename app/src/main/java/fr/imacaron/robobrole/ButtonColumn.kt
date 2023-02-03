package fr.imacaron.robobrole

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val buttonModifier = Modifier.padding(6.5.dp)
private val textModifier = Modifier.padding(9.dp, 16.dp)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.ButtonColumn(points: List<Points>, quart: Int){
    Column(Modifier.weight(1F)) {
        println(quart)
        Spacer(Modifier.height(60.dp))
        PressableButton(buttonModifier, { points[quart].one++ }, { if(points[quart].one > 0) points[quart].one-- }){
            Text("+1", style = MaterialTheme.typography.h5)
        }
        PressableButton(buttonModifier, {points[quart].two++}, { if(points[quart].two > 0) points[quart].two-- }){
            Text("+2", style = MaterialTheme.typography.h5)
        }
        PressableButton(buttonModifier, {points[quart].three++}, { if(points[quart].three > 0) points[quart].three-- }){
            Text("+3", style = MaterialTheme.typography.h5)
        }
        PressableButton(buttonModifier, {points[quart].lucille++}, { if(points[quart].lucille > 0) points[quart].lucille-- }){
            Text("+L", style = MaterialTheme.typography.h5)
        }
        Text("Total : ", textModifier, style = MaterialTheme.typography.h5)
    }
}