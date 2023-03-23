@file:Suppress("FunctionName")

package fr.imacaron.robobrole.match.stat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.imacaron.robobrole.db.Type
import fr.imacaron.robobrole.service.MatchService
import fr.imacaron.robobrole.service.NavigationService

data class PlayerStat(
	val name: String,
	var one: Int,
	var two: Int,
	var three: Int,
	var total: Int,
	var points: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatScreen(service: MatchService, navigator: NavigationService) {
	BackHandler(true) { navigator.navigateUp() }
	val values = service.players.sortedBy { it.player.name }.map { PlayerStat(it.player.name, 0, 0, 0, 0, 0) }
	service.events.forEach { ev ->
		if(ev.type == Type.Point && ev.team == service.myTeam){
			values.find { it.name == ev.player }?.let { stat ->
				when(ev.data) {
					"1" -> {
						stat.one++
						stat.points += 1
					}
					"2" -> {
						stat.two++
						stat.points += 2
					}
					"3" -> {
						stat.three++
						stat.points += 3
					}
				}
				stat.total++
			}
		}
	}
	Scaffold(
		topBar = { TopAppBar({ Text("Statistique") }, navigationIcon = { IconButton({ navigator.navigateUp() }){ Icon(
			Icons.Outlined.ArrowBack, "back") } }) }
	) {
		LazyVerticalGrid(GridCells.Fixed(6), Modifier.padding(it)) {
			item {
				Text("Joueuse")
			}
			item {
				Text("1 pt")
			}
			item {
				Text("2 pts")
			}
			item {
				Text("3 pts")
			}
			item {
				Text("Tot Panier")
			}
			item {
				Text("Tot points")
			}
			values.forEach {
				item {
					Text(it.name)
				}
				item {
					Text(it.one.toString())
				}
				item {
					Text(it.two.toString())
				}
				item {
					Text(it.three.toString())
				}
				item {
					Text(it.total.toString())
				}
				item {
					Text(it.points.toString())
				}
			}
		}
	}
}