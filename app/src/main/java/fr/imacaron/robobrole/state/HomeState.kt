package fr.imacaron.robobrole.state

import androidx.compose.runtime.mutableStateListOf
import fr.imacaron.robobrole.db.Match

class HomeState {

	val history: MutableList<Match> = mutableStateListOf()

}