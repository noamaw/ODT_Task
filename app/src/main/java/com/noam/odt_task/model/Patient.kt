package com.noam.odt_task.model

import android.media.Image

data class Patient(val name : String, val avatar : String, val clinicianNotes : String, val images : List<Image>) {
    constructor(name: String) : this(name, "", "", emptyList<Image>())
    companion object {
        fun emptyPatient() : Patient = Patient("", "", "", emptyList())
    }
}