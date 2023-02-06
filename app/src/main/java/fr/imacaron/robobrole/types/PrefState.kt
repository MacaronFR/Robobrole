package fr.imacaron.robobrole.types

import android.content.SharedPreferences
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

@Stable
class PrefState(val sharedPref: SharedPreferences) {

	var theme: Theme by mutableStateOf(Theme.values()[sharedPref.getInt("theme", Theme.Default.value)])
		private set

	var left: Boolean by mutableStateOf(sharedPref.getBoolean("left", false))
		private set

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
}