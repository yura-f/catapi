package ru.z13.catapi.app.models

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
data class CatData(val id: Int,
                   val labelId: String,
                   val breeds: List<BreedData>,
                   val url: String,
                   val width: Int,
                   val height: Int
)