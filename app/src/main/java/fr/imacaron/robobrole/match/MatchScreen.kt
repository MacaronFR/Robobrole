package fr.imacaron.robobrole.match

import android.widget.Toast
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.swipeable
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.activity.MainActivity
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.MatchPlayer
import fr.imacaron.robobrole.db.Player
import fr.imacaron.robobrole.types.*
import kotlinx.coroutines.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class, DelicateCoroutinesApi::class)
@Composable
fun MatchScreen(matchState: MatchState, db: AppDatabase, uiState: UIState, current: Long, left: Boolean){
	val size = LocalConfiguration.current.screenWidthDp
	val sizePx = with(LocalDensity.current) { size.dp.toPx() }
	var start: Boolean by remember { mutableStateOf(false) }
	var change: Boolean by remember { mutableStateOf(false) }
	val anchors = mutableMapOf<Float, Int>()
	for( i in matchState.myTeamSum.indices){
		anchors[i * -sizePx] = i
	}
	val activity = LocalContext.current as MainActivity
	uiState.home = false
	uiState.title = "Match"
	LaunchedEffect(uiState.home){
		withContext(Dispatchers.IO) {
			matchState.loadFromMatch(db.matchDao().get(current))
			matchState.loadEvents(db.eventDAO().getByMatch(matchState.current))
			matchState.players.clear()
			matchState.loadPlayers(db)
			uiState.export = matchState.done
		}
	}
	Column {
		val swipeState = rememberSwipeableState(0)
		Column(Modifier.padding(10.dp, 7.dp).fillMaxWidth().weight(1f)){
			OutlinedCard(Modifier.padding(0.dp, 8.dp)) {
				Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceAround){
					ElevatedButton(
						{
							start = true
						},
						enabled = matchState.startAt == 0L && !matchState.done
					){ Text("Début du match") }
					Button(
						{
							matchState.done = true
							uiState.export = true
							GlobalScope.launch(Dispatchers.IO){
								db.matchDao().setDone(matchState.current)
								activity.save(matchState)
							}
						},
						enabled = !matchState.done && matchState.startAt != 0L
					){
						Text("Sauvegarder")
					}
				}
			}
			Button({ change = true }, enabled = matchState.startAt != 0L){ Text("Changement") }
			OutlinedCard(Modifier.padding(0.dp, 8.dp).fillMaxWidth()) {
				Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
					TeamInfo(matchState.myTeam, matchState.myTeamSum, swipeState.targetValue)
					Column(Modifier.weight(0.10f)) {
						Text("Q${swipeState.targetValue+1}", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineMedium)
					}
					TeamInfo(matchState.otherTeam, matchState.otherTeamSum, swipeState.targetValue)
				}
			}
		}
		if(start){
			TeamSelector({ start = false }, {
				start = false
				val matchStart = System.currentTimeMillis() / 1000
				matchState.startAt = matchStart
				GlobalScope.launch(Dispatchers.IO){
					db.matchDao().setStart(matchStart, matchState.current)
				}
			}, matchState.players)
		}
		if(change){
			TeamSelector(
				{ change = false },
				{ change = false },
				matchState.players
			)
		}
		TeamCards(matchState, size.dp, sizePx, swipeState, anchors, left)
	}
}

@Composable
fun RowScope.TeamInfo(name: String, summary: List<Summary>, quart: Int){
	Column(Modifier.weight(0.45f)) {
		Text(name, Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimaryContainer, textAlign = TextAlign.Center)
		Text(summary.total().toString(), Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
		Text(summary[quart].total().toString(), Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
	}
}

@Composable
fun TeamSelector(onDismiss: () -> Unit, onConfirm: () -> Unit, players: List<PlayerMatch>){
	val context = LocalContext.current
	AlertDialog(
		onDismiss,
		{ Button(
			{
				if(players.filter { it.onMatch }.size != 5){
					Toast.makeText(context, "Selctionnez 5 joueur", Toast.LENGTH_SHORT).show()
				}else{
					onConfirm()
				}
			}
		) {
			Icon(Icons.Outlined.Check, null)
			Text("Valider")
		} },
		title = { Text("Sélection Équipe") },
		text = {
			LazyColumn {
				itemsIndexed(players){ i, p ->
					Row {
						Checkbox(p.onMatch, { players[i] onMatch it})
						Text(p.player.name)
					}
				}
			}
		}
	)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TeamCards(matchState: MatchState, size: Dp, sizePx: Float, swipeState: SwipeableState<Int>, anchors: Map<Float, Int>, left: Boolean){
	Box(Modifier.padding(0.dp, 8.dp).width(size).swipeable(state = swipeState, anchors = anchors, thresholds = { _, _ -> FractionalThreshold(0.3f) }, orientation = Orientation.Horizontal)) {
		for(i in matchState.myTeamSum.indices){
			Column {
				QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx * i).roundToInt(), 0) }, matchState, true, i + 1, left)
				QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx * i).roundToInt(), 0) }, matchState, false, i + 1, left)
			}
		}
	}
}