package fr.imacaron.robobrole.service

import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Match
import fr.imacaron.robobrole.state.HomeState
import kotlinx.coroutines.*

class HomeService(private val db: AppDatabase) {

	private val state = HomeState()

	val history: List<Match>
		get() = state.history

	val currents: List<Match>
		get() = state.currents

	suspend fun loadHistory(){
		withContext(Dispatchers.IO){
			state.history.addAll(db.matchDao().getSaved())
		}
	}

	fun cleanHistory(){
		state.history.clear()
	}

	fun cleanCurrents(){
		state.currents.clear()
	}

	suspend fun loadCurrent(){
		withContext(Dispatchers.IO){
			state.currents.addAll(db.matchDao().getCurrent())
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun deleteMatch(id: Long){
		state.history.removeIf { it.uid == id }
		state.currents.removeIf { it.uid == id }
		GlobalScope.launch(Dispatchers.IO){
			db.matchDao().delete(id)
		}
	}
}