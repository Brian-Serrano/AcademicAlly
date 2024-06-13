package thesis.academic.ally.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import thesis.academic.ally.activity.userDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserCacheModule {

    @Provides
    @Singleton
    fun provideUserCacheDataStore(@ApplicationContext context: Context): DataStore<UserCache> {
        return context.userDataStore
    }

    @Provides
    @Singleton
    fun provideUserCacheRepository(userDataStore: DataStore<UserCache>): UserCacheRepository {
        return UserCacheRepository(userDataStore)
    }
}