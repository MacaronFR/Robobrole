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
import fr.imacaron.robobrole.db.MatchEvent
import fr.imacaron.robobrole.types.AppState
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun  HomeScreen(navController: NavController, db: AppDatabase, state: AppState){
	val context = LocalContext.current as MainActivity
	var confirm: Boolean by remember { mutableStateOf(false) }
	var history: List<Info> by remember { mutableStateOf(listOf()) }
	state.home = true
	LaunchedEffect(state.alert, state.infoId){
		withContext(Dispatchers.IO){
			history = db.infoDao().getSaved()
		}
	}
	Column {
		Card(Modifier.fillMaxWidth().padding(8.dp)) {
		Row(Modifier.padding(8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
			Button(
				{
					GlobalScope.launch(Dispatchers.IO){
						db.infoDao().getCurrent()?.let { info ->
							reloadInfo(state, info)
							reloadSummary(state, db)
							reloadEvent(state, db)
							withContext(Dispatchers.Main){
								navController.navigate("match")
							}
						} ?: withContext(Dispatchers.Main){
							Toast.makeText(context, "Aucun match en cours", Toast.LENGTH_SHORT).show()
						}
					}
				},
				enabled = state.infoId != 0L && !state.done,
			){
				Text("Continuer le match")
			}
				Button({
					GlobalScope.launch(Dispatchers.IO){
						if(confirm || db.infoDao().getCurrent() == null){
							db.matchDao().wipeTable()
							db.summaryDao().wipeTable()
							db.infoDao().deleteCurrent()
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
		Card(Modifier.padding(8.dp).fillMaxWidth()) {
			Text("Historique :", Modifier.padding(8.dp), style = MaterialTheme.typography.titleLarge)
			LazyColumn {
				items(history.size){index ->
					if(index != 0){
						Divider(Modifier.fillMaxWidth().padding(16.dp, 0.dp), 1.dp, MaterialTheme.colorScheme.outline)
					}
					Row(Modifier.padding(8.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
						Text("${history[index].local} ${history[index].visitor} ${history[index].level}${history[index].gender}", style = MaterialTheme.typography.titleMedium)
						Button({
							GlobalScope.launch(Dispatchers.IO) {
								reloadInfo(state, history[index])
								val data = context.loadFile(history[index].uid)
								reloadFromFile(state, data)
								withContext(Dispatchers.Main){
									navController.navigate("match")
								}
							}
						}){
							Text("Voir plus")
						}
					}
				}
			}
		}
	}
}

suspend fun reloadFromFile(state: AppState, data: String){
	val lines = data.lineSequence().iterator()
	lines.next()
	while(lines.hasNext()){
		val s = lines.next()
		if(s.isEmpty()){
			continue
		}
		val e = MatchEvent(s)
		val team = if(state.local.name == e.team){
			state.local
		}else {
			state.visitor
		}
		team.scores[e.quart-1].apply {
			when(e.data){
				"1" -> one += 1
				"2" -> two += 1
				"3" -> three += 1
				"L" -> lucille += 1
			}
		}
	}
}

suspend fun reloadInfo(state: AppState, info: Info){
	withContext(Dispatchers.IO){
		state.local.name = info.local
		state.local.matchStart = info.matchStart
		state.gender = info.gender
		state.visitor.name = info.visitor
		state.visitor.matchStart = info.matchStart
		state.level = info.level
		state.infoId = info.uid
		state.done = info.done
	}
}

suspend fun reloadSummary(state: AppState, db: AppDatabase){
	withContext(Dispatchers.IO){
		db.summaryDao().getAll().forEach {
			val team = if(it.team == state.local.name){
				state.local
			}else {
				state.visitor
			}
			team.scores[it.quart-1][it.key] = it.value
		}
	}
}

suspend fun reloadEvent(state: AppState, db: AppDatabase){
	withContext(Dispatchers.IO){
		db.matchDao().getAll().forEach {
			val team = if(state.local.name == it.team){
				state.local
			}else {
				state.visitor
			}
			team.scores[it.quart-1].apply {
				when(it.data){
					"1" -> one.reloadId(it.uid)
					"2" -> two.reloadId(it.uid)
					"3" -> three.reloadId(it.uid)
					"L" -> lucille.reloadId(it.uid)
				}
			}
		}
	}
}