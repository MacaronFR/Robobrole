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
fun ScoreText(text: String){
	Text(text, style = MaterialTheme.typography.titleLarge)
}

@Composable
fun TotalText(index: Int){
	Text("Quart temps $index", Modifier.fillMaxWidth().padding(0.dp, 10.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
}

@Composable
fun LabelText(text: String){
	Text(text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun QuartCard(modifier: Modifier, points: Points, index: Int){
	val conf = LocalConfiguration.current
	Card(modifier.requiredWidth(conf.screenWidthDp.dp).padding(10.dp, 7.dp)) {
		TotalText(index)
		Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(16.dp, 6.dp)) {
			ScoreText("${points.one}")
			ScoreText("${points.two}")
			ScoreText("${points.three}")
			ScoreText("${points.lucille}")
		}
		Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp)) {
			ButtonLong({ points.one++ }, { if(points.one > 0) points.one-- }) { LabelText("1 pt") }
			ButtonLong({ points.two++ }, { if(points.two > 0) points.two-- }) { LabelText("2 pt") }
			ButtonLong({ points.three++ }, { if(points.three > 0) points.three-- }) { LabelText("3 pt") }
			ButtonLong({ points.lucille++ }, { if(points.lucille > 0) points.lucille-- }) { LabelText("+L") }
		}
	}
}