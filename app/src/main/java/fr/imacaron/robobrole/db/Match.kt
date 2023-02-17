package fr.imacaron.robobrole.db

import androidx.room.*
import fr.imacaron.robobrole.types.GenderConverter

enum class Type(val value: String){
	Point("point"),
	Fault("fault"),
	Change("change"),
	Pause("pause")
}

@Entity(tableName = "current")
data class MatchEvent(
	@PrimaryKey(true) var uid: Long,
	@ColumnInfo(name = "type") val type: Type,
	@ColumnInfo(name = "team") val team: String,
	@ColumnInfo(name = "data") val data: String,
	@ColumnInfo(name = "time") val time: Long,
	@ColumnInfo(name = "quart") val quart: Int,
){
	constructor(type: Type, team: String, data: String, time: Long, quart: Int): this(0, type, team, data, time, quart)

	companion object {
		operator fun invoke(data: String): MatchEvent{
			val d = data.split(';')
			return MatchEvent(Type.values().find { it.name == d[0] }!!, d[1], d[2], d[3].toLong(), d[4].toInt())
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
interface MatchEventDAO {
	@Query("SELECT * FROM current ORDER BY time")
	fun getAll(): List<MatchEvent>

	@Insert
	fun insertEvent(event: MatchEvent): Long

	@Query("DELETE FROM current WHERE uid = :id")
	fun deleteEvent(id: Long)

	@Delete
	fun deleteEvents(vararg events: MatchEvent)

	@Query("DELETE FROM current")
	fun wipeTable()

	@Query("SELECT * FROM current WHERE team = :team AND quart = :quart AND type = :type AND data = :data ORDER BY time DESC")
	fun getSpecificEventDesc(team: String, quart: Int, type: Type, data: String): List<MatchEvent>

	@Query("SELECT * FROM current WHERE team = :team AND quart = :quart AND type = 'change' ORDER BY time DESC")
	fun getChangeDesc(team: String, quart: Int): List<MatchEvent>

	@Query("SELECT * FROM current WHERE team = :team AND quart = :quart AND type = 'fault' ORDER BY time DESC")
	fun getFaultDesc(team: String, quart: Int): List<MatchEvent>
}

@Database(entities = [MatchEvent::class, Summary::class, Info::class], version = 6)
@TypeConverters(Converters::class, LocalDateConverters::class, GenderConverter::class)
abstract class AppDatabase: RoomDatabase(){
	abstract fun matchDao(): MatchEventDAO

	abstract fun summaryDao(): SummaryDAO

	abstract fun infoDao(): InfoDAO
}