package fr.imacaron.robobrole.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.imacaron.robobrole.types.Points

@Composable
fun MatchScreen(points: List<Points>){
	Column(Modifier.fillMaxHeight().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Top) {
		QuartCard(points[0], 1)
		QuartCard(points[1], 2)
		QuartCard(points[2], 3)
		QuartCard(points[3], 4)
	}
}