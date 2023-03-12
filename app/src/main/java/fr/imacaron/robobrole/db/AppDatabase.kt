package fr.imacaron.robobrole.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.imacaron.robobrole.types.GenderConverter

@Database(entities = [Event::class, Match::class, Player::class, MatchPlayer::class], version = 12)
@TypeConverters(Converters::class, LocalDateConverters::class, GenderConverter::class)
abstract class AppDatabase: RoomDatabase(){
	abstract fun eventDAO(): EventDAO

	abstract fun matchDao(): MatchDAO

	abstract fun playerDao(): PlayerDao

	abstract fun matchPlayerDao(): MatchPlayerDao
}