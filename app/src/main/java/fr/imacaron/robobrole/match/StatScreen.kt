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
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.types.AppState
@Composable
fun StatScreen(state: AppState, db: AppDatabase){
	Card(Modifier.fillMaxWidth().padding(8.dp)) {
		Canvas(
			Modifier.fillMaxSize(),
		){
			val w = this.size.width
			val h = this.size.height
			val localData = mutableListOf<Offset>()
			val visitorData = mutableListOf<Offset>()
			val localTotal = state.local.total()
			val visitorTotal = state.visitor.total()
			state.local.scores.forEachIndexed { index, points ->
				localData.add(Offset(100f + index * (w-100) / 4, points.tot() * h / localTotal))
			}
			state.visitor.scores.forEachIndexed { index, points ->
				visitorData.add(Offset(100f + index * (w-100) / 4, points.tot() * h / visitorTotal))
			}
			drawPoints(localData, PointMode.Polygon, Color.Red, strokeWidth = 12f, cap = StrokeCap.Round)
			drawPoints(visitorData, PointMode.Polygon, Color.Blue, strokeWidth = 12f, cap = StrokeCap.Round)
		}
	}
}