package fr.imacaron.robobrole.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import fr.imacaron.robobrole.Theme

private val DarkColorPalette = darkColors(
    primary = PrimaryDark,
    primaryVariant = PrimaryVariantDark,
    secondary = SecondaryDark,
    secondaryVariant = SecondaryVariantDark,
    background = Color.Black,

)

private val LightColorPalette = lightColors(
    primary = PrimaryLight,
    primaryVariant = PrimaryVariantLight,
    secondary = SecondaryLight,
    secondaryVariant = SecondaryVariantLight,
    background = BgLight,
    surface = BgLight

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun RobobroleTheme(darkTheme: Theme = Theme.Default, content: @Composable () -> Unit) {
    val colors = if (darkTheme == Theme.Dark) {
        DarkColorPalette
    } else if(darkTheme == Theme.Light){
        LightColorPalette
    } else if(isSystemInDarkTheme()){
        DarkColorPalette
    }else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}