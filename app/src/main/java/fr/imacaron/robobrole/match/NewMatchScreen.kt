package fr.imacaron.robobrole.match

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Match
import fr.imacaron.robobrole.db.MatchPlayer
import fr.imacaron.robobrole.db.Player
import fr.imacaron.robobrole.types.Gender
import fr.imacaron.robobrole.types.PrefState
import fr.imacaron.robobrole.types.UIState
import kotlinx.coroutines.*

val defaultModifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)

class NewMatchScreenState{
	var openLevel: Boolean by mutableStateOf(false)
	var level: String by mutableStateOf("Senior")
	var levelError: Boolean by mutableStateOf(false)
	var otherTeam: String by mutableStateOf("")
	var otherTeamError: Boolean by mutableStateOf(false)
	var women: Boolean by mutableStateOf(true)
	var teamError: Boolean by mutableStateOf(false)
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun NewMatchScreen(navController: NavController, db: AppDatabase, uiState: UIState, prefState: PrefState){
	val newMatchScreenState: NewMatchScreenState by remember { mutableStateOf(NewMatchScreenState()) }
	val focusRequester = remember { FocusRequester() }
	val players: MutableList<Player> = remember { mutableStateListOf() }
	val playerSelected: MutableList<Boolean> = remember { mutableStateListOf() }
	uiState.home = false
	uiState.title = "Nouveau match"
	LaunchedEffect(db){
		withContext(Dispatchers.IO){
			players.addAll(db.playerDao().getAll())
			repeat(players.size){
				playerSelected.add(false)
			}
		}
	}
	LaunchedEffect(Unit){
		focusRequester.requestFocus()
	}
	Column {
		Column(defaultModifier, horizontalAlignment = Alignment.CenterHorizontally) {
			OutlinedTextField(
				prefState.team,
				{  },
				defaultModifier,
				label = { Text("Mon équipe") },
				readOnly = true,
				singleLine = true
			)
			OutlinedTextField(
				newMatchScreenState.otherTeam,
				{ newMatchScreenState.otherTeam = it },
				defaultModifier.focusRequester(focusRequester),
				label = { Text("Adversaire") },
				singleLine = true
			)
			Row(defaultModifier, horizontalArrangement = Arrangement.SpaceAround) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					RadioButton(!newMatchScreenState.women, { newMatchScreenState.women = false })
					Text("Masculin")
				}
				Row(verticalAlignment = Alignment.CenterVertically) {
					RadioButton(newMatchScreenState.women, { newMatchScreenState.women = true })
					Text("Féminin")
				}
			}
			Box(defaultModifier){
				OutlinedTextField(newMatchScreenState.level, {}, Modifier.fillMaxWidth(), label = { Text("Niveau") }, trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }, isError = newMatchScreenState.levelError)
				DropdownMenu(newMatchScreenState.openLevel, { newMatchScreenState.openLevel = false }, Modifier.fillMaxWidth(0.6f)){
					DropdownMenuItem({ Text("U9")}, { newMatchScreenState.level = "U9"; newMatchScreenState.openLevel = false })
					DropdownMenuItem({ Text("U11")}, { newMatchScreenState.level = "U11"; newMatchScreenState.openLevel = false })
					DropdownMenuItem({ Text("U13")}, { newMatchScreenState.level = "U13"; newMatchScreenState.openLevel = false })
					DropdownMenuItem({ Text("U15")}, { newMatchScreenState.level = "U15"; newMatchScreenState.openLevel = false })
					DropdownMenuItem({ Text("U17")}, { newMatchScreenState.level = "U17"; newMatchScreenState.openLevel = false })
					DropdownMenuItem({ Text("U18")}, { newMatchScreenState.level = "U18"; newMatchScreenState.openLevel = false })
					DropdownMenuItem({ Text("U20")}, { newMatchScreenState.level = "U20"; newMatchScreenState.openLevel = false })
					DropdownMenuItem({ Text("Senior")}, { newMatchScreenState.level = "Senior"; newMatchScreenState.openLevel = false })
				}
				Spacer(Modifier.matchParentSize().clickable { newMatchScreenState.openLevel = true })
			}
		}
		Column(Modifier.fillMaxWidth().padding(16.dp, 0.dp)) {
			Text("Équipe", Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp), style = MaterialTheme.typography.headlineSmall)
			FlowRow(Modifier.fillMaxWidth().padding(8.dp, 0.dp), mainAxisAlignment = FlowMainAxisAlignment.Start, mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
				players.forEachIndexed { index, player ->
					FilterChip(playerSelected[index], { playerSelected[index] = !playerSelected[index] }, { Text(player.name) } )
				}
			}
			if(newMatchScreenState.teamError){
				Text("Sélectionner entre 5 et 10 joueuses", Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp), color = MaterialTheme.colorScheme.error)
			}
		}
		Button(
			{
				var ok = true
				if(newMatchScreenState.otherTeam == ""){
					ok = false
					newMatchScreenState.otherTeamError = true
				}else{
					newMatchScreenState.otherTeamError = false
				}
				if(newMatchScreenState.level == ""){
					ok = false
					newMatchScreenState.levelError = true
				}else{
					newMatchScreenState.levelError = false
				}
				if(playerSelected.filter { it }.size < 5 || playerSelected.filter { it }.size > 10 ){
					ok = false
					newMatchScreenState.teamError = true
				}else {
					newMatchScreenState.teamError = false
				}
				if(ok){
					val info = Match(prefState.team, newMatchScreenState.otherTeam, newMatchScreenState.level, if(newMatchScreenState.women) Gender.Women else Gender.Men)
					val matchPlayers = players.filterIndexed { index, _ -> playerSelected[index] }.map { MatchPlayer(it) }
					GlobalScope.launch{
						val id = db.matchDao().insertInfo(info)
						db.matchPlayerDao().insertAll(matchPlayers)
						withContext(Dispatchers.Main){
							navController.navigate("match/$id"){ popUpTo("home") }
						}
					}
				}
			},
			defaultModifier
		){
			Text("Créer le match")
		}
	}
}