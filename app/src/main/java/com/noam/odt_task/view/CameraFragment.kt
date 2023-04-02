package com.noam.odt_task.view

import android.R.attr.bitmap
import android.content.ContentResolver
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.noam.odt_task.BuildConfig
import com.noam.odt_task.databinding.FragmentCameraBinding
import java.io.File
import java.util.*


class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.imagePreview.setImageURI(uri)
                setFilterOptions(uri)
            }
        }
    }

    private fun setFilterOptions(imageUri: Uri) {
        var bitmap: Bitmap? = null
        val contentResolver: ContentResolver = requireContext().contentResolver
        try {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } else {
                val source = ImageDecoder.createSource(contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.grayScaleFilter.setImageBitmap(bitmap?.let { getGrayScaleBitmap(it) })
        binding.greenScaleFilter.setImageBitmap(bitmap?.let { getGreenScaleBitmap(it) })
        binding.noFilter.setImageBitmap(bitmap)
    }

    private var latestTmpUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.takeImageButton.setOnClickListener { takeImage() }
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", requireContext().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireContext().applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

//    @Throws(IOException::class)
//    private fun createImageFile(): File? {
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val imageFileName = "JPEG_" + timeStamp + "_"
//        val storageDir =
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES
//            ) else requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val image = File.createTempFile(
//            imageFileName,  /* prefix */
//            ".jpg",  /* suffix */
//            storageDir /* directory */
//        )
//        currentPhotoPath = image.absolutePath
//        return image
//    }

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
}