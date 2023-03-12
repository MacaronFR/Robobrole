package fr.imacaron.robobrole.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import fr.imacaron.robobrole.match.MatchScreen
import fr.imacaron.robobrole.components.AppBar
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Event
import fr.imacaron.robobrole.drawer.*
import fr.imacaron.robobrole.home.HomeScreen
import fr.imacaron.robobrole.match.NewMatchScreen
import fr.imacaron.robobrole.match.StatScreen
import fr.imacaron.robobrole.home.TeamScreen
import fr.imacaron.robobrole.types.MatchState
import fr.imacaron.robobrole.types.PrefState
import fr.imacaron.robobrole.types.UIState
import fr.imacaron.robobrole.ui.theme.RobobroleTheme
import kotlinx.coroutines.*

import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.lang.StringBuilder

class MainActivity : ComponentActivity() {

	lateinit var db: AppDatabase

	@OptIn(ExperimentalMaterial3Api::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		db = Room.databaseBuilder(
			applicationContext,
			AppDatabase::class.java, "match"
		).fallbackToDestructiveMigration().build()
		val sharedPref = getSharedPreferences("fr.imacaron.robobrole.settings", Context.MODE_PRIVATE)
		val matchState = MatchState()
		val prefState = PrefState(sharedPref)
		val uiState = UIState()
		setContent {
			val navController = rememberNavController()
			RobobroleTheme(darkTheme = prefState.theme) {
				Scaffold(
					topBar = { AppBar(prefState, db, navController, uiState, matchState) },
					bottomBar = { Navigation(navController, db, uiState, matchState) }
				) {
					NavHost(navController, startDestination = "home", modifier = Modifier.fillMaxSize().padding(it)){
						composable("home"){ HomeScreen(navController, db, uiState, matchState) }
						composable("new_match"){ NewMatchScreen(navController, db, uiState, prefState) }
						composable("match/{current}", arguments = listOf(navArgument("current"){ type = NavType.LongType })){ entries -> MatchScreen(matchState, db, uiState, entries.arguments!!.getLong("current"), prefState.left) }
						composable("stat"){ StatScreen(matchState) }
						composable("team"){ TeamScreen(db, uiState, prefState) }
					}
				}
			}
		}
	}

	suspend fun save(state: MatchState) {
		state.done = true
		db.matchDao().setDone(state.current)
		withContext(Dispatchers.Main){
			Toast.makeText(baseContext, "Sauvegardé", Toast.LENGTH_SHORT).show()
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun share(matchState: MatchState){
		GlobalScope.launch(Dispatchers.IO){
			val data = getCsv(matchState.events, matchState.myTeam, matchState.otherTeam)
			filesDir.resolve("match").apply {
				if(!exists()){
					mkdir()
				}
			}
			val f = File(filesDir, "match/match.csv")
			val out = FileOutputStream(f)
			out.write(data.toByteArray())
			out.close()
			val shareIntent = Intent().apply {
				action = Intent.ACTION_SEND
				addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
				putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@MainActivity, "fr.imacaron.robobrole", f))
				type = "text/csv"
			}
			startActivity(Intent.createChooser(shareIntent, "Test"))
		}
	}

	suspend fun export(matchState: MatchState){
		val data = getCsv(matchState.events, matchState.myTeam, matchState.otherTeam)
		val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
		val initName = "${matchState.myTeam}-${matchState.otherTeam}-${matchState.level}-${matchState.gender.value}-${matchState.date.dayOfMonth}-${matchState.date.monthValue}-${matchState.date.year}"
		var name = "$initName.csv"
		var index = 0
		val fileNames = dir.listFiles()?.map { it.name } ?: listOf()
		while (name in fileNames) {
			index++
			name = "$initName$index.csv"
		}
		saveFile(dir, name, data)
	}

	private suspend fun saveFile(dir: File, fileName: String, data: String) {
		val f = File(dir, fileName)
		try {
			val fw = FileWriter(f)
			fw.append(data)
			fw.close()
			withContext(Dispatchers.Main){
				Toast.makeText(baseContext, "Exporté", Toast.LENGTH_SHORT).show()
			}
		} catch (e: IOException) {
			withContext(Dispatchers.Main){
				Toast.makeText(baseContext, "Erreur lors de l'exportation", Toast.LENGTH_SHORT).show()
			}
			e.printStackTrace()
		}
	}

	fun getCsv(events: List<Event>, own: String, other: String): String {
		val res = StringBuilder()
		res.appendLine("Quart;Temps;$own;$other;Player")
		events.forEach { e ->
			when(e.team){
				own -> res.appendLine("${e.quart};${e.time};${e.data};0;${e.player}")
				other -> res.appendLine("${e.quart};${e.time};0;${e.data};${e.player}")
			}
		}
		return res.toString()
	}

	suspend fun removeAllSave(){
		filesDir.listFiles()?.forEach { it.delete() }
	}

}
