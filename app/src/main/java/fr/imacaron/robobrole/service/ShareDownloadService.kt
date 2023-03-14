package fr.imacaron.robobrole.service

import fr.imacaron.robobrole.db.Type
import java.lang.StringBuilder

interface ShareDownloadService {
	fun getCsv(service: MatchService): String{
		val res = StringBuilder()
		res.appendLine("Quart;Temps;${service.myTeam};${service.otherTeam};Player;P1;P2;P3;P4;P5")
		val players = mutableListOf<String>()
		service.events.forEach { e ->
			when(e.type){
				Type.Point -> when(e.team){
					service.myTeam -> res.appendLine("${e.quart};${e.time};${e.data};0;${e.player};${players.joinToString(";")}")
					service.otherTeam -> res.appendLine("${e.quart};${e.time};0;${e.data};${e.player};${players.joinToString(";")}")
				}
				Type.Change -> {
					if(e.data == "in"){
						players.add(e.player)
					}else {
						players.remove(e.player)
					}
				}
				else -> {  }
			}
		}
		return res.toString()
	}

	fun share(service: MatchService)

	fun download(service: MatchService)
}