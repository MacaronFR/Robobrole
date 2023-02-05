package fr.imacaron.robobrole.types

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.MatchEvent
import fr.imacaron.robobrole.db.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

@Stable
class Team(db: AppDatabase){
    var name: String by mutableStateOf("")
    var matchStart: Long by mutableStateOf(0)
    val scores: List<Points> = listOf(Points(db, this, 1), Points(db, this, 2), Points(db, this, 3), Points(db, this, 4))

    fun reset(){
        name = ""
        scores.forEach(Points::reinit)
    }
}

class Point(private val db: AppDatabase, private val team: Team, private val name: String, private val quart: Int){
    private var value: Int by mutableStateOf(0)

    private var inserted: MutableList<Long> = mutableListOf()

    operator fun inc(): Point {
        value++
        println("ici")
        GlobalScope.launch(Dispatchers.IO){
            println("ICI")
            inserted.add(db.matchDao().insertEvent(MatchEvent(Type.Point, team.name, name, (System.currentTimeMillis() / 1000) - team.matchStart)))
            db.summaryDao().incValue(team.name, name, quart)
        }
        return this
    }

    operator fun dec(): Point {
        value--
        GlobalScope.launch(Dispatchers.IO) {
            db.matchDao().deleteEvent(inserted.last())
            inserted.removeLast()
        }
        return this
    }

    operator fun compareTo(other: Int): Int {
        return value.compareTo(other)
    }

    operator fun getValue(thisRef: Points, property: KProperty<*>): Int{
        return value
    }

    operator fun setValue(thisRef: Points, property: KProperty<*>, value: Int){
        println("ICI $value, ${this.value}")
        GlobalScope.launch(Dispatchers.IO){
            println("$value, ${this@Point.value}")
            if(this@Point.value - value == 1){
                db.matchDao().deleteEvent(inserted.last())
                inserted.removeLast()
            }else if(this@Point.value - value == -1){
                println("ICI")
                inserted.add(db.matchDao().insertEvent(MatchEvent(Type.Point, team.name, name, (System.currentTimeMillis() / 1000) - team.matchStart)))
                db.summaryDao().incValue(team.name, name, quart)
                println(inserted)
            }
            this@Point.value = value
        }
    }
}

class Points(db: AppDatabase, team: Team, quart: Int){
    var one: Int by Point(db, team, "1", quart)
    var two: Int by Point(db, team, "2", quart)
    var three: Int by Point(db, team, "3", quart)
    var lucille: Int by Point(db, team, "4", quart)

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
