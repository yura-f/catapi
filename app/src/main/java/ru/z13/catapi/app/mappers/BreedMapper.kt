package ru.z13.catapi.app.mappers

import ru.z13.catapi.app.db.enteties.BreedEntity
import ru.z13.catapi.app.models.BreedData
import ru.z13.catapi.app.network.dto.Breed

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */

fun List<Breed>.toEntity() = map { it.toEntity() }
fun Breed.toEntity() = BreedEntity(labelId = id, name = name, origin = origin, wikipediaUrl = wikipediaUrl)

fun List<BreedEntity>.toData() = map { it.toData() }
fun BreedEntity.toData() = BreedData(id, labelId, name, origin, wikipediaUrl)