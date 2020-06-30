package ru.z13.catapi.app.mappers

import ru.z13.catapi.app.db.enteties.CatEntity
import ru.z13.catapi.app.features.favorites.items.FavoriteCatItem
import ru.z13.catapi.app.features.home.items.CatItem
import ru.z13.catapi.app.models.CatData
import ru.z13.catapi.app.network.dto.Cat

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */

fun List<Cat>.toEntity() = map { it.toEntity() }
fun Cat.toEntity() = CatEntity(labelId = id, breeds = breeds.toEntity(), url = url, width = width, height = height)

fun List<CatEntity>.toData() = map { it.toData() }
fun CatEntity.toData() = CatData(id, labelId, breeds.toData(), url, width, height)

fun List<CatData>.toCatItem(): List<CatItem> = map { data ->
    val item = CatItem()
    item.withData(data)
}

fun List<CatData>.toFavoriteCatItem(): List<FavoriteCatItem> = map { data ->
    val item = FavoriteCatItem()
    item.withData(data)
}

