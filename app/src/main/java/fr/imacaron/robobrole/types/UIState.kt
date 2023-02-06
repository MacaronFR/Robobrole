package fr.imacaron.robobrole.types

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class UIState {
	var alert: Boolean by mutableStateOf(false)

	var home: Boolean by mutableStateOf(false)

	var export: Boolean by mutableStateOf(false)

	var displayMenu: Boolean by mutableStateOf(false)
		private set

	fun toggleMenu(){
		displayMenu = !displayMenu
	}

	fun closeMenu(){
		displayMenu = false
	}
}