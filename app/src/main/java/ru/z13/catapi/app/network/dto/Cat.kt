package ru.z13.catapi.app.network.dto

import com.squareup.moshi.JsonClass

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
@JsonClass(generateAdapter = true)
data class Cat(val id: String,
               val breeds: List<Breed>,
               val url: String,
               val width: Int,
               val height: Int)