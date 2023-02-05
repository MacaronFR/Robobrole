package fr.imacaron.robobrole.types

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.imacaron.robobrole.db.AppDatabase
import fr.imacaron.robobrole.db.MatchEvent
import fr.imacaron.robobrole.db.Type
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
    var value: Int by mutableStateOf(0)

    private var inserted: MutableList<Long> = mutableListOf()

    @OptIn(DelicateCoroutinesApi::class)
    operator fun inc(): Point {
        value++
        GlobalScope.launch(Dispatchers.IO){
            inserted.add(db.matchDao().insertEvent(MatchEvent(Type.Point, team.name, name, (System.currentTimeMillis() / 1000) - team.matchStart, quart)))
            db.summaryDao().incValue(team.name, name, quart)
        }
        return this
    }

    @OptIn(DelicateCoroutinesApi::class)
    operator fun dec(): Point {
        value--
        GlobalScope.launch(Dispatchers.IO) {
            db.matchDao().deleteEvent(inserted.last())
            inserted.removeLast()
        }
        return this
    }

    fun reset(){
        inserted.clear()
        value = 0
    }

    operator fun compareTo(other: Int): Int {
        return value.compareTo(other)
    }

    operator fun plus(value: Int): Int = this.value + value
    operator fun plus(other: Point): Int = this.value + other.value

    operator fun times(value: Int): Int = this.value * value

    infix fun reloadId(value: Long){
        inserted.add(value)
    }
}

class Points(db: AppDatabase, team: Team, quart: Int){
    var one: Point = Point(db, team, "1", quart)
    var two: Point = Point(db, team, "2", quart)
    var three: Point = Point(db, team, "3", quart)
    var lucille: Point = Point(db, team, "L", quart)

    fun tot(): Int = one + two * 2 + three * 3

    fun reinit(){
        one.reset()
        two.reset()
        three.reset()
        lucille.reset()
    }

    operator fun get(index: String): Point = when(index){
        "1" -> one
        "2" -> two
        "3" -> three
        "L" -> lucille
        else -> throw IllegalArgumentException()
    }

    operator fun set(index: String, value: Int) {
        when(index){
            "1" -> one.value = value
            "2" -> two.value = value
            "3" -> three.value = value
            "L" -> lucille.value = value
        }
    }
}
