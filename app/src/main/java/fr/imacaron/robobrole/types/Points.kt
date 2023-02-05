package fr.imacaron.robobrole.types

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.imacaron.robobrole.db.AppDatabase
import kotlin.reflect.KProperty

@Stable
class Team(db: AppDatabase){
    var name: String by mutableStateOf("")
    val scores: List<Points> = listOf(Points(db, this), Points(db, this), Points(db, this), Points(db, this))

    fun reset(){
        name = ""
        scores.forEach(Points::reinit)
    }
}

class Point(private val db: AppDatabase, private val team: Team){
    private var value: Int by mutableStateOf(0)

    operator fun inc(): Point {
        value++
        return this
    }

    operator fun dec(): Point {
        value--
        return this
    }

    operator fun compareTo(other: Int): Int {
        return value.compareTo(other)
    }

    operator fun getValue(thisRef: Points, property: KProperty<*>): Int{
        return value
    }

    operator fun setValue(thisRef: Points, property: KProperty<*>, value: Int){
        this.value = value
    }
}

class Points(db: AppDatabase, team: Team){
    var one: Int by Point(db, team)
    var two: Int by Point(db, team)
    var three: Int by Point(db, team)
    var lucille: Int by Point(db, team)

    fun tot(): Int = one + two * 2 + three * 3

    fun reinit(){
        one = 0
        two = 0
        three = 0
        lucille = 0
    }

    operator fun get(index: String): Int = when(index){
        "1" -> one
        "2" -> two
        "3" -> three
        "L" -> lucille
        else -> -1
    }

    operator fun set(index: String, value: Int) {
        when(index){
            "1" -> one = value
            "2" -> two = value
            "3" -> three = value
            "L" -> lucille = value
        }
    }
}
