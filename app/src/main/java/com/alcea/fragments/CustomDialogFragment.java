package com.alcea.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.alcea.utils.CustomDialog;

public class CustomDialogFragment extends DialogFragment {
    private CustomDialog.CustomDialogListener listener;

    public CustomDialogFragment(CustomDialog.CustomDialogListener listener){
        this.listener = listener;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        String positive = getArguments().getString("positive");
        String negative = getArguments().getString("negative");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
            .setTitle(title)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setNegativeButton(negative, null)
            .setMessage(message);
        builder.setPositiveButton(positive, (dialog, which) ->
               listener.onDialogPositiveClick(CustomDialogFragment.this));
        builder.setNegativeButton(negative, (dialog, which) ->
                listener.onDialogNegativeClick(CustomDialogFragment.this));
        return builder.create();
    }
}
