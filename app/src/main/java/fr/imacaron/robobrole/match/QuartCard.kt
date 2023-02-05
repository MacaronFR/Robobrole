package fr.imacaron.robobrole.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.imacaron.robobrole.components.ButtonLong
import fr.imacaron.robobrole.types.Team

@Composable
fun TeamText(name: String){
	Text(name, Modifier.fillMaxWidth().padding(0.dp, 10.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
}

@Composable
fun LabelText(text: String){
	Text(text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PointButton(onClick: () -> Unit, onLongClick: () -> Unit, badgeText: String, content: @Composable RowScope.() -> Unit){
	BadgedBox( { Badge(Modifier.offset((-8).dp, 7.dp)) { Text(badgeText, style = MaterialTheme.typography.titleSmall) } } ){
		ButtonLong(onClick, onLongClick, shape = MaterialTheme.shapes.large, content = content)
	}
}

@Composable
fun QuartCard(modifier: Modifier, team: Team, index: Int){
	val conf = LocalConfiguration.current
	Card(modifier.requiredWidth(conf.screenWidthDp.dp).padding(10.dp, 7.dp)) {
		Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp)) {
			PointButton({ team.scores[index].one++ }, { if (team.scores[index].one > 0) team.scores[index].one-- }, "1") { LabelText("${team.scores[index].one}") }
			PointButton({ team.scores[index].two++ }, { if(team.scores[index].two > 0) team.scores[index].two-- }, "2") { LabelText("${team.scores[index].two}") }
			PointButton({ team.scores[index].three++ }, { if(team.scores[index].three > 0) team.scores[index].three-- }, "3") { LabelText("${team.scores[index].three}") }
			PointButton({ team.scores[index].lucille++ }, { if(team.scores[index].lucille > 0) team.scores[index].lucille-- }, "L") { LabelText("${team.scores[index].lucille}") }
		}
		TeamText(team.name)
	}
}