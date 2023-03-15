@file:Suppress("FunctionName")

package fr.imacaron.robobrole.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.service.NavigationService
import fr.imacaron.robobrole.service.SettingService
import fr.imacaron.robobrole.types.Theme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun SettingRow(onTap: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit){
	Row(modifier.fillMaxWidth().requiredHeight(48.dp).pointerInput(Unit){ detectTapGestures { onTap() } }.padding(16.dp, 0.dp), verticalAlignment = Alignment.CenterVertically){
		content()
	}
}

@OptIn(DelicateCoroutinesApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(service: SettingService, navigator: NavigationService){
	BackHandler { navigator.navigateUp() }
	Scaffold(
		topBar = { TopAppBar(
			{ Text("Paramètres") },
			navigationIcon = { IconButton({ navigator.navigateUp() }){ Icon(Icons.Outlined.ArrowBack, "Back") } }
		) }
	) {
		Column(Modifier.padding(it)) {
			SettingRow({ service.toggleThemeDialog() }){
				Text("Sélectionnez un thème pour l'application")
			}
			Divider()
			SettingRow({ service.toggleHistoryDialog() }){
				Text("Supprimer l'historique")
			}
			Divider()
			SettingRow({ service.toggleDeleteDialog() }){
				Text("Supprimer toutes les données")
			}
		}
		if(service.themeDialog){
			AlertDialog(
				{ service.toggleThemeDialog() },
				{ TextButton({ service.toggleThemeDialog() }){ Text("Fermer") } },
				title = { Text("Sélectionner un thème") },
				text = {
					Column {
						Row(Modifier.pointerInput(Unit){ detectTapGestures { service.theme = Theme.Light } }, verticalAlignment = Alignment.CenterVertically) {
							RadioButton(service.theme == Theme.Light, { service.theme = Theme.Light })
							Text("Thème clair")
						}
						Row(Modifier.pointerInput(Unit){ detectTapGestures { service.theme = Theme.Dark } }, verticalAlignment = Alignment.CenterVertically) {
							RadioButton(service.theme == Theme.Dark, { service.theme = Theme.Dark })
							Text("Thème sombre")
						}
						Row(Modifier.pointerInput(Unit){ detectTapGestures { service.theme = Theme.Default } }, verticalAlignment = Alignment.CenterVertically) {
							RadioButton(service.theme == Theme.Default, { service.theme = Theme.Default })
							Text("Thème par défaut")
						}
					}
				}
			)
		}
		if(service.historyDialog){
			AlertDialog(
				{ service.toggleHistoryDialog() },
				{ FilledTonalButton({ GlobalScope.launch { service.deleteHistory() }; service.toggleHistoryDialog() }){ Text("Supprimer") } },
				title = { Text("Supprimer l'historique") },
				text = { Text("Cette action est irréversible") }
			)
		}
		if(service.deleteDialog){
			AlertDialog(
				{ service.toggleDeleteDialog() },
				{ FilledTonalButton({ GlobalScope.launch { service.deleteAllData() }; service.toggleDeleteDialog() }){ Text("Supprimer") } },
				title = { Text("Supprimer les données") },
				text = { Text("Cette action est irréversible") }
			)
		}
	}
}