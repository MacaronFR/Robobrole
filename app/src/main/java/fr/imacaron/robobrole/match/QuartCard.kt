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
fun TeamText(name: String){
	Text(name, Modifier.fillMaxWidth().padding(0.dp, 10.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
}

@Composable
fun LabelText(text: String){
	Text(text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PointButton(onClick: () -> Unit, onLongClick: () -> Unit, enabled: Boolean, badgeText: String, content: @Composable RowScope.() -> Unit){
	BadgedBox( { Badge(Modifier.offset((-8).dp, 7.dp)) { Text(badgeText, style = MaterialTheme.typography.titleSmall) } } ){
		ButtonLong(onClick, onLongClick, enabled = enabled, shape = MaterialTheme.shapes.large, content = content)
	}
}

@Composable
fun QuartCard(modifier: Modifier, team: Team, index: Int){
	val conf = LocalConfiguration.current
	Card(modifier.requiredWidth(conf.screenWidthDp.dp).padding(10.dp, 7.dp)) {
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
					team.matchStart != 0L,
					it
				){
					LabelText("${team.scores[index][it].value}")
				}
			}
		}
		TeamText(team.name)
	}
}