package fr.imacaron.robobrole.db

import androidx.room.*

@Entity(tableName = "summary")
data class Summary(
	@PrimaryKey(true) val uid: Int,
	@ColumnInfo(name = "team") val team: String,
	@ColumnInfo(name = "key") val key: String,
	@ColumnInfo(name = "quart") val quart: Int,
	@ColumnInfo(name = "value") val value: Int
){
	constructor(team: String, key: String, quart: Int, value: Int): this(0, team, key, quart, value)
}

@Dao
interface SummaryDAO {

	@Query("SELECT * FROM summary")
	fun getAll(): List<Summary>

	@Insert
	fun insertSummary(vararg summary: Summary)

	@Insert
	fun insertSummary(summary: List<Summary>)

	@Update
	fun updateSummary(summary: Summary)

	@Query("DELETE FROM summary")
	fun wipeTable()

}