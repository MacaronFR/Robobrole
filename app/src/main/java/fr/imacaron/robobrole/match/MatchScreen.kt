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
		ElevatedCard(Modifier.padding(10.dp, 7.dp).fillMaxWidth().weight(1f)){
			Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
				Column(Modifier.padding(0.dp, 8.dp)) {
					Text("${state.local.scores.sumOf { it.tot() }} Q${swipeState.currentValue + 1} ${state.visitor.scores.sumOf { it.tot() }}", Modifier.fillMaxWidth().padding(16.dp, 6.dp), color = MaterialTheme.colorScheme.onPrimaryContainer, textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
					Text("${state.local.scores[swipeState.currentValue].tot()}    ${state.visitor.scores[swipeState.currentValue].tot()}", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineMedium)
				}
				Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
					ElevatedButton(
						{
							val matchStart = System.currentTimeMillis() / 1000
							state.local.matchStart = matchStart
							state.visitor.matchStart = matchStart
							GlobalScope.launch(Dispatchers.IO){
								db.infoDao().setStart(matchStart, state.infoId)
							}
						},
						enabled = state.local.matchStart == 0L
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
		}
		TeamCards(state.local, size.dp, sizePx, swipeState, anchors)
		TeamCards(state.visitor, size.dp, sizePx, swipeState, anchors)
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TeamCards(team: Team, size: Dp, sizePx: Float, swipeState: SwipeableState<Int>, anchors: Map<Float, Int>){
	Box(Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp).width(size).swipeable(state = swipeState, anchors = anchors, thresholds = { _, _ -> FractionalThreshold(0.3f) }, orientation = Orientation.Horizontal)) {
		for(i in team.scores.indices){
			QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx * i).roundToInt(), 0) }, team, i)
		}
	}
}