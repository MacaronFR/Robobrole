@file:Suppress("FunctionName")

package fr.imacaron.robobrole.match

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import fr.imacaron.robobrole.service.SettingService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(service: SettingService){
	Scaffold(
		topBar = { TopAppBar({ Text("Paramètres") }) }
	) {
		Column(Modifier.padding(it)) {
			Row {
				Text("Theme")
				Button(
					{

					}
				){
					Text("OULA")
				}
			}
			Divider()
			Row(Modifier.pointerInput(Unit){ GlobalScope.launch { service.deleteHistory() } }) {
				Text("Supprimer l'historique")
			}
		}
	}
}