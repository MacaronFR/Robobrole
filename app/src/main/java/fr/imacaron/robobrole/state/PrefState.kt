package fr.imacaron.robobrole.state

import android.content.SharedPreferences
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import fr.imacaron.robobrole.types.Theme

@Stable
class PrefState(private val sharedPref: SharedPreferences) {

	var theme: Theme by mutableStateOf(Theme.values()[sharedPref.getInt("theme", Theme.Default.value)])
		private set

	private var teamName: String by mutableStateOf(sharedPref.getString("team", "")!!)

	var team: String
		get() = teamName
		set(value) {
			teamName = value
			sharedPref.edit {
				putString("team", value)
			}
		}

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
}