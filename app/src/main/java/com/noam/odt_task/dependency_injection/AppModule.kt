package com.noam.odt_task.dependency_injection

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.noam.odt_task.data_layer.AvatarRemoteDataSource
import com.noam.odt_task.data_layer.AvatarService
import com.noam.odt_task.db.AppDatabase
import com.noam.odt_task.model.PatientRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

// module for providing complex objects for the dependency injection
@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    // provide the single object of PatientRepository
    @Provides
    @Singleton
    fun providePatientRepository(@ApplicationContext applicationContext: Context, provideDatabase: AppDatabase, provideRemoteDataSource : AvatarRemoteDataSource): PatientRepository
    = PatientRepository(provideDatabase, applicationContext, provideRemoteDataSource)

    @Provides
    fun provideDatabase(
        @ApplicationContext applicationContext: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "patients_database"
        )
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson) : Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl("https://xsgames.co/randomusers/")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    fun provideAvatarService(retrofit: Retrofit): AvatarService = retrofit.create(AvatarService::class.java)

    @Singleton
    @Provides
    fun provideAvatarRemoteDataSource(avatarService: AvatarService) = AvatarRemoteDataSource(avatarService)

}