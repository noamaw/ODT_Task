package com.noam.odt_task.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.noam.odt_task.R;

public class ViewHelper {
    //define callback interface
    public interface DialogButtonInterface {
        void onPositiveButtonClicked(String info);

        void onNegativeButtonClicked();

        Boolean checkUnique(String name);
    }

    public static void openConfirmationDialog(Context context, String title, String message, String positiveBtn, String negativeBtn, DialogButtonInterface buttonInterface) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(positiveBtn, (dialog, which) -> {
                    // Continue with delete operation
                    buttonInterface.onPositiveButtonClicked("");
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(negativeBtn, (dialog, which) -> {
                    buttonInterface.onNegativeButtonClicked();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void openNewPatientDialog(Context context, String title, String message, String positiveBtn, String negativeBtn, DialogButtonInterface buttonInterface) {
        Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        TextView titleText = dialog.findViewById(R.id.title_txt);
        TextView userInputTitle = dialog.findViewById(R.id.user_input_txt);
        TextView errorText = dialog.findViewById(R.id.error_txt);
        EditText userInput = dialog.findViewById(R.id.user_input_edit_txt);

        TextView positiveButton = dialog.findViewById(R.id.positive_txt_btn);
        TextView negativeButton = dialog.findViewById(R.id.negative_txt_btn);

        titleText.setText(title);
        userInputTitle.setText("Enter Patient name:");
        positiveButton.setText(positiveBtn);
        negativeButton.setText(negativeBtn);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredName = userInput.getText().toString();
                if (buttonInterface.checkUnique(enteredName)) {
                    buttonInterface.onPositiveButtonClicked(enteredName);
                    Toast.makeText(context, userInput.getText().toString(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText("Please enter unique name");
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonInterface.onNegativeButtonClicked();
                dialog.dismiss();
                Toast.makeText(context, "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();

    }
}