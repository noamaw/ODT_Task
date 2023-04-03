package com.noam.odt_task.util

import android.graphics.*

    // get a grayscale image from an original image by setting sat to 0
    fun getGrayScaleBitmap(original: Bitmap): Bitmap {
        // You have to make the Bitmap mutable when changing the config because there will be a crash
        // That only mutable Bitmap's should be allowed to change config.
        val bmp = original.copy(Bitmap.Config.ARGB_8888, true)
        val bmpGrayscale = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpGrayscale)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixFilter
        canvas.drawBitmap(bmp, 0F, 0F, paint)
        return bmpGrayscale
    }

    //getting a green channel image by highlighting the green on each pixel of the image
    fun getGreenScaleBitmap(original: Bitmap): Bitmap {
        // You have to make the Bitmap mutable when changing the config because there will be a crash
        // That only mutable Bitmap's should be allowed to change config.
        val bmp = original.copy(Bitmap.Config.ARGB_8888, true)
        for (x in 0 until bmp.width) {
            for (y in 0 until bmp.height) {
                bmp.setPixel(x, y, bmp.getPixel(x, y) and -0xff0100)
            }
        }
        return bmp
    }
