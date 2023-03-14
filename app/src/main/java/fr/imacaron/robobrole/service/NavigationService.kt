package fr.imacaron.robobrole.service

import androidx.navigation.NavController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NavigationService(
	private val navController: NavController,
	private val matchService: MatchService,
	private val newMatchService: NewMatchService,
	private val homeService: HomeService
) {

	fun navigateTeam(){
		navController.navigate("team")
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun navigateNewMatch(){
		GlobalScope.launch{
			matchService.cleanCurrent()
			newMatchService.loadPlayers()
		}
		navController.navigate("new_match")
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun navigateMatch(match: Long){
		GlobalScope.launch {
			matchService.loadMatch(match)
		}
		navController.navigate("match"){ popUpTo("home") }
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun navigateUp(){
		homeService.cleanHistory()
		GlobalScope.launch{
			newMatchService.loadPlayers()
			homeService.loadHistory()
		}
		navController.navigateUp()
	}

	fun navigateSettings(){
		navController.navigate("settings")
	}
}