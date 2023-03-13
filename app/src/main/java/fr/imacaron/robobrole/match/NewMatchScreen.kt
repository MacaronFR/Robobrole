package fr.imacaron.robobrole.match

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import fr.imacaron.robobrole.service.NewMatchService
import kotlinx.coroutines.*

val defaultModifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMatchBar(navController: NavController){
	TopAppBar(
		{ Text("Nouveau match") },
		navigationIcon = { IconButton({ navController.navigateUp() }){ Icon(Icons.Outlined.ArrowBack, "Back") } },
		actions = {
			IconButton({ navController.navigate("team") }){ Icon(Icons.Outlined.Groups, "Team") }
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun NewNewMatchScreen(service: NewMatchService, navController: NavController){
	LaunchedEffect(Unit){
		service.loadPlayers()
	}
	Scaffold(
		topBar = { NewMatchBar(navController) }
	) { p ->
		Column(Modifier.padding(p), horizontalAlignment = Alignment.CenterHorizontally) {
			MatchInfo(service)
			PlayerSelection(service)
			Button(
				{
					if(service.isValid) {
						GlobalScope.launch {
							val match = service.createMatch()
							service.createPlayer(match)
							withContext(Dispatchers.Main){
								navController.navigate("match/$match"){ popUpTo("home") }
							}
						}
					}
				}
			){
				Icon(Icons.Outlined.Add, "Create match")
				Text("Créer le match")
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchInfo(service: NewMatchService){
	val focusRequester = remember { FocusRequester() }
	LaunchedEffect(Unit){
		focusRequester.requestFocus()
	}
	Column(defaultModifier, horizontalAlignment = Alignment.CenterHorizontally) {
		OutlinedTextField(
			service.myTeam,
			{  },
			defaultModifier,
			label = { Text("Mon équipe") },
			readOnly = true,
			singleLine = true
		)
		OutlinedTextField(
			service.otherTeam,
			{ service.otherTeam = it },
			defaultModifier.focusRequester(focusRequester),
			label = { Text("Adversaire") },
			singleLine = true,
			isError = service.otherError
		)
		Row(defaultModifier, horizontalArrangement = Arrangement.SpaceAround) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				RadioButton(!service.women, { service.women = false })
				Text("Masculin")
			}
			Row(verticalAlignment = Alignment.CenterVertically) {
				RadioButton(service.women, { service.women = true })
				Text("Féminin")
			}
		}
		Box(defaultModifier){
			OutlinedTextField(service.level, {}, Modifier.fillMaxWidth(), label = { Text("Niveau") }, trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) })
			DropdownMenu(service.openLevel, { service.openLevel = false }, Modifier.fillMaxWidth(0.6f)){
				DropdownMenuItem({ Text("U9")}, { service.level = "U9"; service.openLevel = false })
				DropdownMenuItem({ Text("U11")}, { service.level = "U11"; service.openLevel = false })
				DropdownMenuItem({ Text("U13")}, { service.level = "U13"; service.openLevel = false })
				DropdownMenuItem({ Text("U15")}, { service.level = "U15"; service.openLevel = false })
				DropdownMenuItem({ Text("U17")}, { service.level = "U17"; service.openLevel = false })
				DropdownMenuItem({ Text("U18")}, { service.level = "U18"; service.openLevel = false })
				DropdownMenuItem({ Text("U20")}, { service.level = "U20"; service.openLevel = false })
				DropdownMenuItem({ Text("Senior")}, { service.level = "Senior"; service.openLevel = false })
			}
			Spacer(Modifier.matchParentSize().clickable { service.openLevel = true })
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSelection(service: NewMatchService){
	Column(Modifier.fillMaxWidth().padding(16.dp, 0.dp)) {
		Text("Équipe", Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp), style = MaterialTheme.typography.headlineSmall)
		FlowRow(Modifier.fillMaxWidth().padding(8.dp, 0.dp), mainAxisAlignment = FlowMainAxisAlignment.Start, mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
			service.players.forEach { (player, isPresent) ->
				FilterChip(isPresent, { service.players[player] = !isPresent }, { Text(player.name) } )
			}
		}
		if(service.teamError){
			Text("Sélectionner entre 5 et 10 joueuses", Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp), color = MaterialTheme.colorScheme.error)
		}
	}
}