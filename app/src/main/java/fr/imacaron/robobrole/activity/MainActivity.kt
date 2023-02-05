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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import fr.imacaron.robobrole.match.MatchScreen
import fr.imacaron.robobrole.components.AppBar
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.home.HomeScreen
import fr.imacaron.robobrole.match.NewMatchScreen
import fr.imacaron.robobrole.types.AppState
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
		val appState = AppState(sharedPref, db)
		setContent {
			val navController = rememberNavController()
			RobobroleTheme(darkTheme = appState.theme) {
				Scaffold(
					topBar = { AppBar(appState, db, navController) },
				) {
					NavHost(navController, startDestination = "home", modifier = Modifier.fillMaxSize().padding(it)){
						composable("home"){ HomeScreen(navController, db, appState) }
						composable("new_match"){ NewMatchScreen(navController, appState, db) }
						composable("match"){ MatchScreen(appState, db) }
					}
				}
			}
		}
	}

	suspend fun save(state: AppState) {
		state.done = true
		db.infoDao().setDone(state.infoId)
		saveHistory(state.infoId, getCsv())
	}

	suspend fun export(id: Long, state: AppState){
		val f = openFileInput(id.toString())
		val data = f.readAllBytes().toString(Charset.defaultCharset())
		val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
		val initName = "${state.local.name}-${state.visitor.name}-${state.level}${state.gender}"
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
				Toast.makeText(baseContext, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show()
			}
			e.printStackTrace()
		}
	}

	private fun getCsv(): String {
		val res = StringBuilder()
		res.appendLine("Type;Équipe;Data;Time;Quart")
		val events = db.matchDao().getAll()
		events.forEach { e ->
			res.appendLine("${e.type};${e.team};${e.data};${e.time};${e.quart}")
		}
		return res.toString()
	}

	suspend fun loadFile(id: Long): String{
		val f = openFileInput(id.toString())
		return f.readAllBytes().toString(Charset.defaultCharset())
	}

	suspend fun removeAllSave(){
		filesDir.listFiles()?.forEach { it.delete() }
	}

}
