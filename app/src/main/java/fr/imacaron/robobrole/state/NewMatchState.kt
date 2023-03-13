package fr.imacaron.robobrole.state

import androidx.compose.runtime.*
import fr.imacaron.robobrole.db.Player

@Stable
class NewMatchState {

	var otherTeam: String by mutableStateOf("")

	var level: String by mutableStateOf("Senior")

	var women: Boolean by mutableStateOf(true)

	val players: MutableMap<Player, Boolean> = mutableStateMapOf()

}

@Stable
class NewMatchUIState {
	var otherError: Boolean by mutableStateOf(false)

	var teamError: Boolean by mutableStateOf(false)

	var levelOpen: Boolean by mutableStateOf(false)
}