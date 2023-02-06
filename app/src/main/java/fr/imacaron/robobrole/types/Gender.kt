package fr.imacaron.robobrole.types

import androidx.room.TypeConverter

enum class Gender(val value: String) {
	Women("F"),
	Men("M")
}

class GenderConverter{

	@TypeConverter
	fun fromGender(gender: Gender): String = gender.value

	@TypeConverter
	fun toGender(value: String): Gender = Gender.values().find { it.value == value } ?: throw IllegalArgumentException()

}