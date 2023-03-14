package fr.imacaron.robobrole.service

import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Event
import fr.imacaron.robobrole.db.Type
import fr.imacaron.robobrole.state.MatchState
import fr.imacaron.robobrole.state.PlayerMatch
import fr.imacaron.robobrole.state.Summary
import fr.imacaron.robobrole.types.Gender
import kotlinx.coroutines.*
import java.time.LocalDate

class MatchService(private val db: AppDatabase) {

	val state = MatchState()

	var myTeam: String
		get() = state.myTeam
		set(value){ state.myTeam = value }

	val myTeamSummary: List<Summary>
		get() = state.myTeamSum

	var otherTeam: String
		get() = state.otherTeam
		set(value) { state.otherTeam = value }

	val otherTeamSummary: List<Summary>
		get() = state.otherTeamSum

	var quart: Int
		get() = state.quart
		set(value) {
			if(value < 1 || value > 4){
				throw IllegalArgumentException()
			}
			state.quart = value
		}

	val done: Boolean
		get() = state.done

	val start: Boolean
		get() = state.startAt != 0L

	val players: List<PlayerMatch>
		get() = state.players

	val matchName: String
		get() = "${state.myTeam} - ${state.otherTeam} - ${state.level}${if(state.gender == Gender.Women) "F" else "M"}"

	val events: List<Event>
		get() = state.events

	val date: LocalDate
		get() = state.date


	@OptIn(DelicateCoroutinesApi::class)
	fun addPoint(amount: Int, team: String, player: String = ""){
		if(state.done){
			throw IllegalStateException()
		}
		val sum = state.getSummary(team, state.quart)
		sum[amount]++
		val event = Event(Type.Point, team, player, amount.toString(), state.quart, state.current, state.startAt)
		GlobalScope.launch(Dispatchers.IO) {
			val id = db.eventDAO().insertEvent(event)
			event.uid = id
			state.events.add(event)
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun removePoint(amount: Int, team: String){
		if(state.done){
			throw IllegalStateException()
		}
		val sum = state.getSummary(team, state.quart)
		sum[amount]--
		val ev = state.events.findLast { it.team == team && it.quart == state.quart && it.type == Type.Point && it.data == amount.toString() } ?: throw IllegalArgumentException()
		GlobalScope.launch(Dispatchers.IO) {
			db.eventDAO().deleteEvent(ev.uid)
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun changeOut(player: PlayerMatch){
		if(state.done){
			throw IllegalStateException()
		}
		player onMatch false
		val ev = Event(Type.Change, state.myTeam, player.player.name, "out", state.quart, state.current, state.startAt)
		state.events.add(ev)
		GlobalScope.launch(Dispatchers.IO){
			db.eventDAO().insertEvent(ev)
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun changeIn(player: PlayerMatch){
		if(state.done){
			throw IllegalStateException()
		}
		player onMatch true
		val ev = Event(Type.Change, state.myTeam, player.player.name, "in", state.quart, state.current, state.startAt)
		state.events.add(ev)
		GlobalScope.launch(Dispatchers.IO){
			db.eventDAO().insertEvent(ev)
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun fault(player: String){
		val event = Event(Type.Fault, state.myTeam, player, "", state.quart, state.current, state.startAt)
		GlobalScope.launch(Dispatchers.IO) {
			db.eventDAO().insertEvent(event)
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun start(at: Long){
		if(state.done || state.startAt != 0L){
			throw IllegalStateException()
		}
		state.startAt = at
		GlobalScope.launch(Dispatchers.IO){
			db.matchDao().setStart(state.startAt, state.current)
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun save(){
		if(state.done){
			throw IllegalStateException()
		}
		state.done = true
		GlobalScope.launch(Dispatchers.IO){
			db.matchDao().setDone(state.current)
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun loadMatch(id: Long){
		GlobalScope.launch(Dispatchers.IO) {
			state.clean()
			loadMatchInfo(id)
			loadEvents()
			loadTeam()
			loadSummary()
		}
	}

	private fun loadMatchInfo(id: Long){
		val match = db.matchDao().get(id)
		state.current = match.uid
		state.startAt = match.matchStart
		state.myTeam = match.myTeam
		state.otherTeam = match.otherTeam
		state.done = match.done
		state.level = match.level
	}

	private fun loadEvents(){
		val events = db.eventDAO().getByMatch(state.current)
		state.events.addAll(events)
	}

	private fun loadTeam(){
		val players = db.matchPlayerDao().getByMatch(state.current).map {
			val p = db.playerDao().get(it.player) ?: throw IllegalArgumentException()
			PlayerMatch(db, p, it.inMatch)
		}
		state.players.addAll(players)
	}

	private fun loadSummary(){
		state.events.forEach {
			if(it.type == Type.Point){
				val sum = state.getSummary(it.team, it.quart)
				sum[it.data.toInt()] = sum[it.data.toInt()] + 1
			}
		}
	}

	suspend fun cleanCurrent(){
		withContext(Dispatchers.IO){
			db.matchDao().getCurrent()?.let{ current ->
				db.matchDao().deleteCurrent()
				db.eventDAO().deleteMatch(current.uid)
				db.matchPlayerDao().deleteMatchPlayer(current.uid)
			}
		}
	}
}