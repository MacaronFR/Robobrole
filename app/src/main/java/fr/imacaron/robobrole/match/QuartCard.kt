package fr.imacaron.robobrole.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.activity.MainActivity
import fr.imacaron.robobrole.components.ButtonLong
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Event
import fr.imacaron.robobrole.db.Type
import fr.imacaron.robobrole.types.Gender
import fr.imacaron.robobrole.types.MatchState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

@OptIn(DelicateCoroutinesApi::class)
fun pointOnClick(matchState: MatchState, team: String, player: String, match: Long, quart: Int, data: String, db: AppDatabase): () -> Unit = {
	val summary = matchState.getSummary(team, quart)
	when(data){
		"1" -> summary.one++
		"2" -> summary.two++
		"3" -> summary.three++
	}
	val e = Event(Type.Point, team, player, data, (System.currentTimeMillis() / 1000) - matchState.startAt, quart, match)
	GlobalScope.launch(Dispatchers.IO){
		e.uid = db.eventDAO().insertEvent(e)
		matchState.events.add(e)
	}
}

@OptIn(DelicateCoroutinesApi::class)
fun pointOnLongClick(matchState: MatchState, team: String, quart: Int, data: String, db: AppDatabase): () -> Unit = {
	GlobalScope.launch(Dispatchers.IO) {
		val events = db.eventDAO().getSpecificEventDesc(team, quart, Type.Point, data)
		if(events.isNotEmpty()){
			val summary = matchState.getSummary(team, quart)
			when(data){
				"1" -> summary.one--
				"2" -> summary.two--
				"3" -> summary.three--
			}
			matchState.events.removeIf{ it.uid == events[0].uid }
			db.eventDAO().deleteEvent(events[0].uid)
		}
	}
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun QuartCard(modifier: Modifier, matchState: MatchState, team: String, quart: Int, left: Boolean, match: Long){
	val conf = LocalConfiguration.current
	val db = (LocalContext.current as MainActivity).db
	val summary = matchState.getSummary(team, quart)
	OutlinedCard(modifier.requiredWidth(conf.screenWidthDp.dp).padding(8.dp)) {
		Row(Modifier.fillMaxWidth().padding(0.dp, 8.dp), horizontalArrangement = Arrangement.SpaceAround){
			ButtonLong(
				{
					val e = Event(Type.Fault, team, "", "", (System.currentTimeMillis() / 1000) - matchState.startAt, quart, match)
					GlobalScope.launch(Dispatchers.IO) {
						e.uid = db.eventDAO().insertEvent(e)
						matchState.events.add(e)
					}
				},
				{
					val events = db.eventDAO().getFaultDesc(team, quart)
					if(events.isNotEmpty()){
						matchState.events.removeIf { it.uid == events[0].uid }
						db.eventDAO().deleteEvent(events[0].uid)
					}
				},
				Modifier,
				matchState.startAt != 0L && !matchState.done
			){
				Text("Faute")
			}
			ButtonLong(
				{
					val e = Event(Type.Change, team, "", "", (System.currentTimeMillis() / 1000) - matchState.startAt, quart, match)
					GlobalScope.launch(Dispatchers.IO) {
						e.uid = db.eventDAO().insertEvent(e)
						matchState.events.add(e)
					}
				},
				{
					val events = db.eventDAO().getChangeDesc(team, quart)
					if(events.isNotEmpty()){
						matchState.events.removeIf { it.uid == events[0].uid }
						db.eventDAO().deleteEvent(events[0].uid)
					}
				},
				enabled = matchState.startAt != 0L && !matchState.done
			){
				Text("Changement")
			}
		}
		Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp)) {
			listOf("1", "2", "3", "L").forEach {
				PointButton(
					pointOnClick(matchState, team, "", match, quart, it, db),
					pointOnLongClick(matchState, team, quart, it, db),
					matchState.startAt != 0L && !matchState.done,
					matchState.gender == Gender.Women,
					it
				){
					val v = when(it){
						"1" -> summary.one
						"2" -> summary.two
						"3" -> summary.three
						"L" -> summary.player
						else -> throw IllegalArgumentException()
					}
					LabelText("$v")
				}
			}
		}
		TeamText(team, left, quart, summary.total())
	}
}