package fr.imacaron.robobrole.match

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.types.MatchState
import fr.imacaron.robobrole.types.total
import kotlin.math.max

@Composable
fun StatScreen(state: MatchState){
	Card(Modifier.fillMaxWidth().padding(8.dp)) {
		Canvas(
			Modifier.fillMaxSize(),
		){
			val w = this.size.width
			val h = this.size.height
			val localData = mutableListOf<Offset>()
			val visitorData = mutableListOf<Offset>()
			var vtmp = 0
			var ltmp = 0
			val maxTotal = max(state.myTeamSum.total(), state.otherTeamSum.total())
			state.myTeamSum.forEachIndexed { index, points ->
				ltmp += points.total()
				localData.add(Offset(100f + index * (w-100) / 4, h-(ltmp * (h - 100) / maxTotal)))
			}
			state.otherTeamSum.forEachIndexed { index, points ->
				vtmp += points.total()
				visitorData.add(Offset(100f + index * (w-100) / 4, h-(vtmp * (h - 100) / maxTotal)))
			}
			drawPoints(localData, PointMode.Polygon, Color.Red, strokeWidth = 12f, cap = StrokeCap.Round)
			drawPoints(visitorData, PointMode.Polygon, Color.Blue, strokeWidth = 12f, cap = StrokeCap.Round)
		}
	}
}