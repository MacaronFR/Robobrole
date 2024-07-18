@file:Suppress("FunctionName")

package fr.imacaron.robobrole.match.stat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.components.Select
import fr.imacaron.robobrole.db.Type
import fr.imacaron.robobrole.match.defaultModifier
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
		topBar = {
			TopAppBar({ Text("Statistique") }, navigationIcon = {
				IconButton({ navigator.navigateUp() }) {
					Icon(
						Icons.Outlined.ArrowBack, "back"
					)
				}
			})
		}
	) {
		Column(Modifier.padding(it)) {
			var joueuse: PlayerStat? by remember { mutableStateOf(null) }
			var open by remember { mutableStateOf(false) }
			Card(Modifier.padding(horizontal = 16.dp)) {
				Text(
					"Score de l'équipe",
					Modifier.padding(top = 8.dp, start = 16.dp),
					style = MaterialTheme.typography.headlineMedium
				)
				service.myTeamSummary.apply {
					Stat(
						sumOf { it.one },
						sumOf { it.two },
						sumOf { it.three },
						sumOf { it.one + it.two + it.three },
						sumOf { it.total() },
					)
				}
			}
			Card(defaultModifier) {
				Select(
					joueuse?.name ?: "",
					open,
					label = { Text("Joueur") },
					onDismissRequest = { open = false },
					onOpenRequest = { open = true }
				) {
					values.forEach {
						DropdownMenuItem({ Text(it.name) }, { open = false; joueuse = it })
					}
				}
				if (joueuse != null) {
					Stat(
						one = joueuse?.one ?: 0,
						two = joueuse?.two ?: 0,
						three = joueuse?.three ?: 0,
						total = joueuse?.total ?: 0,
						points = joueuse?.points ?: 0
					)
				}
			}
		}
	}
}

@Composable
fun Stat(one: Int, two: Int, three: Int, total: Int, points: Int) {
	Row(defaultModifier) {
		Column(Modifier.padding(end = 4.dp), horizontalAlignment = Alignment.End) {
			Text("1 pt")
			Text("2 pt")
			Text("3 pt")
			Text("Tot Panier")
			Text("Tot points")
		}
		Column {
			Text(one.toString())
			Text(two.toString())
			Text(three.toString())
			Text(total.toString())
			Text(points.toString())
		}
	}
}