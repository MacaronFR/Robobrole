package fr.imacaron.robobrole.activity

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import fr.imacaron.robobrole.match.MatchScreen
import fr.imacaron.robobrole.components.AppBar
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.home.HomeScreen
import fr.imacaron.robobrole.match.NewMatchScreen
import fr.imacaron.robobrole.match.StatScreen
import fr.imacaron.robobrole.home.TeamScreen
import fr.imacaron.robobrole.types.MatchState
import fr.imacaron.robobrole.types.PrefState
import fr.imacaron.robobrole.types.UIState
import fr.imacaron.robobrole.ui.theme.RobobroleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.StringBuilder
import java.nio.charset.Charset

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
					topBar = { AppBar(prefState, db, navController, uiState, matchState) }
				) {
					NavHost(navController, startDestination = "home", modifier = Modifier.fillMaxSize().padding(it)){
						composable("home"){ HomeScreen(navController, db, uiState, matchState) }
						composable("new_match"){ NewMatchScreen(navController, db, uiState, prefState) }
						composable("match/{current}", arguments = listOf(navArgument("current"){ type = NavType.LongType })){ entries -> MatchScreen(matchState, db, navController, uiState, entries.arguments!!.getLong("current"), prefState.left) }
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
		saveHistory(state.current, getCsv())
	}

	suspend fun export(matchState: MatchState){
		val f = openFileInput(matchState.current.toString())
		val data = f.readAllBytes().toString(Charset.defaultCharset())
		val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
		val initName = "${matchState.local}-${matchState.visitor}-${matchState.level}-${matchState.gender.value}-${matchState.date.dayOfMonth}-${matchState.date.monthValue}-${matchState.date.year}"
		var name = "$initName.csv"
		var index = 0
		val fileNames = dir.listFiles()?.map { it.name } ?: listOf()
		while (name in fileNames) {
			index++
			name = "$initName$index.csv"
		}
		saveFile(dir, name, data)
	}

	private suspend fun saveHistory(id: Long, data: String){
		val f = openFileOutput(id.toString(), 0)
		try {
			f.write(data.toByteArray())
			withContext(Dispatchers.Main){
				Toast.makeText(baseContext, "Sauvegardé", Toast.LENGTH_SHORT).show()
			}
		} catch (e: IOException) {
			withContext(Dispatchers.Main){
				Toast.makeText(baseContext, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show()
			}
			e.printStackTrace()
		}
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

	private fun getCsv(): String {
		val res = StringBuilder()
		res.appendLine("Type;Équipe;Data;Time;Quart")
		val events = db.eventDAO().getAll()
		events.forEach { e ->
			res.appendLine("${e.type};${e.team};${e.data};${e.time};${e.quart}")
		}
		return res.toString()
	}

	suspend fun removeAllSave(){
		filesDir.listFiles()?.forEach { it.delete() }
	}

}
