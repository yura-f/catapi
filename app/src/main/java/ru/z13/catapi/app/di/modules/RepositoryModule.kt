package ru.z13.catapi.app.di.modules

import dagger.Binds
import dagger.Module
import ru.z13.catapi.app.repositories.CatRepository
import ru.z13.catapi.app.repositories.CatRepositoryImpl

/**
 * @author Yura F (yura-f.github.io)
 */
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindCatRepository(repository: CatRepositoryImpl): CatRepository
}