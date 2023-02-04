package fr.imacaron.robobrole.components

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import fr.imacaron.robobrole.R
import fr.imacaron.robobrole.activity.MainActivity
import fr.imacaron.robobrole.types.AppState

@Composable
fun SaveFloatingButton(appState: AppState){
	val activity = (LocalContext.current as MainActivity)
	FloatingActionButton({ activity.save(appState.points)}, Modifier.offset { IntOffset(0, -250) }) {
		Icon(ImageVector.vectorResource(R.drawable.save), null)
	}
}