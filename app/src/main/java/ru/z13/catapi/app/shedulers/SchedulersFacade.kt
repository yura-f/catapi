package ru.z13.catapi.app.shedulers

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Yura F (yura-f.github.io)
 */
@Singleton
class SchedulersFacade @Inject constructor() {

    fun io() : Scheduler {
        return Schedulers.io()
    }

    fun computation() : Scheduler {
        return Schedulers.computation()
    }

    fun ui() : Scheduler {
        return AndroidSchedulers.mainThread()
    }
}