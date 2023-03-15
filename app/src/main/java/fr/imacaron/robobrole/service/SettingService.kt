package fr.imacaron.robobrole.service

import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.state.PrefState
import fr.imacaron.robobrole.state.SettingUIState
import fr.imacaron.robobrole.types.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingService(private val db: AppDatabase, private val prefState: PrefState) {

	private val ui = SettingUIState()

	val themeDialog: Boolean
		get() = ui.themeDialog

	val historyDialog: Boolean
		get() = ui.deleteHistoryDialog

	val deleteDialog: Boolean
		get() = ui.deleteDataDialog

	var theme: Theme
		get() = prefState.theme
		set(value) {
			when(value) {
				Theme.Light -> prefState.setLightTheme()
				Theme.Dark -> prefState.setDarkTheme()
				Theme.Default -> prefState.setDefaultTheme()
			}
		}

	suspend fun deleteHistory(){
		withContext(Dispatchers.IO){
			db.matchDao().deleteAll()
			db.matchPlayerDao().deleteAll()
			db.eventDAO().deleteAll()
		}
	}

	suspend fun deleteTeam(){
		withContext(Dispatchers.IO){
			db.playerDao().deleteAll()
		}
	}

	suspend fun deleteAllData(){
		deleteTeam()
		deleteHistory()
		prefState.reset()
	}

	fun toggleThemeDialog(){
		ui.themeDialog = !ui.themeDialog
	}

	fun toggleHistoryDialog(){
		ui.deleteHistoryDialog = !ui.deleteHistoryDialog
	}

	fun toggleDeleteDialog(){
		ui.deleteDataDialog = !ui.deleteDataDialog
	}
}