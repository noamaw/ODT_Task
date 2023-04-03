package com.noam.odt_task.db.type_converters

import androidx.room.TypeConverter
import com.noam.odt_task.model.Images

class ImageConverter {
    // converter for string value to list of Images for storing it in the Room Database
    @TypeConverter
    fun storedStringToImages(value: String): Images {
        val images: List<String> = mutableListOf(*value.split("\\s*,\\s*".toRegex()).toTypedArray())
        return Images(images)
    }

    // converter for images data class (list of string) to convert into one string
    @TypeConverter
    fun imagesToStoredString(images: Images): String {
        var value = ""
        for (image in images.images) value += "$image,"
        return value
    }
}