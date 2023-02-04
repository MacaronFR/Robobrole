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
import fr.imacaron.robobrole.match.MatchScreen
import fr.imacaron.robobrole.types.Points
import fr.imacaron.robobrole.components.AppBar
import fr.imacaron.robobrole.components.SaveFloatingButton
import fr.imacaron.robobrole.types.AppState
import fr.imacaron.robobrole.ui.theme.RobobroleTheme

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.StringBuilder

class MainActivity : ComponentActivity() {
	@OptIn(ExperimentalMaterial3Api::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			val sharedPref = getSharedPreferences("fr.imacaron.robobrole.settings", Context.MODE_PRIVATE)
			val appState = AppState(sharedPref)
			RobobroleTheme(darkTheme = appState.theme) {
				Scaffold(
					topBar = { AppBar(appState) },
					floatingActionButton = { SaveFloatingButton(appState) }
				) {
					Surface(modifier = Modifier.fillMaxSize().padding(it)) {
						MatchScreen(appState.points)
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