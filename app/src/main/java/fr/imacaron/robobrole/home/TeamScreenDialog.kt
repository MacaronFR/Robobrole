package fr.imacaron.robobrole.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.db.Player
import fr.imacaron.robobrole.service.TeamService
import kotlinx.coroutines.*
import java.lang.NumberFormatException

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun TeamScreenDialog(add: Boolean, closeAdd: () -> Unit, service: TeamService) {
	var name: String by remember { mutableStateOf("") }
	var number: Int? by remember { mutableStateOf(null) }
	if(add){
		AlertDialog(
			onDismissRequest = {
				closeAdd()
			},
			confirmButton = {
				TextButton({
					GlobalScope.launch{
						if(number != null && name != ""){
							service.createPlayer(Player(name, number!!))
							withContext(Dispatchers.Main){
								closeAdd()
								name = ""
								number = null
							}
						}
					}
				} ){ Text("Ajouter") }
			},
			title = { Text("Ajouter un joueur") },
			text = {
				Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
					OutlinedTextField(name, { name = it }, label = { Text("Nom") }, isError = name == "")
					OutlinedTextField(number?.toString() ?: "", { number = try { it.toInt() } catch (_: NumberFormatException) { null } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), label = { Text("Numéro") }, isError = number == null)
				}
			}
		)
	}
}