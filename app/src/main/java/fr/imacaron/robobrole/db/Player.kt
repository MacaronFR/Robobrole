package fr.imacaron.robobrole.db

import androidx.room.*

@Entity(tableName = "player")
data class Player(
	@PrimaryKey(true) var id: Long,
	@ColumnInfo(name = "name") val name: String
){
	constructor(name: String): this(0, name)
}

@Dao
interface PlayerDao {
	@Insert
	fun insertPlayer(player: Player): Long

	@Update
	fun updatePlayer(player: Player): Int

	@Query("SELECT * FROM player")
	fun getAll(): List<Player>

	@Query("SELECT * FROM player WHERE id = :id")
	fun get(id: Long): Player?

	@Delete
	fun delete(player: Player)

	@Query("DELETE FROM player")
	fun deleteAll()
}