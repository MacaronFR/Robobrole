package fr.imacaron.robobrole.types

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class Team{
    var name: String by mutableStateOf("")
    val scores: List<Points> = listOf(Points(), Points(), Points(), Points())

    fun reset(){
        name = ""
        scores.forEach(Points::reinit)
    }
}

class Points{
    var one: Int by mutableStateOf(0)
    var two: Int by mutableStateOf(0)
    var three: Int by mutableStateOf(0)
    var lucille: Int by mutableStateOf(0)

    fun tot(): Int = one + two * 2 + three * 3

    fun reinit(){
        one = 0
        two = 0
        three = 0
        lucille = 0
    }
}
