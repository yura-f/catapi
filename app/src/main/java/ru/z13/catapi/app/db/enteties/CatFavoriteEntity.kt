package ru.z13.catapi.app.db.enteties

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author Yura F (yura-f.github.io)
 */
@Entity(tableName = "cat_favorites")
data class CatFavoriteEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                             @ColumnInfo(name = "cat_id") val catId: Int
)
