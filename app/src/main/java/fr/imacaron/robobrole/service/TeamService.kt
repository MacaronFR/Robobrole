package fr.imacaron.robobrole.service

import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Player
import fr.imacaron.robobrole.state.PrefState
import fr.imacaron.robobrole.state.TeamState
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
class TeamService(private val db: AppDatabase, private val prefState: PrefState) {

	init {
		GlobalScope.launch(Dispatchers.IO) {
			state.players.addAll(db.playerDao().getAll())
		}
	}

	private val state = TeamState(prefState.team)

	var team: String
		get() = state.team
		set(value) {
			state.team = value
			prefState.team = value
		}

	val players: List<Player>
		get() = state.players

	suspend fun updatePlayer(player: Player){
		withContext(Dispatchers.IO){
			db.playerDao().updatePlayer(player)
		}
		val index = state.players.indexOfFirst { it.id == player.id }
		state.players[index] = player
	}

	suspend fun createPlayer(player: Player){
		val id = withContext(Dispatchers.IO){
			db.playerDao().insertPlayer(player)
		}
		player.id = id
		state.players.add(player)
	}

	suspend fun deletePlayer(player: Player){
		state.players.remove(player)
		withContext(Dispatchers.IO){
			db.playerDao().delete(player)
		}
	}
}