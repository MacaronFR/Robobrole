package fr.imacaron.robobrole

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PressableButton(
    modifier: Modifier = Modifier,
    onPress: () -> Unit = {},
    onLongPress: () -> Unit = {},
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
                        .padding(6.dp, 0.dp)
                        .combinedClickable(
                            onClick = onPress,
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongPress()
                            }
                        )
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
