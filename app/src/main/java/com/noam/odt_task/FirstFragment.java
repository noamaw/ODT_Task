package com.noam.odt_task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

import com.noam.odt_task.databinding.FragmentFirstBinding;
import com.noam.odt_task.model.Patient;
import com.noam.odt_task.view.PatientAdapterOnClickListener;
import com.noam.odt_task.view.PatientDataAdapter;
import com.noam.odt_task.view.ViewHelper;
import com.noam.odt_task.view_models.PatientViewModel;

import java.util.List;

import static com.noam.odt_task.view.ViewHelper.openConfirmationDialog;
import static com.noam.odt_task.view.ViewHelper.openNewPatientDialog;

@AndroidEntryPoint
public class FirstFragment extends Fragment implements PatientAdapterOnClickListener {

    private FragmentFirstBinding binding;
    private PatientViewModel patientViewModel;
    private PatientDataAdapter patientDataAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        patientViewModel = new ViewModelProvider(requireActivity()).get(PatientViewModel.class);
        RecyclerView recyclerView = binding.viewPatients;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        patientDataAdapter = new PatientDataAdapter(this);
        recyclerView.setAdapter(patientDataAdapter);
        addSwipeActionsToRecyclerView();

        observePatients();
    }

    // Observing the live data
    private void observePatients() {
        patientViewModel.getPatients().observe(getViewLifecycleOwner(), new Observer<List<Patient>>() {
            @Override
            public void onChanged(List<Patient> patients) {
                patientDataAdapter.setPatientList(patients);
            }
        });
    }

    private void openAddNewPatientDialog() {
        openNewPatientDialog(getContext(),
                "Add new Patient", "",
                "Add", "Cancel", new ViewHelper.DialogButtonInterface() {
                    @Override
                    public void onPositiveButtonClicked(String info) {
                        patientViewModel.setPatientChosenForExam(new Patient(info));
                        NavHostFragment.findNavController(FirstFragment.this)
                                .navigate(R.id.action_FirstFragment_to_SecondFragment);
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public Boolean checkUnique(String name) {
                        return patientViewModel.checkUnique(name);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClickListener(Patient patient) {
        patientViewModel.setPatientChosenForExam(patient);
        patientViewModel.setPatientExamImages(patient.getImages().getImages());
        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);
    }

    @Override
    public void onAddItemClickListener() {
        openAddNewPatientDialog();
    }

    @Override
    public void onItemLongClickListener(Patient patient) {
        openConfirmationDialog(getContext(), "Export Patient", "Do you want to Export all of " + patient.getName() + "'s information?",
                "Export", "No", new ViewHelper.DialogButtonInterface() {
                    @Override
                    public void onPositiveButtonClicked(String info) {
                        patientViewModel.exportPatient(patient);
                    }

                    @Override
                    public void onNegativeButtonClicked() {}

                    @Override
                    public Boolean checkUnique(String name) {
                        return null;
                    }
                });
    }

    private void addSwipeActionsToRecyclerView() {
        // on below line we are creating a method to create item touch helper
        // method for adding swipe to delete functionality.
        // in this we are specifying drag direction and position to right
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // this method is called
                // when the item is moved.
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                List<Patient> patients = patientViewModel.getPatients().getValue();
                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                assert patients != null;
                Patient deletedPatient = patients.get(viewHolder.getBindingAdapterPosition() - 1);
                // below line is to get the position
                // of the item at that position.
                int position = viewHolder.getBindingAdapterPosition() - 1;

                openConfirmationDialog(getContext(), "Delete Patient?",
                        "Are you sure you want to delete " + deletedPatient.getName() + "'s information?",
                        "Delete", "Undo", new ViewHelper.DialogButtonInterface() {
                            @Override
                            public void onPositiveButtonClicked(String info) {
                                patientViewModel.deletePatient(deletedPatient);
                                patientDataAdapter.notifyItemRemoved(position + 1);
                            }

                            @Override
                            public void onNegativeButtonClicked() {
                                patientDataAdapter.notifyItemInserted(position);
                            }

                            @Override
                            public Boolean checkUnique(String name) { return null; }
                        });

            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(binding.viewPatients);
    }
}