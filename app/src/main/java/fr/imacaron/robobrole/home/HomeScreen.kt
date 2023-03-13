package fr.imacaron.robobrole.home

import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import fr.imacaron.robobrole.state.UIState
import kotlinx.coroutines.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(navController: NavController, db: AppDatabase, uiState: UIState){
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
				itemsIndexed(history){index, match ->
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
										navController.navigate("match/${match.uid}")
										true
									}
									else -> false
								}
												  },
						verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
						Column {
							Text("${match.myTeam} - ${match.otherTeam} | ${match.level}${match.gender.value}", style = MaterialTheme.typography.titleMedium)
							Text("${match.date.dayOfMonth}/${match.date.monthValue}/${match.date.year}")
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