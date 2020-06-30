package ru.z13.catapi.app.db.enteties

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
@Entity(tableName = "cats")
data class CatEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                     @ColumnInfo(name = "label_id") val labelId: String,
                     val breeds: List<BreedEntity> = listOf(),
                     val url: String,
                     val width: Int,
                     val height: Int
)