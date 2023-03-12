package fr.imacaron.robobrole.db

import androidx.room.*

@Entity(tableName = "match_player")
data class MatchPlayer(
	@PrimaryKey(true) val id: Long,
	@ColumnInfo(name = "player") val player: Long,
	@ColumnInfo(name = "present") val InMatch: Boolean
){
	constructor(player: Player): this(0, player.id, false)
}

@Dao
interface MatchPlayerDao {
	@Insert
	fun insertPlayer(matchPlayer: MatchPlayer)

	@Insert
	fun insertAll(matchPlayers: List<MatchPlayer>)

	@Delete
	fun delete(matchPlayer: MatchPlayer)

	@Query("SELECT * FROM match_player")
	fun getAll(): List<MatchPlayer>

	@Query("DELETE FROM match_player")
	fun deleteAll()

	@Query("UPDATE match_player SET present = :present WHERE player = :player")
	fun setPresent(present: Boolean, player: Long): Int
}