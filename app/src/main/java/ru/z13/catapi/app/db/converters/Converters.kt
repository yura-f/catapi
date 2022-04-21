package ru.z13.catapi.app.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.z13.catapi.app.db.enteties.BreedEntity

/**
 * @author Yura F (yura-f.github.io)
 */
class Converters {
    private val gson:Gson = Gson()

    @TypeConverter
    fun toListBreed(value: String): List<BreedEntity> {
        val mapType = object : TypeToken<List<BreedEntity>>() {}.type
        return gson.fromJson(value,  mapType)
    }

    @TypeConverter
    fun fromListBrees(value: List<BreedEntity>): String {
        return gson.toJson(value)
    }
}
