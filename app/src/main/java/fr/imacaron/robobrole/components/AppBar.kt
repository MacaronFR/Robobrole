package fr.imacaron.robobrole.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import fr.imacaron.robobrole.R
import fr.imacaron.robobrole.activity.MainActivity
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.types.AppState
import fr.imacaron.robobrole.types.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AppBar(appState: AppState, db: AppDatabase, nav: NavController){
	val activity = LocalContext.current as MainActivity
	TopAppBar(
		title = { Text("Robobrole") },
		navigationIcon = {
			if(!appState.home){
				IconButton({
					nav.navigateUp()
				}){
					Icon(Icons.Default.ArrowBack, null)
				}
			}
		},
		actions = {
			IconButton(onClick = {
				if(appState.theme == Theme.Dark){
					appState.setLightTheme()
				}else if(appState.theme == Theme.Light){
					appState.setDarkTheme()
				}
			}) {
				if(appState.theme == Theme.Default){
					if(isSystemInDarkTheme()){
						appState.setDarkTheme(false)
					} else {
						appState.setLightTheme(false)
					}
				}
				if(appState.theme == Theme.Dark){
					Icon(ImageVector.vectorResource(R.drawable.sun), null)
				}else if(appState.theme == Theme.Light){
					Icon(ImageVector.vectorResource(R.drawable.moon), null)
				}
			}
			if(appState.done && appState.infoId != 0L && !appState.home){
				IconButton(
					{
						GlobalScope.launch(Dispatchers.IO) {
							activity.export(appState.infoId, appState)
						}
					}
				){
					Icon(ImageVector.vectorResource(R.drawable.file_copy), null)
				}
			}
			Box{
				IconButton({appState.toggleMenu()}){
					Icon(Icons.Filled.MoreVert, null)
				}
				DropdownMenu(expanded = appState.displayMenu, onDismissRequest = { appState.closeMenu() }){
					DropdownMenuItem(
						text = { Text(if(appState.left) "Gaucher" else "Droitier") },
						onClick = {
							appState.toggleLeftHanded()
						},
						leadingIcon = { Icon(ImageVector.vectorResource(R.drawable.back_hand), null, Modifier.scale(if(appState.left) -1f else 1f, 1f)) }
					)
					DropdownMenuItem(
						text = { Text("Réinitialiser les paramètre") },
						onClick = { appState.setDefaultTheme() },
						leadingIcon = { Icon(Icons.Outlined.Refresh, null) }
					)
					DropdownMenuItem(
						text = { Text("Supprimer toutes les données") },
						onClick = { appState.alert = true },
						leadingIcon = { Icon(Icons.Default.Delete, null)}
					)
				}
				if(appState.alert){
					AlertDialog(
						{
							appState.alert = false
						},
						{
							Button({
								GlobalScope.launch(Dispatchers.IO) {
									db.infoDao().removeAll()
									db.summaryDao().wipeTable()
									db.matchDao().wipeTable()
									activity.removeAllSave()
									appState.alert = false
									appState.displayMenu = false
								}
							}){ Text("Supprimer") }
						},
						dismissButton = { Button({ appState.alert = false }){ Text("Annuler") } },
						icon = { Icon(Icons.Default.Delete, null) },
						title = { Text("Supprimer toute les données") },
						text = { Text("Êtes vous sur de vouloir supprimer toutes les données.\nCette action est irréversible") },
						properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = true, decorFitsSystemWindows = true)
					)
				}
			}
		}
	)
}