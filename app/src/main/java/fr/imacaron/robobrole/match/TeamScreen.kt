package fr.imacaron.robobrole.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Player
import fr.imacaron.robobrole.types.UIState
import kotlinx.coroutines.*
import java.lang.NumberFormatException

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun TeamScreen(db: AppDatabase, uiState: UIState){
	var players: List<Player> by remember { mutableStateOf(listOf()) }
	var add: Boolean by remember { mutableStateOf(false) }
	var edit: Player? by remember { mutableStateOf(null) }
	var delete: Player? by remember { mutableStateOf(null) }
	var name: String by remember { mutableStateOf("") }
	var number: Int? by remember { mutableStateOf(null) }
	uiState.home = false
	LaunchedEffect(add, edit, delete, uiState.alert){
		withContext(Dispatchers.IO){
			players = db.playerDao().getAll()
		}
	}
	println(players.size)
	LazyColumn(Modifier.padding(16.dp, 0.dp), horizontalAlignment = Alignment.CenterHorizontally) {
		items(players) { player ->
			Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
				Text(player.name)
				Text(player.number.toString())
				Spacer(Modifier.weight(1f))
				IconButton({
					edit = player
					name = player.name
					number = player.number
				}){
					Icon(Icons.Outlined.Edit, null)
				}
				IconButton({ delete = player }){
					Icon(Icons.Outlined.Delete, null)
				}
			}
		}
		item {
			Button({
				add = true
			}){
				Icon(Icons.Outlined.Add, null)
				Text("Ajouter un joueur")
			}
		}
	}
	if(add){
		AlertDialog(
			onDismissRequest = {
				add = false
			},
			confirmButton = {
				TextButton({
					GlobalScope.launch(Dispatchers.IO){
						if(number != null && name != ""){
							println(db.playerDao().insertPlayer(Player(name, number!!)))
							withContext(Dispatchers.Main){
								add = false
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
	if(edit != null){
		AlertDialog(
			{ edit = null },
			{
				TextButton({
					GlobalScope.launch(Dispatchers.IO){
						if(number != null && edit != null && name != ""){
							db.playerDao().updatePlayer(Player(edit!!.id, name, number!!))
							edit = null
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
					OutlinedTextField(number?.toString() ?: "", { number = try { it.toInt() } catch (_: NumberFormatException) { null } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), label = { Text("Numéro") }, isError = number == null)
				}
			}
		)
	}
	if(delete != null){
		AlertDialog(
			{ delete = null },
			{
				TextButton({
					GlobalScope.launch(Dispatchers.IO) {
						db.playerDao().delete(delete!!)
						delete = null
					}
				}){ Text("Supprimer") }
			},
			title = { Text("Supprimer le joueur") },
			text = { Text("Voulez supprimer la joueuse") }
		)
	}
}