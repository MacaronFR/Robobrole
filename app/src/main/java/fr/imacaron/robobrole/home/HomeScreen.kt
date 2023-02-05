package fr.imacaron.robobrole.home

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Info
import fr.imacaron.robobrole.types.AppState
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun  HomeScreen(navController: NavController, db: AppDatabase, state: AppState){
	val context = LocalContext.current
	var confirm: Boolean by remember { mutableStateOf(false) }
	Column {
		Text("Roborole")
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
		Button({
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
		}){
			Text("Load current match")
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
			team.scores[it.quart].apply {
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