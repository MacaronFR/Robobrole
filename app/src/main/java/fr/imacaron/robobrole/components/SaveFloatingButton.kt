package fr.imacaron.robobrole.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import fr.imacaron.robobrole.R
import fr.imacaron.robobrole.activity.MainActivity
import fr.imacaron.robobrole.types.AppState

@Composable
fun SaveFloatingButton(appState: AppState){
	FloatingActionButton({ (LocalContext.current as MainActivity).save(appState.points)}) {
		Icon(ImageVector.vectorResource(R.drawable.save), null)
	}
}