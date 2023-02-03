package fr.imacaron.robobrole

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@Composable
fun PressableButton(
    modifier: Modifier = Modifier,
    onPress: (Offset) -> Unit = {},
    onLongPress: (Offset) -> Unit = {},
    content: @Composable RowScope.() -> Unit
){
    val contentColor by ButtonDefaults.buttonColors().contentColor(true)
    val haptic = LocalHapticFeedback.current
    Surface(
        contentColor = contentColor.copy(alpha = 1f)
    ) {
        CompositionLocalProvider(LocalContentAlpha provides contentColor.alpha) {
            ProvideTextStyle(MaterialTheme.typography.button) {
                Row(
                    modifier
                        .pointerInput(onPress, onLongPress) {
                            detectTapGestures(
                                onTap = onPress,
                                onLongPress = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onLongPress(it)
                                }
                            )
                        }
                        .padding(6.dp, 0.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colors.primary)
                        .padding(0.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    content = content
                )
            }
        }
    }
}
