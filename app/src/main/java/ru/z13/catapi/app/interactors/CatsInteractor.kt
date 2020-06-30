package ru.z13.catapi.app.interactors

import io.reactivex.Observable
import ru.z13.catapi.app.data.Data
import ru.z13.catapi.app.features.favorites.items.FavoriteCatItem
import ru.z13.catapi.app.features.home.items.CatItem
import ru.z13.catapi.app.mappers.toCatItem
import ru.z13.catapi.app.mappers.toFavoriteCatItem
import ru.z13.catapi.app.models.CatData
import ru.z13.catapi.app.models.Page
import ru.z13.catapi.app.repositories.CatRepository
import javax.inject.Inject

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
class CatsInteractor @Inject constructor(private val wineryRepository: CatRepository) {

    fun loadCats(page: Page): Observable<Data<List<CatItem>>> {
        return wineryRepository.loadCats(page).scan { data1: Data<List<CatData>>, data2: Data<List<CatData>> ->
            val linked = linkedSetOf<CatData>()
            linked.addAll(data1.value)
            linked.addAll(data2.value)

            data2.copy(value = linked.toList())
        }.map {
            Data(it.value.toCatItem(), it.state)
        }
    }

    fun loadFavoriteCats(page: Page): Observable<Data<List<FavoriteCatItem>>> {
        return wineryRepository.loadFavoriteCats(page).scan { data1: Data<List<CatData>>, data2: Data<List<CatData>> ->
            val linked = linkedSetOf<CatData>()
            linked.addAll(data1.value)
            linked.addAll(data2.value)

            data2.copy(value = linked.toList())
        }.map {
            Data(it.value.toFavoriteCatItem(), it.state)
        }
    }

    fun addFavorite(catId: Int): Observable<Unit> {
        return wineryRepository.addFavorite(catId).andThen(Observable.just(Unit))
    }

    fun removeFavorite(catId: Int): Observable<Unit> {
        return wineryRepository.removeFavorite(catId).andThen(Observable.just(Unit))
    }
}