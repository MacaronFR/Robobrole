package fr.imacaron.robobrole.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Match
import fr.imacaron.robobrole.db.MatchPlayer
import fr.imacaron.robobrole.db.Player
import fr.imacaron.robobrole.state.NewMatchState
import fr.imacaron.robobrole.state.NewMatchUIState
import fr.imacaron.robobrole.types.Gender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewMatchService(private val db: AppDatabase, val myTeam: String) {

	private val state by mutableStateOf(NewMatchState())

	val ui = NewMatchUIState()

	val players: MutableMap<Player, Boolean>
		get() = state.players

	var otherTeam: String
		get() = state.otherTeam
		set(value) { state.otherTeam = value }

	var level: String
		get() = state.level
		set(value) { state.level = value }

	var women: Boolean
		get() = state.women
		set(value) { state.women = value }

	var openLevel: Boolean
		get() = ui.levelOpen
		set(value) { ui.levelOpen = value }

	var otherError: Boolean
		get() = ui.otherError
		set(value) { ui.otherError= value }

	var teamError: Boolean
		get() = ui.teamError
		set(value) { ui.teamError= value }

	val isValid: Boolean
		get(){
			var ok = true
			if(state.otherTeam == ""){
				ok = false
				ui.otherError = true
			}else {
				ui.otherError = false
			}
			state.players.filter { it.value }.size.let {
				if (it < 5 || it > 10) {
					ok = false
					ui.teamError = true
				}else{
					ui.teamError = false
				}
			}
			return ok
		}

	suspend fun createMatch(): Long = withContext(Dispatchers.IO){
		db.matchDao().insertInfo(Match(myTeam, state.otherTeam, state.level, if(state.women) Gender.Women else Gender.Men))
	}

	suspend fun createPlayer(match: Long){
		withContext(Dispatchers.IO){
			val players = state.players.filter { it.value }.map { MatchPlayer(it.key, match) }
			db.matchPlayerDao().insertAll(players)
		}
	}

	suspend fun loadPlayers(){
		withContext(Dispatchers.IO){
			println("ICI")
			db.playerDao().getAll().forEach {
				state.players[it] = false
			}
		}
	}
}