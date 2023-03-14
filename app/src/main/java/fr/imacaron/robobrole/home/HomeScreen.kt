@file:Suppress("FunctionName")

package fr.imacaron.robobrole.home

import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.DismissDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.service.HomeService
import fr.imacaron.robobrole.service.NavigationService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBar(navigator: NavigationService){
	TopAppBar(
		{ Text("Robobrole") },
		actions = {
			IconButton({ navigator.navigateSettings() }){
				Icon(Icons.Outlined.Settings, "Settings")
			}
		}
	)
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun HomeNav(navigator: NavigationService, homeService: HomeService){
	var current: Long? by remember { mutableStateOf(null) }
	var confirm: Boolean by remember { mutableStateOf(false) }
	val context = LocalContext.current
	LaunchedEffect(homeService){
		current = homeService.currentMatch()
	}
	NavigationBar {
		NavigationBarItem(
			icon = { Icon(Icons.Outlined.Groups, null) },
			label = { Text("Équipe") },
			selected = false,
			onClick = {
				navigator.navigateTeam()
			}
		)
		NavigationBarItem(
			icon = { Icon(Icons.Outlined.Add, null) },
			label = { Text("Nouveau") },
			selected = false,
			onClick = {
				if(confirm || current == null){
					navigator.navigateNewMatch()
				}else{
					confirm = true
					GlobalScope.launch {
						delay(5000)
						confirm = false
					}
					Toast.makeText(context, "Un match est en cours. Appuyer à nouveau pour l'écraser", Toast.LENGTH_LONG).show()
				}
			}
		)
		NavigationBarItem(
			icon = { Icon(Icons.Outlined.PlayArrow, null) },
			label = { Text("Continuer") },
			selected = false,
			onClick = {
				navigator.navigateMatch(current ?: -1)
			},
			enabled = current != null
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigator: NavigationService, service: HomeService){
	Scaffold(
		topBar = { HomeBar(navigator) },
		bottomBar = { HomeNav(navigator, service) }
	) {
		Column(Modifier.padding(it)) {
			History(navigator, service)
		}
	}
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun History(navigator: NavigationService, service: HomeService){
	Card(Modifier.padding(8.dp).fillMaxWidth()) {
		Text("Historique :", Modifier.padding(8.dp), style = MaterialTheme.typography.titleLarge)
		LazyColumn {
			items(service.history){ match ->
				val dismissState = rememberDismissState(confirmStateChange = {
					if( it == DismissValue.DismissedToStart){
						service.deleteHistory(match.uid)
					}
					it == DismissValue.DismissedToStart
				})
				SwipeToDismiss(
					state = dismissState,
					directions = setOf(DismissDirection.EndToStart),
					background = {
						val direct = dismissState.dismissDirection ?: return@SwipeToDismiss
						val alignement = when(direct){
							DismissDirection.StartToEnd -> Alignment.CenterStart
							DismissDirection.EndToStart -> Alignment.CenterEnd
						}
						val icon = when (direct) {
							DismissDirection.StartToEnd -> Icons.Default.Done
							DismissDirection.EndToStart -> Icons.Default.Delete
						}
						val scale by animateFloatAsState(
							if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
						)
						Box(
							Modifier.fillMaxSize().background(MaterialTheme.colorScheme.errorContainer).padding(horizontal = 20.dp),
							contentAlignment = alignement
						) {
							Icon(icon, contentDescription = null, modifier = Modifier.scale(scale), tint = MaterialTheme.colorScheme.onErrorContainer)
						}
					},
					dismissContent = {
						Row(
							Modifier
								.background(MaterialTheme.colorScheme.surfaceVariant)
								.padding(8.dp)
								.fillMaxWidth()
								.pointerInteropFilter {
									return@pointerInteropFilter when (it.action) {
										MotionEvent.ACTION_DOWN -> {
											true
										}

										MotionEvent.ACTION_UP -> {
											navigator.navigateMatch(match.uid)
											true
										}

										else -> false
									}
								},
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.SpaceBetween
						) {
							Column {
								Text(
									"${match.myTeam} - ${match.otherTeam} | ${match.level}${match.gender.value}",
									style = MaterialTheme.typography.titleMedium
								)
								Text("${match.date.dayOfMonth}/${match.date.monthValue}/${match.date.year}")
							}
							IconButton({}, Modifier.size(24.dp)) {
								Icon(
									Icons.Outlined.ArrowForward,
									null,
									tint = MaterialTheme.colorScheme.onSurfaceVariant
								)
							}
						}
					}
				)
			}
		}
	}
}