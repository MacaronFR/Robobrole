package fr.imacaron.robobrole.types

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.imacaron.robobrole.db.Match
import fr.imacaron.robobrole.db.Event
import fr.imacaron.robobrole.db.Type
import java.time.LocalDate

@Stable
class MatchState {

	var date: LocalDate by mutableStateOf(LocalDate.now())

	var current: Long by mutableStateOf(-1)

	var gender: Gender by mutableStateOf(Gender.Women)

	var startAt: Long by mutableStateOf(0)

	var level: String by mutableStateOf("")

	var local: String by mutableStateOf("")

	var visitor: String by mutableStateOf("")

	var done: Boolean by mutableStateOf(false)

	val events: MutableList<Event> = mutableListOf()

	val localSummary = List(4) { Summary() }

	val visitorSummary = List(4) { Summary() }
	
	fun loadFromMatch(match: Match){
		current = match.uid
		gender = match.gender
		startAt = match.matchStart
		level = match.level
		local = match.local
		visitor = match.visitor
		done = match.done
		date = match.date
	}

	fun loadEvents(events: List<Event>){
		events.sortedBy { it.time }.forEach {e ->
			val summary = when(e.team){
				local -> localSummary
				visitor -> visitorSummary
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

	fun getSummary(team: String, quart: Int): Summary = when(team) {
		local -> localSummary[quart - 1]
		visitor -> visitorSummary[quart - 1]
		else -> throw IllegalArgumentException()
	}

	fun clean(){
		date = LocalDate.now()
		current = -1
		gender = Gender.Women
		startAt = 0
		level = "0"
		local = ""
		visitor = ""
		events.clear()
		localSummary.clean()
		visitorSummary.clean()
	}
}