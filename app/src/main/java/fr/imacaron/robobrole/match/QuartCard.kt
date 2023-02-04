package fr.imacaron.robobrole.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.types.Points
import fr.imacaron.robobrole.components.ButtonLong

@Composable
fun TeamText(index: Int){
	Text("Quart temps $index", Modifier.fillMaxWidth().padding(0.dp, 10.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
}

@Composable
fun LabelText(text: String){
	Text(text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PointButton(onClick: () -> Unit, onLongClick: () -> Unit, badgeText: String, content: @Composable RowScope.() -> Unit){
	BadgedBox( { Badge(Modifier.offset((-8).dp, 7.dp)) { Text(badgeText, style = MaterialTheme.typography.titleSmall) } } ){
		ButtonLong(onClick, onLongClick, shape = MaterialTheme.shapes.large, content = content)
	}
}

@Composable
fun QuartCard(modifier: Modifier, points: Points, index: Int){
	val conf = LocalConfiguration.current
	Card(modifier.requiredWidth(conf.screenWidthDp.dp).padding(10.dp, 7.dp)) {
		TeamText(index)
		Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp)) {
			PointButton({ points.one++ }, { if (points.one > 0) points.one-- }, "${points.one}") { LabelText("1 pt") }
			PointButton({ points.two++ }, { if(points.two > 0) points.two-- }, "${points.two}") { LabelText("2 pt") }
			PointButton({ points.three++ }, { if(points.three > 0) points.three-- }, "${points.three}") { LabelText("3 pt") }
			PointButton({ points.lucille++ }, { if(points.lucille > 0) points.lucille-- }, "${points.lucille}") { LabelText("+L") }
		}
	}
}