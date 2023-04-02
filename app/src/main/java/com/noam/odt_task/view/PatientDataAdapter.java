package com.noam.odt_task.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.noam.odt_task.R;
import com.noam.odt_task.databinding.AddPatientRowBinding;
import com.noam.odt_task.databinding.PatientListItemBinding;
import com.noam.odt_task.model.Patient;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class PatientDataAdapter
        extends RecyclerView.Adapter<PatientDataAdapter.PatientViewHolder> {
    private List<Patient> patients;
    PatientAdapterOnClickListener itemClickListener;

    private final int ADD_NEW = 0;
    private final int PATIENT = 1;

    public PatientDataAdapter(PatientAdapterOnClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == ADD_NEW) {
            AddPatientRowBinding addPatientRowBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                    R.layout.add_patient_row, viewGroup, false);
            return new PatientViewHolder(addPatientRowBinding);
        }
        PatientListItemBinding patientListItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.patient_list_item, viewGroup, false);
        return new PatientViewHolder(patientListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder patientViewHolder, int position) {
        if(position > 0) {
            Patient currentStudent = patients.get(position - 1);
            patientViewHolder.patientListItemBinding.setPatient(currentStudent);
        }
    }

    @Override
    public int getItemCount() {
        if (patients != null) {
            return patients.size() + 1;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ADD_NEW;
        } else {
            return PATIENT;
        }
    }

    public void setPatientList(List<Patient> patients) {
        this.patients = patients;
        notifyDataSetChanged();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder {
        private PatientListItemBinding patientListItemBinding;
        private AddPatientRowBinding addPatientRowBinding;
        public PatientViewHolder(@NonNull PatientListItemBinding patientListItemBinding) {
            super(patientListItemBinding.getRoot());
            this.patientListItemBinding = patientListItemBinding;
            this.itemView.setOnClickListener(view -> itemClickListener.onItemClickListener(patientListItemBinding.getPatient()));
            this.itemView.setOnLongClickListener(view -> {
                itemClickListener.onItemLongClickListener(patientListItemBinding.getPatient());
                return true;
            });
        }

        public PatientViewHolder(@NonNull AddPatientRowBinding addPatientRowBinding) {
            super(addPatientRowBinding.getRoot());
            this.addPatientRowBinding = addPatientRowBinding;
            this.addPatientRowBinding.addButton.setOnClickListener(view -> itemClickListener.onAddItemClickListener());
            this.itemView.setOnClickListener(view -> itemClickListener.onAddItemClickListener());
        }
    }
}