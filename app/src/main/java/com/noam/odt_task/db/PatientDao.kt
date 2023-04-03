package com.noam.odt_task.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.noam.odt_task.model.Patient

// patient dao for data access object
@Dao
 interface PatientDao {
     @Query("SELECT * FROM patient")
     fun getAll(): List<Patient>

     @Query("SELECT * FROM patient WHERE name LIKE :name")
     fun findByName(name: String): Patient

     @Insert
     fun insertAll(vararg patients: Patient)

     @Delete
     fun delete(patient: Patient)
 }