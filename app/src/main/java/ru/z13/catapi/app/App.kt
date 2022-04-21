package ru.z13.catapi.app

import android.app.Application
import ru.z13.catapi.app.di.AppComponent
import ru.z13.catapi.app.di.DaggerAppComponent
import ru.z13.catapi.app.di.modules.AppModule
import ru.z13.catapi.app.di.modules.DataSourceModule
import ru.z13.catapi.app.di.modules.NetworkModule


/**
 * @author Yura F (yura-f.github.io)
 */
class App: Application() {


    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .dataSourceModule(DataSourceModule())
            .networkModule(NetworkModule())
            .build()

        appComponent.inject(this)
    }
}