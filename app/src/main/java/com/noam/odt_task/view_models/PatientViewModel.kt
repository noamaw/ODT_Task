package com.noam.odt_task.view_models

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.noam.odt_task.R
import com.noam.odt_task.model.Patient
import com.noam.odt_task.model.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PatientViewModel @Inject constructor(private val patientRepository: PatientRepository) : ViewModel() {
    private val patientEmitter = MutableLiveData<List<Patient>>()
    val patients: LiveData<List<Patient>> = patientEmitter

    var patientChosenForExam : Patient = Patient.emptyPatient()

    init {
        loadPatient()
    }

    // getting patient list using
    // repository and passing it into live data
    private fun loadPatient() {
        patientEmitter.value = patientRepository.getPatients()
    }

    fun deletePatient(deletedPatient: Patient) {

    }

    fun exportPatient(patient: Patient) {

    }

    fun checkUnique(name: String): Boolean {
        val patients = patientEmitter.value
        val names = patients?.map { it -> it.name }
        Log.d(
            "TAG",
            "checkUnique: patients list = $patients, and all the names in the system are: $names"
        )
        val found = names?.contains(name) ?: true
        Log.d("TAG", "checkUnique: after running through the list $names, it is ${!found} that it is a unique name")
        return !found
//        return patients.value?.map { it -> it.name }?.contains(name) ?: true
    }
}

@BindingAdapter("avatar")
fun loadImage(imageView: ImageView, imageURL: String?) {
    Glide.with(imageView.context)
        .setDefaultRequestOptions(
            RequestOptions()
                .circleCrop()
        )
        .load(imageURL)
        .placeholder(R.drawable.loading)
        .into(imageView)
}