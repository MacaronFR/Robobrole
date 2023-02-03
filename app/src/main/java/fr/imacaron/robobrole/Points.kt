package fr.imacaron.robobrole

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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
