package ru.z13.catapi.app.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.z13.catapi.app.network.dto.Cat

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
interface Api {
    @GET("images/search")
    fun loadCats(@Query("page") page: Int,
                 @Query("limit") limit: Int = 25,
                 @Query("mime_types") mimeTypes: String = "jpg",
                 @Query("size") size: String = "small",
                 @Query("order") order: String = "ASC"): Single<List<Cat>>
}