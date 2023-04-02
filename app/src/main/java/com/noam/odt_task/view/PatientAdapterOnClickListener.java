package com.noam.odt_task.view;

import com.noam.odt_task.model.Patient;

public interface PatientAdapterOnClickListener {
    void onItemClickListener(Patient patient);
    void onAddItemClickListener();
    void onItemLongClickListener(Patient patient);
}
