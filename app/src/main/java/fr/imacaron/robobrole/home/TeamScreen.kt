@file:Suppress("FunctionName")

package fr.imacaron.robobrole.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.db.Player
import fr.imacaron.robobrole.service.NavigationService
import fr.imacaron.robobrole.service.TeamService
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun TeamScreen(service: TeamService, navigator: NavigationService){
	var add: Boolean by remember { mutableStateOf(false) }
	var edit: Player? by remember { mutableStateOf(null) }
	var name: String by remember { mutableStateOf("") }
	BackHandler(true) { navigator.navigateUp() }
	Scaffold(
		topBar = {
			TopAppBar(
				{ Text("Équipe" ) },
				navigationIcon = { IconButton({ navigator.navigateUp()} ){ Icon(Icons.Outlined.ArrowBack, "Back")} }
			)
		}
	){ p ->
		Box(Modifier.padding(p)){
			Card(Modifier.padding(8.dp, 8.dp)) {
				OutlinedTextField(
					service.team,
					{ service.team = it },
					Modifier.fillMaxWidth().padding(16.dp, 0.dp),
					label = { Text("Nom de l'équipe") },
					singleLine = true
				)
				Spacer(Modifier.height(16.dp))
				LazyColumn(Modifier.fillMaxWidth().padding(vertical = 0.dp), horizontalAlignment = Alignment.CenterHorizontally) {
					items(service.players, { player: Player -> player.id }) { player ->
						val dismissState = rememberDismissState(confirmValueChange = {
							if( it == DismissValue.DismissedToStart){
								GlobalScope.launch{
									service.deletePlayer(player)
								}
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
									Modifier.fillMaxWidth().height(72.dp).background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp, 8.dp, 24.dp, 8.dp),
									horizontalArrangement = Arrangement.spacedBy(16.dp),
									verticalAlignment = Alignment.CenterVertically
								) {
									Column{
										Text(player.name, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyLarge)
									}
									Spacer(Modifier.weight(1f))
									IconButton({
										edit = player
										name = player.name
									}, Modifier.size(24.dp)) {
										Icon(Icons.Outlined.Edit, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
									}
								}
							}
						)
					}
					item {
						Button(
							{
								add = true
							},
							Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
						) {
							Icon(Icons.Outlined.Add, null)
							Text("Ajouter un joueur")
						}
					}
				}
			}
		}
	}
	TeamScreenDialog(add, { add = false}, service)
	if(edit != null){
		AlertDialog(
			{
				edit = null
				name = ""
			},
			{
				TextButton({
					GlobalScope.launch(Dispatchers.IO){
						if(edit != null && name != ""){
							service.updatePlayer(Player(edit!!.id, name))
							edit = null
							name = ""
						}
					}
				}){
					Text("Sauvegarder")
				}
			},
			title = { Text("Modifier") },
			text = {
				Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
					OutlinedTextField(name, { name = it }, label = { Text("Nom") }, isError = name == "")
				}
			}
		)
	}
}