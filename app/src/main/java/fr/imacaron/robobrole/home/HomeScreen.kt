@file:Suppress("FunctionName")

package fr.imacaron.robobrole.home

import android.view.MotionEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.db.Match
import fr.imacaron.robobrole.match.defaultModifier
import fr.imacaron.robobrole.service.HomeService
import fr.imacaron.robobrole.service.NavigationService

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

@Composable
fun HomeNav(navigator: NavigationService){
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
				navigator.navigateNewMatch()
			}
		)
	}
}

@Composable
fun HomeScreen(navigator: NavigationService, service: HomeService){
	BackHandler { navigator.navigateUp() }
	Scaffold(
		topBar = { HomeBar(navigator) },
		bottomBar = { HomeNav(navigator) }
	) {
		Column(Modifier.padding(it)) {
			MatchList(navigator, service, service.currents, "En cours :", "Aucun match en cours")
			MatchList(navigator, service, service.history, "Historique :", "Aucun historique de match")
		}
	}
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MatchList(navigator: NavigationService, service: HomeService, match: List<Match>, title: String, noContent: String){
	Card(Modifier.padding(8.dp).fillMaxWidth()) {
		Text(title, Modifier.padding(8.dp), style = MaterialTheme.typography.titleLarge)
		LazyColumn {
			if(match.size == 0) {
				item {
					Text(noContent, Modifier.padding(8.dp))
				}
			}
			items(match, { it.uid }){ match ->
				val dismissState = rememberDismissState(confirmValueChange = {
					if( it == DismissValue.DismissedToStart){
						service.deleteMatch(match.uid)
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
							if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f, label = ""
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