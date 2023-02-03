package fr.imacaron.robobrole

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ScoreText(text: String){
	Text(text, style = MaterialTheme.typography.titleLarge)
}

@Composable
fun TotalText(score: Int){
	Text("Total : $score", Modifier.fillMaxWidth().padding(0.dp, 10.dp), style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
}

@Composable
fun LabelText(text: String){
	Text(text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun QuartCard(points: Points, index: Int){
	Card(Modifier.padding(10.dp, 10.dp)) {
		Text("Quart temps n°$index", Modifier.fillMaxWidth().padding(0.dp, 15.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
		Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)) {
			ButtonLong({ points.one++ }, { if(points.one > 0) points.one-- }) { LabelText("+1") }
			ButtonLong({ points.two++ }, { if(points.two > 0) points.two-- }) { LabelText("+2") }
			ButtonLong({ points.three++ }, { if(points.three > 0) points.three-- }) { LabelText("+3") }
			ButtonLong({ points.lucille++ }, { if(points.lucille > 0) points.lucille-- }) { LabelText("+L") }
		}
		Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)) {
			ScoreText("${points.one}")
			ScoreText("${points.two}")
			ScoreText("${points.three}")
			ScoreText("${points.lucille}")
		}
		Divider(Modifier.fillMaxWidth().padding(16.dp, 8.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
		TotalText(points.tot())
	}
}