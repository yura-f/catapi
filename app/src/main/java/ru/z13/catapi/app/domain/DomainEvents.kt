package ru.z13.catapi.app.domain

import io.reactivex.Observable


/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
interface DomainEvents {
    fun subscribeOnAppEvent(): Observable<AppEvent>
    fun notifyAppEvent(appEvent: AppEvent)

    enum class AppEvent {
        CAT_FAVORITES_UPDATED
    }
}