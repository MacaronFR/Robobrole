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
import fr.imacaron.robobrole.components.AppBar
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.drawer.*
import fr.imacaron.robobrole.home.HomeScreen
import fr.imacaron.robobrole.home.TeamScreen
import fr.imacaron.robobrole.match.MatchScreen
import fr.imacaron.robobrole.match.NewMatchScreen
import fr.imacaron.robobrole.service.MatchService
import fr.imacaron.robobrole.service.ShareDownloadService
import fr.imacaron.robobrole.state.MatchState
import fr.imacaron.robobrole.state.PrefState
import fr.imacaron.robobrole.state.UIState
import fr.imacaron.robobrole.ui.theme.RobobroleTheme
import kotlinx.coroutines.*

import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException

class MainActivity : ComponentActivity(), ShareDownloadService {

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
		val matchService = MatchService(db, matchState)
		setContent {
			val navController = rememberNavController()
			val navController2 = rememberNavController()
			RobobroleTheme(darkTheme = prefState.theme) {
				NavHost(navController, startDestination = "home", modifier = Modifier.fillMaxSize()){
					composable("home"){
						Scaffold(
							topBar = { AppBar(prefState, db, navController2, uiState, matchState) },
							bottomBar = { Navigation(navController2, db, uiState, matchState) }
						) {
							Box(Modifier.fillMaxSize().padding(it)){
								NavHost(navController2, startDestination = "home", modifier = Modifier.fillMaxSize().padding(it)) {
									composable("home"){ HomeScreen(navController, db, uiState) }
									composable("new_match"){ NewMatchScreen(navController2, db, uiState, prefState) }
									composable("team"){ TeamScreen(db, uiState, prefState) }
								}
							}
						}
					}
					composable("match/{current}", arguments = listOf(navArgument("current"){ type = NavType.LongType })){ entries -> MatchScreen(navController, matchService, entries.arguments!!.getLong("current"), this@MainActivity) }
				}
			}
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	override fun share(matchState: MatchState){
		GlobalScope.launch(Dispatchers.IO){
			val data = getCsv(matchState)
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

	override fun download(matchState: MatchState){
		val data = getCsv(matchState)
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

	@OptIn(DelicateCoroutinesApi::class)
	private fun saveFile(dir: File, fileName: String, data: String) {
		GlobalScope.launch(Dispatchers.IO){
			val f = File(dir, fileName)
			try {
				val fw = FileWriter(f)
				fw.append(data)
				fw.close()
				withContext(Dispatchers.Main){
					Toast.makeText(baseContext, "Téléchargé", Toast.LENGTH_SHORT).show()
				}
			} catch (e: IOException) {
				withContext(Dispatchers.Main){
					Toast.makeText(baseContext, "Erreur lors du téléchargement", Toast.LENGTH_SHORT).show()
				}
				e.printStackTrace()
			}
		}
	}

	fun removeAllSave(){
		filesDir.listFiles()?.forEach { it.delete() }
	}

}
