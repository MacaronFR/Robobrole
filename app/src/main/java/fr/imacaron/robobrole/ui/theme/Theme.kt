package fr.imacaron.robobrole.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import fr.imacaron.robobrole.types.Theme

@Composable
fun RobobroleTheme(darkTheme: Theme = Theme.Default, content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    val colors = if (darkTheme == Theme.Dark) {
        dynamicDarkColorScheme(LocalContext.current)
    } else if(darkTheme == Theme.Light){
        dynamicLightColorScheme(LocalContext.current)
    } else if(isSystemInDarkTheme()){
        dynamicDarkColorScheme(LocalContext.current)
    }else {
        dynamicLightColorScheme(LocalContext.current)
    }
    systemUiController.setSystemBarsColor(colors.background)
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}