package ru.z13.catapi.app.di.modules

import dagger.Module
import dagger.Provides
import ru.z13.catapi.app.domain.AppDomainEvents
import ru.z13.catapi.app.domain.DomainEvents
import javax.inject.Singleton

/**
 * @author Yura F (yura-f.github.io)
 */
@Module
class DomainModule {
    private val domainEvents: AppDomainEvents = AppDomainEvents()

    @Provides
    @Singleton
    fun provideDomainEvents(): DomainEvents {
        return domainEvents
    }
}