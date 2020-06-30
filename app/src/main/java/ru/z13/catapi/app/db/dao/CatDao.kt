package ru.z13.catapi.app.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.z13.catapi.app.db.enteties.CatEntity

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
@Dao
interface CatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRx(items: List<CatEntity>): Completable

    @Query("SELECT * FROM cats ORDER BY CASE WHEN :isAsc = 1 THEN id END ASC, CASE WHEN :isAsc = 0 THEN id END DESC LIMIT :limit OFFSET :offset")
    fun getPage(limit: Int, offset: Int, isAsc: Boolean = false): Single<List<CatEntity>>

    @Query("SELECT * FROM cats WHERE id IN (:ids)")
    fun getCatsByIds(ids: List<Int>): Single<List<CatEntity>>
}