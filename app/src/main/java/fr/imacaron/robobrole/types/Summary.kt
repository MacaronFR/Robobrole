package fr.imacaron.robobrole.types

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Summary{
	var one: Int by mutableStateOf(0)
	var two: Int by mutableStateOf(0)
	var three: Int by mutableStateOf(0)
	var player: Int by mutableStateOf(0)

	fun reset(){
		one = 0
		two = 0
		three = 0
		player = 0
	}

	fun total(): Int = one + two * 2 + three * 3
}

fun List<Summary>.total(): Int = sumOf { it.total() }
fun List<Summary>.clean(){
	forEach(Summary::reset)
}
