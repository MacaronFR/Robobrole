package fr.imacaron.robobrole.match

import android.widget.Toast
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.swipeable
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.imacaron.robobrole.components.ButtonLong
import fr.imacaron.robobrole.service.MatchService
import fr.imacaron.robobrole.service.ShareDownloadService
import fr.imacaron.robobrole.state.*
import fr.imacaron.robobrole.types.*
import kotlinx.coroutines.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchAppBar(service: MatchService, navController: NavController, shareDownload: ShareDownloadService){
	TopAppBar(
		{ Text("${service.myTeam} - ${service.otherTeam}") },
		navigationIcon = {
			IconButton({ navController.navigateUp() }){
				Icon(Icons.Outlined.ArrowBack, "Arrow Back")
			}
		},
		actions = {
			if(service.done){
				IconButton({
					shareDownload.download(service.state)
				}){
					Icon(Icons.Outlined.Download, "Download")
				}
			}else {
				IconButton({
					service.save()
				}){
					Icon(Icons.Outlined.Save, "Save")
				}
			}
		}
	)
}

@Composable
fun MatchFab(service: MatchService, shareDownload: ShareDownloadService){
	if(service.done){
		FloatingActionButton({ shareDownload.share(service.state) }){ Icon(Icons.Outlined.Share, "Share") }
	}else if(!service.start){
		FloatingActionButton({ service.start(System.currentTimeMillis() / 1000) }){ Icon(Icons.Outlined.PlayArrow, "Start") }
	}
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MatchScreen(navController: NavController, matchService: MatchService, current: Long, shareDownload: ShareDownloadService){
	val size = LocalConfiguration.current.screenWidthDp
	val sizePx = with(LocalDensity.current) { size.dp.toPx() }
	val anchors = mutableMapOf<Float, Int>()
	for (i in matchService.myTeamSummary.indices) {
		anchors[i * -sizePx] = i
	}
	LaunchedEffect(current){
		matchService.loadMatch(current)
	}
	val swipeState = rememberSwipeableState(0) {
		matchService.quart = it
		true
	}
	Scaffold(
		topBar = { MatchAppBar(matchService, navController, shareDownload) },
		floatingActionButton = { MatchFab(matchService, shareDownload) },
	) {
		Column(Modifier.padding(it).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
			MatchBoard(matchService)
			FauteChange(matchService)
			Box(
				Modifier.padding(0.dp, 8.dp).width(size.dp).swipeable(
					state = swipeState,
					anchors = anchors,
					thresholds = { _, _ -> FractionalThreshold(0.3f) },
					orientation = Orientation.Horizontal
				)
			) {
				for (i in matchService.myTeamSummary.indices) {
					ElevatedCard(
						Modifier.requiredWidth(size.dp).padding(horizontal = 8.dp).offset { IntOffset((swipeState.offset.value + sizePx * i).roundToInt(), 0) }
					){
						Teams(matchService, i)
					}
				}
			}
		}
	}
}

@Composable
fun MatchBoard(service: MatchService){
	Row(verticalAlignment = Alignment.CenterVertically) {
		TeamInfo(service.myTeam, service.myTeamSummary, service.quart)
		Column(Modifier.weight(0.10f)) {
			Text("Q${service.quart + 1}", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineMedium)
		}
		TeamInfo(service.otherTeam, service.otherTeamSummary, service.quart)
	}
}

@Composable
fun FauteChange(service: MatchService){
	var teamSelector: Boolean by remember { mutableStateOf(false) }
	Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
		Button({}){ Text("Faute") }
		Button({ teamSelector = true }, /*enabled = service.start && !service.done*/){ Text("Changement") }
		if(teamSelector){
			TeamSelector(
				{ teamSelector = false },
				{ teamSelector = false },
				{ inMatch, player ->
					if(inMatch){
						service.changeIn(player)
					}else{
						service.changeOut(player)
					}
				},
				service.players
			)
		}
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
fun Teams(service: MatchService, quart: Int){
	Column {
		MyTeam(service, quart)
		Divider(Modifier.padding(16.dp))
		OtherTeam(service, quart)
	}
}

@Composable
fun MyTeam(service: MatchService, quart: Int){
	var displaySelector: Boolean by remember { mutableStateOf(false) }
	var amount: Int by remember { mutableStateOf(1) }
	val team = service.myTeam
	val summary = service.myTeamSummary[quart]
	Column {
		Row(Modifier.fillMaxWidth().padding(16.dp, 16.dp), horizontalArrangement = Arrangement.SpaceAround){
			PointButton({ displaySelector = true; amount = 1 }, { service.removePoint(1, team) }, service.start && !service.done, "1"){ Text(summary.one.toString()) }
			PointButton({ displaySelector = true; amount = 2 }, { service.removePoint(2, team) }, service.start && !service.done, "2"){ Text(summary.two.toString()) }
			PointButton({ displaySelector = true; amount = 3 }, { service.removePoint(3, team) }, service.start && !service.done, "3"){ Text(summary.three.toString()) }
		}
		Row{
			Text("Q${service.quart+1}", Modifier.weight(0.1f), color = MaterialTheme.colorScheme.onSurface.copy(0.6f), textAlign = TextAlign.End, style = MaterialTheme.typography.headlineSmall)
			Text(team, Modifier.weight(0.8f), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
			Text(summary.total().toString(), Modifier.weight(0.1f), color = MaterialTheme.colorScheme.onSurface.copy(0.6f), style = MaterialTheme.typography.headlineSmall)
		}
	}
	if(displaySelector){
		PlayerSelector(service.players, { displaySelector = false }){
			service.addPoint(amount, team, it)
		}
	}
}

@Composable
fun PlayerSelector(players: List<PlayerMatch>, onDismiss: () -> Unit, onSelect: (player: String) -> Unit){
	AlertDialog(onDismiss, {}, title = { Text("Joueuse") }, text = {
		LazyVerticalGrid(
			GridCells.Fixed(2), Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8
			.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
			items(players.filter { it.onMatch }){
				TextButton(
					{
						onSelect(it.player.name)
						onDismiss()
					}
				){
					Text(it.player.name, style = MaterialTheme.typography.headlineSmall)
				}
			}
		}
	})
}

@Composable
fun OtherTeam(service: MatchService, quart: Int){
	val team = service.otherTeam
	val summary = service.otherTeamSummary[quart]
	Column {
		Row(Modifier.fillMaxWidth().padding(16.dp, 16.dp), horizontalArrangement = Arrangement.SpaceAround){
			PointButton({ service.addPoint(1, team) }, { service.removePoint(1, team) }, service.start && !service.done, "1"){ Text(summary.one.toString()) }
			PointButton({ service.addPoint(1, team) }, { service.removePoint(1, team) }, service.start && !service.done, "2"){ Text(summary.two.toString()) }
			PointButton({ service.addPoint(1, team) }, { service.removePoint(1, team) }, service.start && !service.done, "3"){ Text(summary.three.toString()) }
		}
		Row(Modifier.padding(bottom = 8.dp)){
			Text("Q${service.quart+1}", Modifier.weight(0.1f), color = MaterialTheme.colorScheme.onSurface.copy(0.6f), textAlign = TextAlign.End, style = MaterialTheme.typography.headlineSmall)
			Text(team, Modifier.weight(0.8f), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
			Text(summary.total().toString(), Modifier.weight(0.1f), color = MaterialTheme.colorScheme.onSurface.copy(0.6f), style = MaterialTheme.typography.headlineSmall)
		}
	}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PointButton(onClick: () -> Unit, onLongClick: () -> Unit, enabled: Boolean, badgeText: String, content: @Composable RowScope.() -> Unit){
	val (contentColor, containerColor) = if(enabled)
		MaterialTheme.colorScheme.onSecondaryContainer to MaterialTheme.colorScheme.secondaryContainer
	else
		MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) to MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
	BadgedBox( { Badge(Modifier.offset((-8).dp, 7.dp)) { Text(badgeText, style = MaterialTheme.typography.titleSmall) } } ){
		ButtonLong(onClick, onLongClick, enabled = enabled, shape = MaterialTheme.shapes.large, contentColor = contentColor, containerColor = containerColor, content = content)
	}
}

@Composable
fun TeamSelector(onDismiss: () -> Unit, onConfirm: () -> Unit, onPlayerChange: (Boolean, PlayerMatch) -> Unit, players: List<PlayerMatch>){
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
					Row(verticalAlignment = Alignment.CenterVertically) {
						Checkbox(p.onMatch, { onPlayerChange(it, p) })
						Text(p.player.name)
					}
				}
			}
		}
	)
}