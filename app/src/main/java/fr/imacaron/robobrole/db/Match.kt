package fr.imacaron.robobrole.db

import androidx.room.*

enum class Type(val value: String){
	Point("point"),
	Fault("fault"),
	Change("change"),
	Pause("pause")
}

@Entity(tableName = "current")
data class MatchEvent(
	@PrimaryKey(true) val uid: Int,
	@ColumnInfo(name = "type") val type: Type,
	@ColumnInfo(name = "team") val team: String,
	@ColumnInfo(name = "data") val data: String,
	@ColumnInfo(name = "time") val time: Long,
){
	constructor(type: Type, team: String, data: String, time: Long): this(0, type, team, data, time)
}

class Converters {
	@TypeConverter
	fun fromTypeEnum(type: Type): String = type.value

	@TypeConverter
	fun toTypeEnum(value: String): Type = Type.values().find { it.value == value }!!
}

@Dao
interface MatchEventDAO {
	@Query("SELECT * FROM current")
	fun getAll(): List<MatchEvent>

	@Insert
	fun insertEvents(vararg events: MatchEvent)

	@Delete
	fun deleteEvents(vararg events: MatchEvent)

	@Query("DELETE FROM current")
	fun wipeTable()
}

@Database(entities = [MatchEvent::class, Summary::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase(){
	abstract fun matchDao(): MatchEventDAO

	abstract fun summaryDao(): SummaryDAO
}