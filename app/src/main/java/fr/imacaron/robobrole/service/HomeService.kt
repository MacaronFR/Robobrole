package fr.imacaron.robobrole.service

import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Match
import fr.imacaron.robobrole.state.HomeState
import kotlinx.coroutines.*

class HomeService(private val db: AppDatabase) {

	private val state = HomeState()

	val history: List<Match>
		get() = state.history

	suspend fun loadHistory(){
		withContext(Dispatchers.IO){
			state.history.addAll(db.matchDao().getSaved())
		}
	}

	fun cleanHistory(){
		state.history.clear()
	}

	suspend fun currentMatch(): Long? = withContext(Dispatchers.IO){
		db.matchDao().getCurrent()?.uid
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun deleteHistory(id: Long){
		state.history.removeIf { it.uid == id }
		GlobalScope.launch(Dispatchers.IO){
			db.matchDao().delete(id)
		}
	}
}