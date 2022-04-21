package ru.z13.catapi.app.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author Yura F (yura-f.github.io)
 */
@JsonClass(generateAdapter = true)
data class Breed(val id: String,
                 val name: String,
                 val origin: String,
                 @Json(name = "wikipedia_url") val wikipediaUrl: String)