package fr.imacaron.robobrole.types

import androidx.compose.runtime.*
import fr.imacaron.robobrole.db.*
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
			db.matchPlayerDao().setPresent(player.id, isPresent)
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

	var level: String by mutableStateOf("")

	var myTeam: String by mutableStateOf("")

	var otherTeam: String by mutableStateOf("")

	val players: MutableList<PlayerMatch> = mutableStateListOf()

	var done: Boolean by mutableStateOf(false)

	val events: MutableList<Event> = mutableStateListOf()

	val myTeamSum = List(4) { Summary() }

	val otherTeamSum = List(4) { Summary() }
	
	fun loadFromMatch(match: Match){
		current = match.uid
		gender = match.gender
		startAt = match.matchStart
		level = match.level
		myTeam = match.myTeam
		otherTeam = match.otherTeam
		done = match.done
		date = match.date
	}

	fun loadEvents(events: List<Event>){
		events.sortedBy { it.time }.forEach {e ->
			val summary = when(e.team){
				myTeam -> myTeamSum
				otherTeam -> otherTeamSum
				else -> { throw IllegalArgumentException() }
			}
			when(e.type) {
				Type.Point -> {
					when(e.data){
						"1" -> { summary[e.quart - 1].one++ }
						"2" -> { summary[e.quart - 1].two++ }
						"3" -> { summary[e.quart - 1].three++ }
						else -> throw IllegalArgumentException()
					}
				}
				Type.Fault -> {

				}
				Type.Change -> {

				}
				else -> TODO("Not yet implemented")
			}
			this.events.add(e)
		}
	}

	fun loadPlayers(db: AppDatabase){
		if(!done){
			players.addAll(db.matchPlayerDao().getAll().map { PlayerMatch(db, db.playerDao().get(it.player)!!, it.InMatch) }.sortedBy { it.player.name })
		}
	}

	fun getSummary(team: String, quart: Int): Summary = when(team) {
		myTeam -> myTeamSum[quart - 1]
		otherTeam -> otherTeamSum[quart - 1]
		else -> throw IllegalArgumentException()
	}

	fun clean(){
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