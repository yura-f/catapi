package ru.z13.catapi.app.data

/**
 * @author Yura F (yura-f.github.io)
 */
data class Data<T>(
    val value: T,
    val state: State
)

sealed class State {
    object Loading: State()
    object Success: State()
    data class Error(val error: Throwable): State()
}
