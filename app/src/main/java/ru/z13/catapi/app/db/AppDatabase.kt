package ru.z13.catapi.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.z13.catapi.app.db.converters.Converters
import ru.z13.catapi.app.db.dao.CatDao
import ru.z13.catapi.app.db.dao.CatFavoriteDao
import ru.z13.catapi.app.db.enteties.BreedEntity
import ru.z13.catapi.app.db.enteties.CatEntity
import ru.z13.catapi.app.db.enteties.CatFavoriteEntity


/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
@Database(
    entities = [CatEntity::class,
        CatFavoriteEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val catDao: CatDao
    abstract val catFavoriteDao: CatFavoriteDao
}