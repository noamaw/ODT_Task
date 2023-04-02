package com.noam.odt_task.model

interface PatientRepository {
    fun getPatients() : List<Patient>
}