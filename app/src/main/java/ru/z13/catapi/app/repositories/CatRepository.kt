package ru.z13.catapi.app.repositories

import io.reactivex.Completable
import io.reactivex.Observable
import ru.z13.catapi.app.data.Data
import ru.z13.catapi.app.data.State
import ru.z13.catapi.app.db.AppDatabase
import ru.z13.catapi.app.db.enteties.CatFavoriteEntity
import ru.z13.catapi.app.domain.DomainEvents
import ru.z13.catapi.app.mappers.toData
import ru.z13.catapi.app.mappers.toEntity
import ru.z13.catapi.app.models.CatData
import ru.z13.catapi.app.models.Page
import ru.z13.catapi.app.network.Api
import javax.inject.Inject

/**
 * @author Yura F (yura-f.github.io)
 */

interface CatRepository{
    fun loadCats(page: Page, orderBy: String = "ASC"): Observable<Data<List<CatData>>>
    fun loadFavoriteCats(page: Page, orderBy: String = "ASC"): Observable<Data<List<CatData>>>

    fun addFavorite(catId: Int): Completable
    fun removeFavorite(catId: Int): Completable
}

class CatRepositoryImpl @Inject constructor(private val db: AppDatabase,
                                            private val api: Api,
                                            private val domainEvents: DomainEvents): CatRepository {
    /**
     * LOAD CATS
     */
    override fun loadCats(page: Page, orderBy: String): Observable<Data<List<CatData>>> {
        return Observable.concatEager(mutableListOf(
            getCachedCats(page = page, orderBy = orderBy),
            getNetworkCats(page = page, orderBy = orderBy)
        )).scan { previous: Data<List<CatData>>, current: Data<List<CatData>> ->
            if(current.state is State.Error){
                current.copy(value = previous.value)
            } else {
                current
            }
        }
    }

    private fun getCachedCats(page: Page, state: State = State.Loading, orderBy: String): Observable<Data<List<CatData>>> {
        val isAsc = orderBy == "ASC"
        return db.catDao.getPage(limit = page.limit, offset = page.offset, isAsc = isAsc)
            .map { it.toData() }
            .map { Data(it, state) }
            .toObservable()
    }

    private fun getNetworkCats(page: Page, orderBy: String): Observable<Data<List<CatData>>> {
        return api.loadCats(page = page.currentNum(), limit = page.limit, order = orderBy)
            .map { list -> list.toEntity() }
            .flatMapObservable{ entities ->
                db.catDao.insertRx(entities).andThen(getCachedCats(page = page, state = State.Success, orderBy = orderBy))
            }.onErrorReturn {
                Data(listOf(), State.Error(it))
            }
    }

    /**
     * LOAD FAVORITES
     */
    override fun loadFavoriteCats(page: Page, orderBy: String): Observable<Data<List<CatData>>> {
        val isAsc = orderBy == "ASC"
        return db.catFavoriteDao.getPage(limit = page.limit, offset = page.offset, isAsc = isAsc)
            .map { it.map { it.catId } }
            .flatMap { db.catDao.getCatsByIds(it) }
            .map { it.toData() }
            .map { Data(it, State.Success) }
            .toObservable()
    }

    override fun addFavorite(catId: Int): Completable {
        val entity = CatFavoriteEntity(catId = catId)
        return db.catFavoriteDao.insertRx(entity).doOnComplete { notifyFavoritesUpdated() }
    }

    override fun removeFavorite(catId: Int): Completable {
        return db.catFavoriteDao.deleteByCatId(catId).doOnComplete { notifyFavoritesUpdated() }
    }

    private fun notifyFavoritesUpdated(){
        domainEvents.notifyAppEvent(DomainEvents.AppEvent.CAT_FAVORITES_UPDATED)
    }
}