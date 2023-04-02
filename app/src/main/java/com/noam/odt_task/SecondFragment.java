package com.noam.odt_task;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import dagger.hilt.android.AndroidEntryPoint;

import com.noam.odt_task.databinding.FragmentSecondBinding;
import com.noam.odt_task.model.Patient;
import com.noam.odt_task.view_models.PatientViewModel;

@AndroidEntryPoint
public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private PatientViewModel patientViewModel;
    private Patient patient;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        patientViewModel = new ViewModelProvider(requireActivity()).get(PatientViewModel.class);
        patient = patientViewModel.getPatientChosenForExam();

        Log.d("TAG", "onViewCreated: patient chosen for exam is "+patient.getName());
        binding.patientExamHeadline.setText(getString(R.string.patient_exam_headline, patient.getName()));
        binding.numberOfImagesTxt.setText(getString(R.string.exam_images_number, patient.getImages().size()));
        binding.clinicianNotesEditText.setText(patient.getClinicianNotes());

        binding.addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_cameraFragment);
            }
        });
        binding.negativeTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
        binding.positiveTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}