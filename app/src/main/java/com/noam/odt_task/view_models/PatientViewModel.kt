package com.noam.odt_task.view_models

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.net.toFile
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.noam.odt_task.R
import com.noam.odt_task.model.Images
import com.noam.odt_task.model.Patient
import com.noam.odt_task.model.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PatientViewModel @Inject constructor(private val patientRepository: PatientRepository) : ViewModel() {
    private val patientEmitter = MutableLiveData<List<Patient>>()
    val patients: LiveData<List<Patient>> = patientEmitter

    var patientChosenForExam : Patient = Patient.emptyPatient()
        set(value) {
            value
            field = value
        }
        get() {
            return Patient(field.name, field.avatar, field.clinicianNotes, Images(patientExamImages))
        }
    var patientExamImages = mutableListOf<String>()

    var filteredImages = MutableLiveData<List<Uri>>()

    init {
        loadPatient()
    }

    // getting patient list using
    // repository and passing it into live data
    private fun loadPatient() {
        viewModelScope.launch {
            patientEmitter.postValue(patientRepository.getPatients())
        }
    }

    fun deletePatient(deletedPatient: Patient) {
        viewModelScope.launch {
            patientRepository.deletePatient(deletedPatient)
        }
    }

    fun exportPatient(patient: Patient) {
        viewModelScope.launch {
            patientRepository.exportPatient(patient)
        }
    }

    fun saveNewPatient(name: String, clinicianNotes: String, avatar: String) {
        Log.d(
            "TAG",
            "saving the patient by the name of $name, with the clinician notes: $clinicianNotes, and with the avatar $avatar"
        )
        viewModelScope.launch {
            patientRepository.savePatient(
                name, clinicianNotes, Images(patientExamImages))
            loadPatient()
        }
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

    fun getFilteredImages(bitmap: Bitmap) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                filteredImages.postValue(patientRepository.filterImages(bitmap))
            }
        }
    }

    fun addUriToImages(chosenUri : Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                patientExamImages.add(patientRepository.saveChosenImage(chosenUri).toString())
            }
        }
        filteredImages = MutableLiveData<List<Uri>>()
    }

    fun deleteFilteredImages() {
        viewModelScope.launch {
            patientRepository.deleteFilterImages()
        }
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