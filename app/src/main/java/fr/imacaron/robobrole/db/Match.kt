package fr.imacaron.robobrole.db

import androidx.room.*

@Entity
data class Match(
	@PrimaryKey val uid: Int,
	@ColumnInfo(name = "type") val type: String,
	@ColumnInfo(name = "sata") val data: String,
	@ColumnInfo(name = "time") val time: Int
)

@Dao
interface MatchDAO {
	@Query("SELECT * FROM match")
	fun getAll(): List<Match>
}

@Database(entities = [Match::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
	abstract fun matchDao(): MatchDAO
}