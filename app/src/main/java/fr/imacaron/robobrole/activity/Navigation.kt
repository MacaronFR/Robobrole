package fr.imacaron.robobrole.activity

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.types.MatchState
import fr.imacaron.robobrole.types.UIState
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun Navigation(navController: NavController, db: AppDatabase, uiState: UIState, matchState: MatchState){
	var current: Long by remember { mutableStateOf(-1) }
	var confirm: Boolean by remember { mutableStateOf(false) }
	val context = LocalContext.current as MainActivity
	LaunchedEffect(uiState.alert, uiState.home){
		withContext(Dispatchers.IO){
			current = db.matchDao().getCurrent()?.uid ?: -1
		}
	}
	if(uiState.home){
		NavigationBar{
			NavigationBarItem(
				icon = { Icon(Icons.Outlined.Groups, null) },
				label = { Text("Équipe") },
				selected = false,
				onClick = {
					navController.navigate("team")
				}
			)
			NavigationBarItem(
				icon = { Icon(Icons.Outlined.Add, null) },
				label = { Text("Nouveau") },
				selected = false,
				onClick = {
					GlobalScope.launch(Dispatchers.IO){
						if(confirm || db.matchDao().getCurrent() == null){
							db.matchDao().getCurrent()?.let {
								db.eventDAO().deleteMatch(it.uid)
							}
							db.matchDao().deleteCurrent()
							db.matchPlayerDao().deleteAll()
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
				}
			)
			NavigationBarItem(
				icon = { Icon(Icons.Outlined.PlayArrow, null) },
				label = { Text("Continuer") },
				selected = false,
				onClick = {
					matchState.clean()
					navController.navigate("match/$current")
						  },
				enabled = current != -1L
			)
		}
	}
}