package ru.z13.catapi.app.di

import dagger.Component
import ru.z13.catapi.app.App
import ru.z13.catapi.app.features.activities.MainActivity
import ru.z13.catapi.app.di.modules.*
import ru.z13.catapi.app.features.favorites.FavoritesFragment
import ru.z13.catapi.app.features.home.HomeFragment

import ru.z13.catapi.app.features.main.MainFragment
import javax.inject.Singleton


/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
@Singleton
@Component(modules = [
    AppModule::class,
    DomainModule::class,
    NetworkModule::class,
    DataSourceModule::class,
    NavigationModule::class,
    RepositoryModule::class])

interface AppComponent {
    fun inject(app: App)

    fun inject(activity: MainActivity)

    fun inject(fragment: MainFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: FavoritesFragment)

}