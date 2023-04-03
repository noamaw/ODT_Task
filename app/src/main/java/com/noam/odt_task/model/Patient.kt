package com.noam.odt_task.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.noam.odt_task.db.type_converters.ImageConverter

@Entity
data class Patient(
    @PrimaryKey @SerializedName("name") val name : String,
    @SerializedName("avatar") val avatar : String,
    @SerializedName("clinician_notes") val clinicianNotes : String,
    @field:TypeConverters(ImageConverter::class)
    @SerializedName("images") val images : Images) {
    constructor(name: String) : this(name, "", "", Images(emptyList()))

    companion object {
        fun emptyPatient() : Patient = Patient("", "", "", Images(emptyList()))

        fun patientToJson(patient: Patient): String? {
            return Gson().toJson(patient)
        }
    }
}