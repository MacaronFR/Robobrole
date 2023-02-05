package fr.imacaron.robobrole.types

import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.core.content.edit
import fr.imacaron.robobrole.db.AppDatabase

@Stable
class AppState(sharedPref: SharedPreferences, db: AppDatabase) {

	var left: Boolean by mutableStateOf(sharedPref.getBoolean("left", false))
		private set

	var done: Boolean by mutableStateOf(false)

	var infoId: Long by mutableStateOf(0)

	var gender: String by mutableStateOf("F")

	val local: Team by mutableStateOf(Team(db))

	val visitor: Team by mutableStateOf(Team(db))

	var level: String by mutableStateOf("")

	val sharedPref: SharedPreferences by mutableStateOf(sharedPref)
	var theme: Theme by mutableStateOf(Theme.values()[sharedPref.getInt("theme", Theme.Default.value)])
		private set
	var displayMenu: Boolean by mutableStateOf(false)

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

	fun toggleLeftHanded(){
		left = !left
		sharedPref.edit {
			putBoolean("left", left)
		}
	}

	fun toggleMenu(){
		displayMenu = !displayMenu
	}

	fun closeMenu(){
		displayMenu = false
	}
}