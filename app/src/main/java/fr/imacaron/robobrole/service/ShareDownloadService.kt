package fr.imacaron.robobrole.service

import fr.imacaron.robobrole.db.Type
import fr.imacaron.robobrole.state.MatchState
import java.lang.StringBuilder

interface ShareDownloadService {
	fun getCsv(state: MatchState): String{
		val res = StringBuilder()
		res.appendLine("Quart;Temps;${state.myTeam};${state.otherTeam};Player")
		state.events.forEach { e ->
			if(e.type == Type.Point){
				when(e.team){
					state.myTeam -> res.appendLine("${e.quart};${e.time};${e.data};0;${e.player}")
					state.otherTeam -> res.appendLine("${e.quart};${e.time};0;${e.data};${e.player}")
				}
			}
		}
		return res.toString()
	}

	fun share(matchState: MatchState)

	fun download(matchState: MatchState)
}