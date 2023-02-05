package fr.imacaron.robobrole.match

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.imacaron.robobrole.types.AppState

val defaultModifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NewMatchScreen(navController: NavController, state: AppState){
	var openLevel by remember { mutableStateOf(false) }
	var level by remember { mutableStateOf("") }
	var levelError by remember { mutableStateOf(false) }
	var local by remember { mutableStateOf("") }
	var localError by remember { mutableStateOf(false) }
	var visitor by remember { mutableStateOf("") }
	var visitorError by remember { mutableStateOf(false) }
	var women by remember { mutableStateOf(true) }
	val focus = LocalFocusManager.current
	val keyboard = LocalSoftwareKeyboardController.current
	Column {
		Card(defaultModifier) {
			Text("Information du match", defaultModifier, textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
			Column{
				OutlinedTextField(
					local,
					{ local = it },
					defaultModifier,
					label = { Text("Locaux") },
					isError = localError,
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
					singleLine = true)
				Text("VS", defaultModifier, textAlign = TextAlign.Center)
				OutlinedTextField(visitor,
					{ visitor = it },
					defaultModifier,
					label = { Text("Visiteurs") },
					isError = visitorError,
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
					keyboardActions = KeyboardActions(onNext = {
						keyboard?.hide(); focus.clearFocus()
					}),
					singleLine = true)
				Row(defaultModifier, horizontalArrangement = Arrangement.SpaceAround) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						RadioButton(!women, { women = false })
						Text("Homme")
					}
					Row(verticalAlignment = Alignment.CenterVertically) {
						RadioButton(women, { women = true })
						Text("Femme")
					}
				}
				Box(defaultModifier){
					OutlinedTextField(level, {}, Modifier.fillMaxWidth(), label = { Text("Niveau") }, trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }, isError = levelError)
					DropdownMenu(openLevel, { openLevel = false }, Modifier.fillMaxWidth(0.6f)){
						DropdownMenuItem({ Text("U17")}, { level = "U17"; openLevel = false })
						DropdownMenuItem({ Text("U18")}, { level = "U18"; openLevel = false })
						DropdownMenuItem({ Text("U20")}, { level = "U20"; openLevel = false })
						DropdownMenuItem({ Text("Senior")}, { level = "Senior"; openLevel = false })
					}
					Spacer(Modifier.matchParentSize().clickable { openLevel = true })
				}
			}
			Button(
				{
					var ok = true
					if(local == ""){
						ok = false
						localError = true
					}else{
						localError = false
					}
					if(visitor == ""){
						ok = false
						visitorError = true
					}else{
						visitorError = false
					}
					if(level == ""){
						ok = false
						levelError = true
					}else{
						levelError = false
					}
					if(ok){
						state.local.reset()
						state.visitor.reset()
						state.local.name = local
						state.visitor.name = visitor
						navController.navigate("match"){ popUpTo("home") }
					}
				},
				defaultModifier
			){
				Text("Créer le match")
			}
		}
	}
}