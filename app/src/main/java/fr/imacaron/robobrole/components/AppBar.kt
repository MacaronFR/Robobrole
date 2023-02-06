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
import fr.imacaron.robobrole.types.MatchState
import fr.imacaron.robobrole.types.PrefState
import fr.imacaron.robobrole.types.Theme
import fr.imacaron.robobrole.types.UIState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
@Composable
fun AppBar(prefState: PrefState, db: AppDatabase, nav: NavController, uiState: UIState, matchState: MatchState){
	val activity = LocalContext.current as MainActivity
	TopAppBar(
		title = { Text("Robobrole") },
		navigationIcon = {
			if(!uiState.home){
				IconButton({
					nav.navigateUp()
				}){
					Icon(Icons.Default.ArrowBack, null)
				}
			}
		},
		actions = {
			IconButton(onClick = {
				if(prefState.theme == Theme.Dark){
					prefState.setLightTheme()
				}else if(prefState.theme == Theme.Light){
					prefState.setDarkTheme()
				}
			}) {
				if(prefState.theme == Theme.Default){
					if(isSystemInDarkTheme()){
						prefState.setDarkTheme(false)
					} else {
						prefState.setLightTheme(false)
					}
				}
				if(prefState.theme == Theme.Dark){
					Icon(ImageVector.vectorResource(R.drawable.sun), null)
				}else if(prefState.theme == Theme.Light){
					Icon(ImageVector.vectorResource(R.drawable.moon), null)
				}
			}
			if(uiState.export){
				IconButton(
					{
						GlobalScope.launch(Dispatchers.IO) {
							activity.export(matchState)
						}
					}
				){
					Icon(ImageVector.vectorResource(R.drawable.file_copy), null)
				}
			}
			Box{
				IconButton({uiState.toggleMenu()}){
					Icon(Icons.Filled.MoreVert, null)
				}
				DropdownMenu(expanded = uiState.displayMenu, onDismissRequest = { uiState.closeMenu() }){
					DropdownMenuItem(
						text = { Text(if(prefState.left) "Gaucher" else "Droitier") },
						onClick = {
							prefState.toggleLeftHanded()
						},
						leadingIcon = { Icon(ImageVector.vectorResource(R.drawable.back_hand), null, Modifier.scale(if(prefState.left) -1f else 1f, 1f)) }
					)
					DropdownMenuItem(
						text = { Text("Réinitialiser les paramètre") },
						onClick = {
							prefState.setDefaultTheme()
							prefState.setDefaultHand()
						},
						leadingIcon = { Icon(Icons.Outlined.Refresh, null) }
					)
					DropdownMenuItem(
						text = { Text("Supprimer toutes les données") },
						onClick = { uiState.alert = true },
						leadingIcon = { Icon(Icons.Default.Delete, null)}
					)
				}
				if(uiState.alert){
					AlertDialog(
						{
							uiState.alert = false
						},
						{
							Button({
								GlobalScope.launch(Dispatchers.IO) {
									db.infoDao().removeAll()
									db.summaryDao().wipeTable()
									db.matchDao().wipeTable()
									activity.removeAllSave()
									uiState.alert = false
									uiState.closeMenu()
								}
							}){ Text("Supprimer") }
						},
						dismissButton = { Button({ uiState.alert = false }){ Text("Annuler") } },
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