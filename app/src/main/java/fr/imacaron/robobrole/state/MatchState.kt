package fr.imacaron.robobrole.state

import androidx.compose.runtime.*
import fr.imacaron.robobrole.db.*
import fr.imacaron.robobrole.types.Gender
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class PlayerMatch(private val db: AppDatabase, player: Player, onMatch: Boolean){
	var onMatch: Boolean by mutableStateOf(onMatch)
		private set

	@OptIn(DelicateCoroutinesApi::class)
	infix fun onMatch(isPresent: Boolean){
		onMatch = isPresent
		GlobalScope.launch(Dispatchers.IO){
			db.matchPlayerDao().setPresent(isPresent, player.id)
		}
	}

	var player: Player by mutableStateOf(player)
}

@Stable
class MatchState {

	var date: LocalDate by mutableStateOf(LocalDate.now())

	var current: Long by mutableStateOf(-1)

	var gender: Gender by mutableStateOf(Gender.Women)

	var startAt: Long by mutableStateOf(0)

	var quart: Int by mutableStateOf(0)

	var level: String by mutableStateOf("")

	var myTeam: String by mutableStateOf("")

	var otherTeam: String by mutableStateOf("")

	val players: MutableList<PlayerMatch> = mutableStateListOf()

	var done: Boolean by mutableStateOf(false)

	val events: MutableList<Event> = mutableStateListOf()

	val myTeamSum = List(4) { Summary() }

	val otherTeamSum = List(4) { Summary() }

	fun getSummary(team: String, quart: Int): Summary = when(team) {
		myTeam -> myTeamSum[quart - 1]
		otherTeam -> otherTeamSum[quart - 1]
		else -> throw IllegalArgumentException()
	}

	fun clean(){
		quart = 0
		date = LocalDate.now()
		current = -1
		gender = Gender.Women
		startAt = 0
		level = "0"
		myTeam = ""
		otherTeam = ""
		players.clear()
		events.clear()
		myTeamSum.clean()
		otherTeamSum.clean()
	}
}