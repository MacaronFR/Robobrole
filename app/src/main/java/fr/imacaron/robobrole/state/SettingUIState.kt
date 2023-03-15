package fr.imacaron.robobrole.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SettingUIState {
	var themeDialog: Boolean by mutableStateOf(false)

	var deleteHistoryDialog: Boolean by mutableStateOf(false)

	var deleteDataDialog: Boolean by mutableStateOf(false)
}