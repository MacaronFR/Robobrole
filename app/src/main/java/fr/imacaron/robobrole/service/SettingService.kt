package fr.imacaron.robobrole.service

import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.state.PrefState
import fr.imacaron.robobrole.state.SettingUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingService(private val db: AppDatabase, private val prefState: PrefState) {

	private val ui = SettingUIState()

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
	}
}