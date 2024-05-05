package thesis.academic.ally.api

import android.content.Context
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(OptionalCurrentUser::class.java, OptionalCurrentUserDeserializer())
            .registerTypeAdapter(NoCurrentUser::class.java, NoCurrentUserDeserializer())
            .registerTypeAdapter(WithCurrentUser::class.java, WithCurrentUserDeserializer())
            .registerTypeAdapter(MessageResponse::class.java, MessageResponseDeserializer())
            .registerTypeAdapter(AuthenticationResponse::class.java, AuthenticationResponseDeserializer())
            .create()

        return Retrofit.Builder()
            .baseUrl("https://BrianSerrano.pythonanywhere.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): AcademicallyApi {
        return retrofit.create(AcademicallyApi::class.java)
    }
}