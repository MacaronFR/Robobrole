package fr.imacaron.robobrole.types

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.imacaron.robobrole.db.Info
import fr.imacaron.robobrole.db.MatchEvent
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

	val events: MutableList<MatchEvent> = mutableListOf()

	val localSummary = List(4) { Summary() }

	val visitorSummary = List(4) { Summary() }
	
	fun loadFromInfo(info: Info){
		current = info.uid
		gender = info.gender
		startAt = info.matchStart
		level = info.level
		local = info.local
		visitor = info.visitor
		done = info.done
		date = info.date
	}

	fun loadEvents(events: List<MatchEvent>){
		println(events)
		events.sortedBy { it.time }.forEach {e ->
			val summary = when(e.team){
				local -> localSummary
				visitor -> visitorSummary
				else -> throw IllegalArgumentException()
			}
			when(e.type) {
				Type.Point -> {
					when(e.data){
						"1" -> { summary[e.quart - 1].one++ }
						"2" -> { summary[e.quart - 1].two++ }
						"3" -> { summary[e.quart - 1].three++ }
						"L" -> { summary[e.quart - 1].player++ }
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