package fr.imacaron.robobrole.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun  HomeScreen(navController: NavController){
	Column {
		Text("Roborole")
		Button({ navController.navigate("new_match") }) {
			Text("Nouveau match")
		}
	}
}