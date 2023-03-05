package fr.imacaron.robobrole.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.imacaron.robobrole.activity.MainActivity
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Info
import fr.imacaron.robobrole.types.MatchState
import fr.imacaron.robobrole.types.UIState
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun  HomeScreen(navController: NavController, db: AppDatabase, uiState: UIState, matchState: MatchState){
	val context = LocalContext.current as MainActivity
	var confirm: Boolean by remember { mutableStateOf(false) }
	var history: List<Info> by remember { mutableStateOf(listOf()) }
	var current: Long by remember { mutableStateOf(-1) }
	uiState.home = true
	uiState.export = false
	uiState.resetTitle()
	LaunchedEffect(uiState.alert){
		withContext(Dispatchers.IO){
			history = db.infoDao().getSaved()
			current = db.infoDao().getCurrent()?.uid ?: -1
		}
	}
	Column {
		OutlinedCard(Modifier.fillMaxWidth().padding(8.dp)) {
			Row(Modifier.padding(8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
				FilledTonalButton(
					{
						matchState.clean()
						navController.navigate("match/${current}")
					},
					enabled = current != -1L
				){
					Text("Continuer le match")
				}
				Button({
					GlobalScope.launch(Dispatchers.IO){
						if(confirm || db.infoDao().getCurrent() == null){
							db.matchDao().wipeTable()
							db.infoDao().deleteCurrent()
							matchState.clean()
							withContext(Dispatchers.Main){
								navController.navigate("new_match")
							}
						}else{
							withContext(Dispatchers.Main){
								Toast.makeText(context, "Un match est en cours. Rappuyer pour le supprimer et en commncer un nouveau", Toast.LENGTH_LONG).show()
								confirm = true
								launch {
									delay(5000)
									confirm = false
								}
							}
						}
					}
				}) {
					Text("Nouveau match")
				}
			}
		}
		OutlinedCard(Modifier.padding(8.dp).fillMaxWidth()) {
			Text("Historique :", Modifier.padding(8.dp), style = MaterialTheme.typography.titleLarge)
			LazyColumn {
				items(history.size){index ->
					if(index != 0){
						Divider(Modifier.fillMaxWidth().padding(16.dp, 0.dp), 1.dp, MaterialTheme.colorScheme.outline)
					}
					Row(Modifier.padding(8.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
						Column {
							Text("${history[index].local} - ${history[index].visitor} | ${history[index].level}${history[index].gender.value}", style = MaterialTheme.typography.titleMedium)
							Text("${history[index].date.dayOfMonth}/${history[index].date.monthValue}/${history[index].date.year}")
						}
						Button({
							matchState.clean()
							navController.navigate("match/${history[index].uid}")
						}){
							Text("Voir plus")
						}
					}
				}
			}
		}
	}
}