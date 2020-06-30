package ru.z13.catapi.app.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.z13.catapi.app.db.AppDatabase
import javax.inject.Singleton

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
@Module
class DataSourceModule {
    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "catDB").build()
    }
}