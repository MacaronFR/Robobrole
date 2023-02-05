package fr.imacaron.robobrole.match

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.swipeable
import androidx.compose.material.FractionalThreshold
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
import fr.imacaron.robobrole.types.AppState
import fr.imacaron.robobrole.types.Team
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class, DelicateCoroutinesApi::class)
@Composable
fun MatchScreen(state: AppState, db: AppDatabase){
	val size = LocalConfiguration.current.screenWidthDp
	val sizePx = with(LocalDensity.current) { size.dp.toPx() }
	val anchors = mutableMapOf<Float, Int>()
	for( i in state.local.scores.indices){
		anchors[i * -sizePx] = i
	}
	Column {
		val swipeState = rememberSwipeableState(0)
		val activity = LocalContext.current as MainActivity
		Column(Modifier.padding(10.dp, 7.dp).fillMaxWidth().weight(1f)){
			Card(Modifier.padding(0.dp, 8.dp)) {
				Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceAround){
					ElevatedButton(
						{
							val matchStart = System.currentTimeMillis() / 1000
							state.local.matchStart = matchStart
							state.visitor.matchStart = matchStart
							GlobalScope.launch(Dispatchers.IO){
								db.infoDao().setStart(matchStart, state.infoId)
							}
						},
						enabled = state.local.matchStart == 0L && !state.done
					){ Text("Début du match") }
					Button(
						{
							GlobalScope.launch(Dispatchers.IO){
								activity.save(state)
							}
						}
					){
						Text("Sauvegarder")
					}
				}
			}
			Card(Modifier.padding(0.dp, 8.dp).fillMaxWidth()) {
				Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
					TeamInfo(state.local, swipeState.targetValue)
					Column(Modifier.weight(0.10f)) {
						Text("Q${swipeState.targetValue+1}", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineMedium)
					}
					TeamInfo(state.visitor, swipeState.targetValue)
				}
			}
		}
		Column(Modifier.padding(0.dp, 8.dp)) {
			TeamCards(state.local, size.dp, sizePx, swipeState, anchors, state.gender == "F", state.left, state.done)
			TeamCards(state.visitor, size.dp, sizePx, swipeState, anchors, state.gender == "F", state.left, state.done)
		}
	}
}

@Composable
fun RowScope.TeamInfo(team: Team, quart: Int){
	Column(Modifier.weight(0.45f)) {
		Text(team.name, Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimaryContainer, textAlign = TextAlign.Center)
		Text(team.total().toString(), Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
		Text(team.scores[quart].tot().toString(), Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TeamCards(team: Team, size: Dp, sizePx: Float, swipeState: SwipeableState<Int>, anchors: Map<Float, Int>, women: Boolean, left: Boolean, done: Boolean){
	Box(Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp).width(size).swipeable(state = swipeState, anchors = anchors, thresholds = { _, _ -> FractionalThreshold(0.3f) }, orientation = Orientation.Horizontal)) {
		for(i in team.scores.indices){
			QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx * i).roundToInt(), 0) }, team, i, women, left, done)
		}
	}
}