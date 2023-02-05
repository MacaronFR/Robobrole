package fr.imacaron.robobrole.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import fr.imacaron.robobrole.db.AppDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun  HomeScreen(navController: NavController, db: AppDatabase){
	Column {
		Text("Roborole")
		Button({
			GlobalScope.launch(Dispatchers.IO){
				db.matchDao().wipeTable()
				db.summaryDao().wipeTable()
			}
			navController.navigate("new_match")
		}) {
			Text("Nouveau match")
		}
	}
}