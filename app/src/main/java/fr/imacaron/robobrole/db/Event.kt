package fr.imacaron.robobrole.db

import androidx.room.*

enum class Type(val value: String){
	Point("point"),
	Fault("fault"),
	Change("change"),
	Start("start"),
	End("end")
}

@Entity(tableName = "event")
data class Event(
	@PrimaryKey(true) var uid: Long,
	@ColumnInfo(name = "type") val type: Type,
	@ColumnInfo(name = "team") val team: String,
	@ColumnInfo(name = "player") val player: String,
	@ColumnInfo(name = "data") val data: String,
	@ColumnInfo(name = "time") val time: Long,
	@ColumnInfo(name = "quart") val quart: Int,
	@ColumnInfo(name = "match") val match: Long
){
	constructor(type: Type, team: String, player: String, data: String, time: Long, quart: Int, match: Long): this(0, type, team, player, data, time, quart, match)

	companion object {
		operator fun invoke(data: String): Event{
			val d = data.split(';')
			return Event(Type.values().find { it.name == d[0] }!!, d[1], "", d[2], d[3].toLong(), d[4].toInt(), 0)
		}
	}
}

class Converters {
	@TypeConverter
	fun fromTypeEnum(type: Type): String = type.value

	@TypeConverter
	fun toTypeEnum(value: String): Type = Type.values().find { it.value == value }!!
}

@Dao
interface EventDAO {
	@Query("SELECT * FROM event ORDER BY time")
	fun getAll(): List<Event>

	@Query("SELECT * FROM event WHERE match = :match ORDER BY time")
	fun getByMatch(match: Long): List<Event>

	@Insert
	fun insertEvent(event: Event): Long

	@Query("DELETE FROM event WHERE uid = :id")
	fun deleteEvent(id: Long)

	@Query("DELETE FROM event")
	fun wipeTable()

	@Query("SELECT * FROM event WHERE team = :team AND quart = :quart AND type = :type AND data = :data ORDER BY time DESC")
	fun getSpecificEventDesc(team: String, quart: Int, type: Type, data: String): List<Event>

	@Query("SELECT * FROM event WHERE team = :team AND quart = :quart AND type = 'change' ORDER BY time DESC")
	fun getChangeDesc(team: String, quart: Int): List<Event>

	@Query("SELECT * FROM event WHERE team = :team AND quart = :quart AND type = 'fault' ORDER BY time DESC")
	fun getFaultDesc(team: String, quart: Int): List<Event>
}