package fr.imacaron.robobrole.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.imacaron.robobrole.db.Player

class TeamState(team: String) {

	var team: String by mutableStateOf(team)

	val players: MutableList<Player> = mutableStateListOf()
}