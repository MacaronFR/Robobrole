package fr.imacaron.robobrole.db

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import fr.imacaron.robobrole.types.GenderConverter

@Database(
	entities = [Event::class, Match::class, Player::class, MatchPlayer::class],
	version = 14,
	autoMigrations = [
		AutoMigration(from = 10, to = 12),
		AutoMigration(from = 12, to = 13),
		AutoMigration(from = 13, to = 14, spec = NumeroDeleteMigration::class)
	]
)
@TypeConverters(Converters::class, LocalDateConverters::class, GenderConverter::class)
abstract class AppDatabase: RoomDatabase(){
	abstract fun eventDAO(): EventDAO

	abstract fun matchDao(): MatchDAO

	abstract fun playerDao(): PlayerDao

	abstract fun matchPlayerDao(): MatchPlayerDao
}

@DeleteColumn("player", "numero")
class NumeroDeleteMigration: AutoMigrationSpec