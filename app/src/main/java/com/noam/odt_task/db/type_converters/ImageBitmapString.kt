package com.noam.odt_task.db.type_converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream


public class ImageBitmapString {
    companion object {
        // static function to convert string (base64) to a bitmap
        fun stringToBitmap(encodedString: String?): Bitmap? {
            return try {
                val encodeByte: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
                bitmap
            } catch (e: Exception) {
                e.message
                null
            }
        }
        // static function to convert bitmap to a string (base64)
        fun bitmapToString(bitmap: Bitmap): String? {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b: ByteArray = baos.toByteArray()
            val temp: String = Base64.encodeToString(b, Base64.DEFAULT)
            return if (temp == null) {
                null
            } else temp
        }
    }
}