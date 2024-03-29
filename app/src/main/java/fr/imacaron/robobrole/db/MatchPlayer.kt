package fr.imacaron.robobrole.db

import androidx.room.*

@Entity(tableName = "match_player")
data class MatchPlayer(
	@PrimaryKey(true) val id: Long,
	@ColumnInfo(name = "player") val player: Long,
	@ColumnInfo(name = "match", defaultValue = "-1") val match: Long,
	@ColumnInfo(name = "present", defaultValue = "false") val inMatch: Boolean
){
	constructor(player: Player, match: Long): this(0, player.id, match, false)
}

@Dao
interface MatchPlayerDao {

	@Insert
	fun insertAll(matchPlayers: List<MatchPlayer>)

	@Query("SELECT * FROM match_player WHERE match = :match")
	fun getByMatch(match: Long): List<MatchPlayer>

	@Query("DELETE FROM match_player")
	fun deleteAll()

	@Query("DELETE FROM match_player WHERE match = :match")
	fun deleteMatchPlayer(match: Long)

	@Query("UPDATE match_player SET present = :present WHERE player = :player AND match = :match")
	fun setPresent(present: Boolean, player: Long, match: Long): Int
}