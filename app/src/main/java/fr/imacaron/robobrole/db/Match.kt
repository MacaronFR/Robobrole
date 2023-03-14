package fr.imacaron.robobrole.db

import androidx.room.*
import fr.imacaron.robobrole.types.Gender
import java.time.LocalDate
import java.util.*

@Entity(tableName = "match")
data class Match(
	@PrimaryKey(true) val uid: Long,
	@ColumnInfo(name = "local") val myTeam: String,
	@ColumnInfo(name = "visitor") val otherTeam: String,
	@ColumnInfo(name = "level") val level: String,
	@ColumnInfo(name = "gender") val gender: Gender,
	@ColumnInfo(name = "match_start") val matchStart: Long,
	@ColumnInfo(name = "done") val done: Boolean,
	@ColumnInfo(name = "data") val date: LocalDate
){
	constructor(local: String, visitor: String, level: String, gender: Gender): this(0, local, visitor, level, gender, 0, false, LocalDate.now())
}

class LocalDateConverters {

	@TypeConverter
	fun fromLocalDate(date: LocalDate): String = "${date.dayOfMonth}/${date.monthValue}/${date.year}"

	@TypeConverter
	fun toLocalDate(date: String): LocalDate {
		val data = date.split('/').map { it.toInt() }
		return LocalDate.of(data[2], data[1], data[0])
	}
}

@Dao
interface MatchDAO {

	@Insert
	fun insertMatch(match: Match): Long

	@Query("UPDATE match SET done = true WHERE uid = :id")
	fun setDone(id: Long)

	@Query("UPDATE match SET match_start = :start WHERE uid = :id")
	fun setStart(start: Long, id: Long)

	@Query("DELETE FROM match WHERE done = false")
	fun deleteCurrent()

	@Query("SELECT * FROM match WHERE done = false")
	fun getCurrent(): Match?

	@Query("SELECT * FROM match WHERE done = true ORDER by uid DESC")
	fun getSaved(): List<Match>

	@Query("DELETE FROM match")
	fun deleteAll()

	@Query("SELECT * FROM match WHERE uid = :id")
	fun get(id: Long): Match

	@Query("DELETE FROM match WHERE uid = :id")
	fun delete(id: Long)
}