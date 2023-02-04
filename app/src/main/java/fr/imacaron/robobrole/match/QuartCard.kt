package fr.imacaron.robobrole.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.types.Points
import fr.imacaron.robobrole.components.ButtonLong

@Composable
fun ScoreText(text: String){
	Text(text, style = MaterialTheme.typography.titleLarge)
}

@Composable
fun TotalText(index: Int, score: Int){
	Text(buildAnnotatedString {
		append("Q $index : ")
		withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)){
			append(score.toString())
		}
	}, Modifier.fillMaxWidth().padding(0.dp, 10.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
}

@Composable
fun LabelText(text: String){
	Text(text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun QuartCard(points: Points, index: Int){
	Card(Modifier.padding(10.dp, 7.dp)) {
		TotalText(index, points.tot())
		Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(16.dp, 6.dp)) {
			ScoreText("${points.one}")
			ScoreText("${points.two}")
			ScoreText("${points.three}")
			ScoreText("${points.lucille}")
		}
		Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(16.dp, 6.dp)) {
			ButtonLong({ points.one++ }, { if(points.one > 0) points.one-- }) { LabelText("+1") }
			ButtonLong({ points.two++ }, { if(points.two > 0) points.two-- }) { LabelText("+2") }
			ButtonLong({ points.three++ }, { if(points.three > 0) points.three-- }) { LabelText("+3") }
			ButtonLong({ points.lucille++ }, { if(points.lucille > 0) points.lucille-- }) { LabelText("+L") }
		}
	}
}