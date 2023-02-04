package fr.imacaron.robobrole.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import fr.imacaron.robobrole.R
import fr.imacaron.robobrole.types.AppState
import fr.imacaron.robobrole.types.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(appState: AppState){
	TopAppBar(
		title = { Text("Robobrole") },
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
			Box{
				IconButton({appState.toggleMenu()}){
					Icon(Icons.Filled.MoreVert, null)
				}
				DropdownMenu(expanded = appState.displayMenu, onDismissRequest = { appState.closeMenu() }){
					DropdownMenuItem(
						text = { Text("Réinitialiser les paramètre") },
						onClick = { appState.setDefaultTheme() },
						leadingIcon = { Icon(Icons.Outlined.Refresh, null) }
					)
				}
			}
		}
	)
}