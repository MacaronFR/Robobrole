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
import androidx.navigation.NavController
import fr.imacaron.robobrole.activity.MainActivity
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.types.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class, DelicateCoroutinesApi::class)
@Composable
fun MatchScreen(matchState: MatchState, db: AppDatabase, nav: NavController, uiState: UIState, current: Long, left: Boolean){
	val size = LocalConfiguration.current.screenWidthDp
	val sizePx = with(LocalDensity.current) { size.dp.toPx() }
	val anchors = mutableMapOf<Float, Int>()
	for( i in matchState.localSummary.indices){
		anchors[i * -sizePx] = i
	}
	val activity = LocalContext.current as MainActivity
	uiState.home = false
	uiState.title = "Match"
	LaunchedEffect(uiState.home){
		GlobalScope.launch(Dispatchers.IO) {
			matchState.loadFromMatch(db.matchDao().get(current))
			matchState.loadEvents(db.eventDAO().getByMatch(matchState.current))
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
							val matchStart = System.currentTimeMillis() / 1000
							matchState.startAt = matchStart
							GlobalScope.launch(Dispatchers.IO){
								db.matchDao().setStart(matchStart, matchState.current)
							}
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
			OutlinedCard(Modifier.padding(0.dp, 8.dp).fillMaxWidth()) {
				Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
					TeamInfo(matchState.local, matchState.localSummary, swipeState.targetValue)
					Column(Modifier.weight(0.10f)) {
						Text("Q${swipeState.targetValue+1}", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineMedium)
					}
					TeamInfo(matchState.visitor, matchState.visitorSummary, swipeState.targetValue)
				}
			}
			OutlinedCard(Modifier.padding(0.dp, 8.dp).fillMaxWidth()) {
				Button({ nav.navigate("stat") }, Modifier.padding(8.dp)){
					Text("Statistique")
				}
			}
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TeamCards(matchState: MatchState, size: Dp, sizePx: Float, swipeState: SwipeableState<Int>, anchors: Map<Float, Int>, left: Boolean){
	Box(Modifier.padding(0.dp, 8.dp).width(size).swipeable(state = swipeState, anchors = anchors, thresholds = { _, _ -> FractionalThreshold(0.3f) }, orientation = Orientation.Horizontal)) {
		for(i in matchState.localSummary.indices){
			Column {
				QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx * i).roundToInt(), 0) }, matchState, matchState.local, i + 1, left, matchState.current)
				QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx * i).roundToInt(), 0) }, matchState, matchState.visitor, i + 1, left, matchState.current)
			}
		}
	}
}