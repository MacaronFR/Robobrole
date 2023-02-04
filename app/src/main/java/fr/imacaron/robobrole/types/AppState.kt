package fr.imacaron.robobrole.types

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

class AppState(val sharedPref: SharedPreferences) {
	var theme: Theme by mutableStateOf(Theme.values()[sharedPref.getInt("theme", Theme.Default.value)])
		private set
	var displayMenu: Boolean by mutableStateOf(false)
	var quart: Int by mutableStateOf(0)
	val points: List<Points> = listOf(Points(), Points(), Points(), Points())

	fun setLightTheme(setPref: Boolean = true){
		theme = Theme.Light
		if(setPref){
			sharedPref.edit {
				putInt("theme", Theme.Light.value)
			}
		}
	}

	fun setDarkTheme(setPref: Boolean = true){
		theme = Theme.Dark
		if(setPref) {
			sharedPref.edit {
				putInt("theme", Theme.Dark.value)
			}
		}
	}

	fun setDefaultTheme(setPref: Boolean = true){
		theme = Theme.Default
		if(setPref){
			sharedPref.edit {
				putInt("theme", Theme.Default.value)
			}
		}
	}

	fun toggleMenu(){
		displayMenu = !displayMenu
	}

	fun closeMenu(){
		displayMenu = false
	}
}