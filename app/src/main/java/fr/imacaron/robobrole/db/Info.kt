package fr.imacaron.robobrole.db

import androidx.room.*
import fr.imacaron.robobrole.types.Gender
import java.time.LocalDate
import java.util.*

@Entity(tableName = "info")
data class Info(
	@PrimaryKey(true) val uid: Long,
	@ColumnInfo(name = "local") val local: String,
	@ColumnInfo(name = "visitor") val visitor: String,
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
interface InfoDAO {

	@Insert
	fun insertInfo(info: Info): Long

	@Query("UPDATE info SET done = true WHERE uid = :id")
	fun setDone(id: Long)

	@Query("UPDATE info SET match_start = :start WHERE uid = :id")
	fun setStart(start: Long, id: Long)

	@Query("DELETE FROM info WHERE done = false")
	fun deleteCurrent()

	@Query("SELECT * FROM info WHERE done = false")
	fun getCurrent(): Info?

	@Query("SELECT * FROM info WHERE done = true ORDER by uid DESC")
	fun getSaved(): List<Info>

	@Query("DELETE FROM info")
	fun removeAll()

	@Query("SELECT * FROM info WHERE uid = :id")
	fun get(id: Long): Info
}