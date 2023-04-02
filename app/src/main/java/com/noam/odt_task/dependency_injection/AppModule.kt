package com.noam.odt_task.dependency_injection

import com.noam.odt_task.model.PatientRepository
import com.noam.odt_task.model.PatientRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun providePatientRepository(): PatientRepository = PatientRepositoryImpl()
}