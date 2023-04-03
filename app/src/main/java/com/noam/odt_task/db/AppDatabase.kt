package com.noam.odt_task.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noam.odt_task.db.type_converters.ImageBitmapString
import com.noam.odt_task.model.Patient

// room database with a typeconverter set, and a dao object for patient object.
@Database(entities = [Patient::class], version = 1)
@TypeConverters(*[ImageBitmapString::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
}