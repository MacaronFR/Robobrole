package fr.imacaron.robobrole.db

import androidx.room.*

@Entity(tableName = "info")
data class Info(
	@PrimaryKey(true) val uid: Long,
	@ColumnInfo(name = "local") val local: String,
	@ColumnInfo(name = "visitor") val visitor: String,
	@ColumnInfo(name = "level") val level: String,
	@ColumnInfo(name = "gender") val gender: String,
	@ColumnInfo(name = "match_start") val matchStart: Long,
	@ColumnInfo(name = "done") val done: Boolean
){
	constructor(local: String, visitor: String, level: String, gender: String): this(0, local, visitor, level, gender, 0, false)
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
}