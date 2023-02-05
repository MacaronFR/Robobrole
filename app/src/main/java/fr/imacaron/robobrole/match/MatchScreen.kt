package fr.imacaron.robobrole.match

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.activity.MainActivity
import fr.imacaron.robobrole.types.AppState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MatchScreen(state: AppState){
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
					Button({}){
						Text("Sauvegarder")
					}
				}
			}
		}
		Box(Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp).width(size.dp).swipeable(state = swipeState, anchors = anchors, thresholds = { a, b -> println("$a, $b"); FractionalThreshold(0.3f) }, orientation = Orientation.Horizontal)) {
			QuartCard(Modifier.offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }, state.local, 0)
			QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx).roundToInt(), 0) }, state.local, 1)
			QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx * 2).roundToInt(), 0) }, state.local, 2)
			QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx * 3).roundToInt(), 0) }, state.local, 3)
		}
		Box(Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp).width(size.dp).swipeable(state = swipeState, anchors = anchors, thresholds = { a, b -> println("$a, $b"); FractionalThreshold(0.3f) }, orientation = Orientation.Horizontal)) {
			QuartCard(Modifier.offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }, state.visitor, 0)
			QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx).roundToInt(), 0) }, state.visitor, 1)
			QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx * 2).roundToInt(), 0) }, state.visitor, 2)
			QuartCard(Modifier.offset { IntOffset((swipeState.offset.value + sizePx * 3).roundToInt(), 0) }, state.visitor, 3)
		}
	}
}