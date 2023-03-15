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
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.drawer.*
import fr.imacaron.robobrole.home.HomeScreen
import fr.imacaron.robobrole.home.TeamScreen
import fr.imacaron.robobrole.match.MatchScreen
import fr.imacaron.robobrole.match.NewMatchScreen
import fr.imacaron.robobrole.home.SettingScreen
import fr.imacaron.robobrole.service.*
import fr.imacaron.robobrole.state.PrefState
import fr.imacaron.robobrole.ui.theme.RobobroleTheme
import kotlinx.coroutines.*

import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException

class MainActivity : ComponentActivity(), ShareDownloadService {

	lateinit var db: AppDatabase

	@OptIn(DelicateCoroutinesApi::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		db = Room.databaseBuilder(
			applicationContext,
			AppDatabase::class.java,
			"match"
		).fallbackToDestructiveMigration().build()
		val sharedPref = getSharedPreferences("fr.imacaron.robobrole.settings", Context.MODE_PRIVATE)
		val prefState = PrefState(sharedPref)
		val matchService = MatchService(db)
		val newMatchService = NewMatchService(db, prefState)
		val teamService = TeamService(db, prefState)
		val homeService = HomeService(db)
		val settingService = SettingService(db, prefState)
		GlobalScope.launch {
			homeService.loadHistory()
		}
		setContent {
			val navController = rememberNavController()
			val navigator = NavigationService(navController, matchService, newMatchService, homeService)
			RobobroleTheme(darkTheme = prefState.theme) {
				NavHost(navController, startDestination = "home", modifier = Modifier.fillMaxSize()){
					composable("home"){ HomeScreen(navigator, homeService) }
					composable("match") { MatchScreen(navigator, matchService, this@MainActivity) }
					composable("new_match") { NewMatchScreen(newMatchService, navigator) }
					composable("team") { TeamScreen(teamService, navigator) }
					composable("settings") { SettingScreen(settingService, navigator) }
				}
			}
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	override fun share(service: MatchService){
		GlobalScope.launch(Dispatchers.IO){
			val data = getCsv(service)
			filesDir.resolve("match").apply {
				if(!exists()){
					mkdir()
				}
			}
			val f = File(filesDir, "match/${service.matchName}")
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

	override fun download(service: MatchService){
		val data = getCsv(service)
		val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
		val initName = "${service.matchName}-${service.date.dayOfMonth}-${service.date.monthValue}-${service.date.year}"
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

}
