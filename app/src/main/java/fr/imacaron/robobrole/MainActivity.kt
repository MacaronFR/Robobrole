package fr.imacaron.robobrole

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.ui.theme.RobobroleTheme

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.StringBuilder

enum class Theme(val value: Int){
	Dark(0),
	Light(1),
	Default(2)
}

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			val sharedPref = getSharedPreferences("fr.imacaron.robobrole.settings", Context.MODE_PRIVATE)
			var quart by remember { mutableStateOf(0) }
			val points = listOf(Points(), Points(), Points(), Points())
			var darkTheme by remember { mutableStateOf(Theme.values()[sharedPref.getInt("theme", 2)]) }
			var displayMenu by remember { mutableStateOf(false) }
			RobobroleTheme(darkTheme = darkTheme) {
				Scaffold(
					topBar = {
						TopAppBar(
							title = { Text("Robobrole") },
							actions = {
								IconButton(onClick = {
									sharedPref.edit().apply{
										putInt("theme", (if(darkTheme == Theme.Dark) Theme.Light else if(darkTheme == Theme.Light) Theme.Dark else Theme.Default).value)
										apply()
									}
									darkTheme = if(darkTheme == Theme.Dark) Theme.Light else if(darkTheme == Theme.Light) Theme.Dark else Theme.Default
								}) {
									if(darkTheme == Theme.Default){
										darkTheme = if(isSystemInDarkTheme()) Theme.Dark else Theme.Light
									}
									if(darkTheme == Theme.Dark){
										Icon(ImageVector.vectorResource(R.drawable.sun), null)
									}else if(darkTheme == Theme.Light){
										Icon(ImageVector.vectorResource(R.drawable.moon), null)
									}
								}
								Box{
									IconButton({displayMenu = !displayMenu}){
										Icon(Icons.Filled.MoreVert, null)
									}
									DropdownMenu(expanded = displayMenu, onDismissRequest = { displayMenu = false }){
										DropdownMenuItem(onClick = { points.forEach(Points::reinit); displayMenu = false; quart = 0 }){ Text("Nouveau match") }
										Divider()
										DropdownMenuItem(onClick = { sharedPref.edit().apply{ putInt("theme", 2);apply();darkTheme = Theme.Default } }){ Text("Réinitialiser les paramètre")}
									}
								}
							},
							elevation = 4.dp
						)
					},
					floatingActionButton = {
						FloatingActionButton({ save(points)}) {
							Icon(ImageVector.vectorResource(R.drawable.save), null)
						}
					}
				) {
					Surface(modifier = Modifier.fillMaxSize().padding(it)) {
						Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) {
							Row {
								ButtonColumn(points, quart)
								QuartColumn(points[0], quart == 0, "1") {
									quart = 0
								}
								QuartColumn(points[1], quart == 1, "2") {
									quart = 1
								}
								QuartColumn(points[2], quart == 2, "3") {
									quart = 2
								}
								QuartColumn(points[3], quart == 3, "4") {
									quart = 3
								}

							}
							Text(
								"Total : ${points.sumOf { it.tot() }}",
								Modifier.fillMaxWidth(),
								style = MaterialTheme.typography.h3,
								textAlign = TextAlign.Center
							)
						}
					}
				}
			}
		}
	}

	private fun save(points: List<Points>) {
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
		res.appendLine("1 point,2 points,3 points,lucille")
		points.forEach { p ->
			res.appendLine("${p.one},${p.two},${p.three},${p.lucille}")
		}
		return res.toString()
	}

}

@Preview(name = "LightMode")
@Preview(name = "DarkMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Preview() {
	RobobroleTheme {
		Surface {
			PressableButton() {
				Text("Coucou")
			}
		}
	}
}