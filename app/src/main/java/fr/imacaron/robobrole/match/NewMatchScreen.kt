package fr.imacaron.robobrole.match

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.imacaron.robobrole.R
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.Info
import fr.imacaron.robobrole.types.Gender
import fr.imacaron.robobrole.types.UIState
import kotlinx.coroutines.*

val defaultModifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)

class NewMatchScreenState{
	var openLevel: Boolean by mutableStateOf(false)
	var level: String by mutableStateOf("Senior")
	var levelError: Boolean by mutableStateOf(false)
	var local: String by mutableStateOf("Bois le roi")
	var localError: Boolean by mutableStateOf(false)
	var visitor: String by mutableStateOf("")
	var visitorError: Boolean by mutableStateOf(false)
	var women: Boolean by mutableStateOf(true)
	var switch: Boolean by mutableStateOf(false)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
@Composable
fun NewMatchScreen(navController: NavController, db: AppDatabase, uiState: UIState){
	val newMatchScreenState = NewMatchScreenState()
	val rotate by animateFloatAsState(if (newMatchScreenState.switch) 270f else 90f)
	val focus = LocalFocusManager.current
	val keyboard = LocalSoftwareKeyboardController.current
	uiState.home = false
	Column {
		OutlinedCard(defaultModifier) {
			Text("Information du match", defaultModifier, textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
			Column(horizontalAlignment = Alignment.CenterHorizontally){
				OutlinedTextField(
					newMatchScreenState.local,
					{ newMatchScreenState.local = it },
					defaultModifier,
					label = { Text("Locaux") },
					isError = newMatchScreenState.localError,
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
					singleLine = true)
				Icon(ImageVector.vectorResource(R.drawable.sync), null, Modifier.clip(MaterialTheme.shapes.extraLarge).rotate(rotate).clickable {
					newMatchScreenState.switch = !newMatchScreenState.switch
					val tmp = newMatchScreenState.local
					newMatchScreenState.local = newMatchScreenState.visitor
					newMatchScreenState.visitor = tmp
				}.padding(8.dp))
				OutlinedTextField(newMatchScreenState.visitor,
					{ newMatchScreenState.visitor = it },
					defaultModifier,
					label = { Text("Visiteurs") },
					isError = newMatchScreenState.visitorError,
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
					keyboardActions = KeyboardActions(onNext = {
						keyboard?.hide(); focus.clearFocus()
					}),
					singleLine = true)
				Row(defaultModifier, horizontalArrangement = Arrangement.SpaceAround) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						RadioButton(!newMatchScreenState.women, { newMatchScreenState.women = false })
						Text("Masculin")
					}
					Row(verticalAlignment = Alignment.CenterVertically) {
						RadioButton(newMatchScreenState.women, { newMatchScreenState.women = true })
						Text("Féminin")
					}
				}
				Box(defaultModifier){
					OutlinedTextField(newMatchScreenState.level, {}, Modifier.fillMaxWidth(), label = { Text("Niveau") }, trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }, isError = newMatchScreenState.levelError)
					DropdownMenu(newMatchScreenState.openLevel, { newMatchScreenState.openLevel = false }, Modifier.fillMaxWidth(0.6f)){
						DropdownMenuItem({ Text("U9")}, { newMatchScreenState.level = "U9"; newMatchScreenState.openLevel = false })
						DropdownMenuItem({ Text("U11")}, { newMatchScreenState.level = "U11"; newMatchScreenState.openLevel = false })
						DropdownMenuItem({ Text("U13")}, { newMatchScreenState.level = "U13"; newMatchScreenState.openLevel = false })
						DropdownMenuItem({ Text("U15")}, { newMatchScreenState.level = "U15"; newMatchScreenState.openLevel = false })
						DropdownMenuItem({ Text("U17")}, { newMatchScreenState.level = "U17"; newMatchScreenState.openLevel = false })
						DropdownMenuItem({ Text("U18")}, { newMatchScreenState.level = "U18"; newMatchScreenState.openLevel = false })
						DropdownMenuItem({ Text("U20")}, { newMatchScreenState.level = "U20"; newMatchScreenState.openLevel = false })
						DropdownMenuItem({ Text("Senior")}, { newMatchScreenState.level = "Senior"; newMatchScreenState.openLevel = false })
					}
					Spacer(Modifier.matchParentSize().clickable { newMatchScreenState.openLevel = true })
				}
			}
			Button(
				{
					var ok = true
					if(newMatchScreenState.local == ""){
						ok = false
						newMatchScreenState.localError = true
					}else{
						newMatchScreenState.localError = false
					}
					if(newMatchScreenState.visitor == ""){
						ok = false
						newMatchScreenState.visitorError = true
					}else{
						newMatchScreenState.visitorError = false
					}
					if(newMatchScreenState.level == ""){
						ok = false
						newMatchScreenState.levelError = true
					}else{
						newMatchScreenState.levelError = false
					}
					if(ok){
						val info = Info(newMatchScreenState.local, newMatchScreenState.visitor, newMatchScreenState.level, if(newMatchScreenState.women) Gender.Women else Gender.Men)
						GlobalScope.launch{
							val id = db.infoDao().insertInfo(info)
							withContext(Dispatchers.Main){
								navController.navigate("match/$id"){ popUpTo("home") }
							}
						}
					}
				},
				defaultModifier
			){
				Text("Créer le match")
			}
		}
	}
}