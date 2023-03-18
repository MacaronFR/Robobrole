package fr.imacaron.robobrole.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.db.Player
import fr.imacaron.robobrole.service.TeamService
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun TeamScreenDialog(add: Boolean, closeAdd: () -> Unit, service: TeamService) {
	var name: String by remember { mutableStateOf("") }
	if(add){
		AlertDialog(
			onDismissRequest = {
				closeAdd()
			},
			confirmButton = {
				TextButton({
					GlobalScope.launch{
						if(name != ""){
							service.createPlayer(Player(name))
							withContext(Dispatchers.Main){
								closeAdd()
								name = ""
							}
						}
					}
				} ){ Text("Ajouter") }
			},
			title = { Text("Ajouter un joueur") },
			text = {
				Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
					OutlinedTextField(name, { name = it }, label = { Text("Nom") }, isError = name == "")
				}
			}
		)
	}
}