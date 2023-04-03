package com.noam.odt_task.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.noam.odt_task.data_layer.AvatarRemoteDataSource
import com.noam.odt_task.db.AppDatabase
import com.noam.odt_task.db.type_converters.ImageBitmapString
import com.noam.odt_task.util.getGrayScaleBitmap
import com.noam.odt_task.util.getGreenScaleBitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.math.log

class PatientRepository @Inject constructor(private val database: AppDatabase, private val applicationContext: Context, private val remoteDataSource: AvatarRemoteDataSource, private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default) {

    // getting the list of patients from the database
    suspend fun getPatients(): List<Patient> = withContext(defaultDispatcher) { database.patientDao().getAll() }

        //delete a patient from the database
    suspend fun deletePatient(patient: Patient) = withContext(defaultDispatcher) { database.patientDao().delete(patient) }

    // export patient into folder with his images and the Json object
    suspend fun exportPatient(patient: Patient) {
        withContext(defaultDispatcher) {
            val folder: File? = applicationContext.getExternalFilesDir(patient.name)
            var counter = 0
            for (img in patient.images.images) {
                try {
                    ImageBitmapString.stringToBitmap(img)?.let {
                        if (folder != null) {
                            savePngFile(patient.name + "img" + (counter++).toString(), folder,
                                it
                            )
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val file = File(folder, patient.name + "_json_object.JSON")
            Patient.patientToJson(patient)?.let { file.writeText(it) }
        }
    }

    // save patient into oom database first generate random avatar for him
    suspend fun savePatient(name: String, clinicianNotes: String, images: Images) {
        withContext(defaultDispatcher) {
            val avatar = generateAvatar()
            database.patientDao().insertAll(Patient(name, generateAvatar(), clinicianNotes, images))
        }
    }

    // call for generating avatar
    private suspend fun generateAvatar(): String = remoteDataSource.getAvatar(getRandomQuery())

    //get random query for query for avatar
    private fun getRandomQuery(): String {
        val r = (Math.random() * 3000).toInt()
        return when (r % 3) {
            0 -> "male"
            1 -> "female"
            2 -> "pixel"
            else -> "male"
        }
    }

    // add filters to images and create temp files on background thread
    suspend fun filterImages(bitmap: Bitmap): List<Uri> {
        deleteFilterImages()
        val filteredImages = mutableListOf<Uri>()
        val folder: File? = applicationContext.getExternalFilesDir("tmpFiltered")
        if (folder != null) {
            savePngFile("green_img", folder, getGreenScaleBitmap(bitmap))?.let { filteredImages.add( 0, it) }
            savePngFile("grayscale_img", folder, getGrayScaleBitmap(bitmap))?.let { filteredImages.add(1, it) }
            savePngFile("nofilter_img", folder, bitmap)?.let { filteredImages.add(2, it) }
        }
        return filteredImages
    }

    // delete the temp files before creating new ones.
    suspend fun deleteFilterImages() {
        val folder: File? = applicationContext.getExternalFilesDir("tmpFiltered")
        if (folder != null) {
            deletePngFile("green_img", folder)
            deletePngFile("grayscale_img", folder)
            deletePngFile("nofilter_img", folder)
        }
    }

    // save the chosen image after filtered
    suspend fun saveChosenImage(chosenUri: Uri): Uri? {
        val folder: File? = applicationContext.getExternalFilesDir("all_patient_images")
        val bitmap = withContext(Dispatchers.IO) {
            Glide.with(applicationContext).asBitmap().load(chosenUri).submit().get()
        }
        if (folder != null) {
            return savePngFile((System.currentTimeMillis().toString()), folder, bitmap)
        } else {
            return chosenUri
        }
    }

    //save png file into memory
    private fun savePngFile(fileName : String, folder : File, bitmap: Bitmap): Uri? {
        var file = File(folder, "$fileName.png")
        file.delete()
        file = File(folder, "$fileName.png")
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            Log.d("savePngFile", e.toString()) // java.io.IOException: Operation not permitted
        }
        file = File(folder, "$fileName.png")
        return file.toUri()
    }

    //delete a png file from memory
    private fun deletePngFile(fileName : String, folder : File) {
        val file = File(folder, "$fileName.png")
        file.delete()
    }
}