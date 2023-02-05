package fr.imacaron.robobrole.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.components.ButtonLong
import fr.imacaron.robobrole.types.Team

@Composable
fun TeamText(name: String, left: Boolean, index: Int, score: Int){
	Row(Modifier.padding(0.dp, 10.dp)) {
		Text(if(!left) "$score" else "Q$index", Modifier.weight(0.1f), color = MaterialTheme.colorScheme.onSurface.copy(0.6f), textAlign = TextAlign.End, style = MaterialTheme.typography.headlineSmall)
		Text(name, Modifier.weight(0.8f), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
		Text(if(left) "$score" else "Q$index", Modifier.weight(0.1f), color = MaterialTheme.colorScheme.onSurface.copy(0.6f), style = MaterialTheme.typography.headlineSmall)
	}
}

@Composable
fun LabelText(text: String){
	Text(text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PointButton(onClick: () -> Unit, onLongClick: () -> Unit, enabled: Boolean, women: Boolean, badgeText: String, content: @Composable RowScope.() -> Unit){
	val (contentColor, containerColor) = if(enabled)
		if(women)
			MaterialTheme.colorScheme.onSecondaryContainer to MaterialTheme.colorScheme.secondaryContainer
		else
			MaterialTheme.colorScheme.onTertiaryContainer to MaterialTheme.colorScheme.tertiaryContainer
	else
		MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) to MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
	BadgedBox( { Badge(Modifier.offset((-8).dp, 7.dp)) { Text(badgeText, style = MaterialTheme.typography.titleSmall) } } ){
		ButtonLong(onClick, onLongClick, enabled = enabled, shape = MaterialTheme.shapes.large, contentColor = contentColor, containerColor = containerColor, content = content)
	}
}

@Composable
fun QuartCard(modifier: Modifier, team: Team, index: Int, women: Boolean, left: Boolean, done: Boolean){
	val conf = LocalConfiguration.current
	Card(modifier.requiredWidth(conf.screenWidthDp.dp).padding(10.dp, 0.dp)) {
		Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp)) {
			listOf("1", "2", "3", "L").forEach {
				PointButton(
					{
						team.scores[index].apply {
							when(it){
								"1" -> one++
								"2" -> two++
								"3" -> three++
								"L" -> lucille++
							}
						}
					},
					{
						if(team.scores[index][it] > 0){
							team.scores[index].apply {
								when(it){
									"1" -> one--
									"2" -> two--
									"3" -> three--
									"L" -> lucille--
								}
							}
						}
					},
					team.matchStart != 0L && !done,
					women,
					it
				){
					LabelText("${team.scores[index][it].value}")
				}
			}
		}
		TeamText(team.name, left, index + 1, team.scores[index].tot())
	}
}