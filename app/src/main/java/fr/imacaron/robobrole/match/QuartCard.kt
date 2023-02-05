package fr.imacaron.robobrole.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.components.ButtonLong
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.MatchEvent
import fr.imacaron.robobrole.db.Type
import fr.imacaron.robobrole.types.Team
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
fun PointButton(onClick: () -> Unit, onLongClick: () -> Unit, badgeText: String, content: @Composable RowScope.() -> Unit){
	BadgedBox( { Badge(Modifier.offset((-8).dp, 7.dp)) { Text(badgeText, style = MaterialTheme.typography.titleSmall) } } ){
		ButtonLong(onClick, onLongClick, shape = MaterialTheme.shapes.large, content = content)
	}
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun QuartCard(modifier: Modifier, team: Team, matchStart: Long, index: Int, db: AppDatabase){
	val conf = LocalConfiguration.current
	Card(modifier.requiredWidth(conf.screenWidthDp.dp).padding(10.dp, 7.dp)) {
		Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp)) {
			listOf("1", "2", "3", "L").forEach {
				PointButton(
					{
						team.scores[index][it] = team.scores[index][it] + 1
						GlobalScope.launch(Dispatchers.IO){
							db.matchDao().insertEvents(MatchEvent(Type.Point, team.name, it, (System.currentTimeMillis() / 1000) - matchStart))
						}
					},
					{
						if(team.scores[index][it] > 0){
							team.scores[index][it] = team.scores[index][it] - 1
						}
					},
					it
				){
					LabelText("${team.scores[index][it]}")
				}
			}
		}
		TeamText(team.name)
	}
}