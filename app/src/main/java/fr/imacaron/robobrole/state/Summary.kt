package fr.imacaron.robobrole.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Summary{
	var one: Int by mutableStateOf(0)
	var two: Int by mutableStateOf(0)
	var three: Int by mutableStateOf(0)

	fun reset(){
		one = 0
		two = 0
		three = 0
	}

	fun total(): Int = one + two * 2 + three * 3

	operator fun get(amount: Int): Int = when(amount){
			1 -> one
			2 -> two
			3 -> three
			else -> throw IllegalArgumentException()
		}

	operator fun set(amount: Int, value: Int) {
		when(amount){
			1 -> one = value
			2 -> two = value
			3 -> three = value
			else -> throw IllegalArgumentException()
		}
	}
}

fun List<Summary>.total(): Int = sumOf { it.total() }
fun List<Summary>.clean(){
	forEach(Summary::reset)
}
