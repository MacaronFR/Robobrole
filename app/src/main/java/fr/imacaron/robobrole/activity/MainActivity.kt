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
import fr.imacaron.robobrole.types.Points
import fr.imacaron.robobrole.components.AppBar
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.home.HomeScreen
import fr.imacaron.robobrole.match.NewMatchScreen
import fr.imacaron.robobrole.types.AppState
import fr.imacaron.robobrole.ui.theme.RobobroleTheme

import java.io.File
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
		).build()
		val sharedPref = getSharedPreferences("fr.imacaron.robobrole.settings", Context.MODE_PRIVATE)
		val appState = AppState(sharedPref)
		setContent {
			val navController = rememberNavController()
			RobobroleTheme(darkTheme = appState.theme) {
				Scaffold(
					topBar = { AppBar(appState) },
				) {
					NavHost(navController, startDestination = "home", modifier = Modifier.fillMaxSize().padding(it)){
						composable("home"){ HomeScreen(navController) }
						composable("new_match"){ NewMatchScreen(navController, appState) }
						composable("match"){ MatchScreen(appState) }
					}
				}
			}
		}
	}

	fun save(points: List<Points>) {
		val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
		var name = "result_match.csv"
		var index = 0
		val fileNames = dir.listFiles()?.map { it.name } ?: listOf()
		println(fileNames)
		while (name in fileNames) {
			index++
			name = "match$index.csv"
		}
		saveFile(dir, name, getCsv(points))
	}

	private fun saveFile(dir: File, fileName: String, data: String) {
		val f = File(dir, fileName)
		try {
			val fw = FileWriter(f)
			fw.append(data)
			fw.close()
			Toast.makeText(baseContext, "Sauvegardé", Toast.LENGTH_SHORT).show()
		} catch (e: IOException) {
			Toast.makeText(baseContext, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show()
			e.printStackTrace()
		}
	}

	private fun getCsv(points: List<Points>): String {
		val res = StringBuilder()
		res.appendLine("1 point;2 points;3 points;lucille")
		points.forEach { p ->
			res.appendLine("${p.one};${p.two};${p.three};${p.lucille}")
		}
		return res.toString()
	}

}
