package com.noam.odt_task.view

import android.content.ContentResolver
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.noam.odt_task.BuildConfig
import com.noam.odt_task.R
import com.noam.odt_task.databinding.FragmentCameraBinding
import com.noam.odt_task.view_models.PatientViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private lateinit var patientViewModel: PatientViewModel

    private val filteredImages = mutableListOf<Uri>()
    private lateinit var chosenUri : Uri
    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.imagePreview.setImageURI(uri)
                Log.d("TAG", "the uri we got is: $uri ")
                chosenUri = uri
                setFilterOptions(uri)
            }
        }
    }

    private fun setFilterOptions(uri: Uri) {
        Log.d("TAG", "the uri we got is: $uri ")
        val contentResolver: ContentResolver = requireContext().contentResolver
        val source = ImageDecoder.createSource(contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source)
//        binding.greenScaleFilter.setImageBitmap(bitmap)
        patientViewModel.getFilteredImages(bitmap)
        binding.greenScaleFilter.setImageResource(R.drawable.loading)
        binding.grayScaleFilter.setImageResource(R.drawable.loading)
        binding.noFilter.setImageResource(R.drawable.loading)
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
        patientViewModel = ViewModelProvider(requireActivity())[PatientViewModel::class.java]
        patientViewModel.filteredImages.observe(viewLifecycleOwner) {
            Log.d("TAG", "fragment getFilteredImages: filtered images has ${filteredImages.size} elements and observed list has ${it.size}")
            filteredImages.clear()
            filteredImages.addAll(it)
            setFilteredOptions()
        }
        setClickListeners()
    }

    private fun setFilteredOptions() {
        if (filteredImages.isEmpty()) { return }
        binding.greenScaleFilter.setImageURI(filteredImages[0])
        binding.grayScaleFilter.setImageURI(filteredImages[1])
        binding.noFilter.setImageURI(filteredImages[2])

        binding.greenScaleFilter.setOnClickListener {
            binding.imagePreview.setImageURI(filteredImages[0])
            chosenUri = filteredImages[0]
        }
        binding.grayScaleFilter.setOnClickListener {
            binding.imagePreview.setImageURI(filteredImages[1])
            chosenUri = filteredImages[1]
        }
        binding.noFilter.setOnClickListener {
            binding.imagePreview.setImageURI(filteredImages[2])
            chosenUri = filteredImages[2]
        }
    }

    private fun setClickListeners() {
        binding.takeImageButton.setOnClickListener { takeImage() }
        binding.imageCancelButton.setOnClickListener {
            patientViewModel.deleteFilteredImages()
            NavHostFragment.findNavController(this@CameraFragment)
                .navigate(R.id.action_cameraFragment_to_SecondFragment)
        }
        binding.imageSaveButton.setOnClickListener {
            patientViewModel.addUriToImages(chosenUri)
            patientViewModel.deleteFilteredImages()
            NavHostFragment.findNavController(this@CameraFragment)
                .navigate(R.id.action_cameraFragment_to_SecondFragment)
        }
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

}