package fr.imacaron.robobrole.home

import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Match
import fr.imacaron.robobrole.types.MatchState
import fr.imacaron.robobrole.types.UIState
import kotlinx.coroutines.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun  HomeScreen(navController: NavController, db: AppDatabase, uiState: UIState, matchState: MatchState){
	var history: List<Match> by remember { mutableStateOf(listOf()) }
	uiState.home = true
	uiState.export = false
	uiState.resetTitle()
	LaunchedEffect(uiState.alert){
		withContext(Dispatchers.IO){
			history = db.matchDao().getSaved()
		}
	}
	Column {
		Card(Modifier.padding(8.dp).fillMaxWidth()) {
			Text("Historique :", Modifier.padding(8.dp), style = MaterialTheme.typography.titleLarge)
			LazyColumn {
				items(history.size){index ->
					if(index != 0){
						Divider(Modifier.fillMaxWidth().padding(16.dp, 0.dp), 1.dp, MaterialTheme.colorScheme.outline)
					}
					Row(
						Modifier
							.padding(8.dp)
							.fillMaxWidth()
							.pointerInteropFilter {
								return@pointerInteropFilter when(it.action){
									MotionEvent.ACTION_DOWN -> {
										true
									}
									MotionEvent.ACTION_UP -> {
										matchState.clean()
										navController.navigate("match/${history[index].uid}")
										true
									}
									else -> false
								}
												  },
						verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
						Column {
							Text("${history[index].myTeam} - ${history[index].otherTeam} | ${history[index].level}${history[index].gender.value}", style = MaterialTheme.typography.titleMedium)
							Text("${history[index].date.dayOfMonth}/${history[index].date.monthValue}/${history[index].date.year}")
						}
						IconButton({}, Modifier.size(24.dp)) {
							Icon(Icons.Outlined.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
						}
					}
				}
			}
		}
	}
}