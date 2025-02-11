package com.alcea.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.alcea.LoginActivity;
import com.alcea.MainActivity;
import com.alcea.interfaces.DialogBehaviour;

import java.util.HashMap;
import java.util.Map;

public class CustomDialogFragment extends DialogFragment {
    private DialogBehaviour listener;
    Map<String, Class> activities = new HashMap<>();

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        try {
            listener = (DialogBehaviour) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException();
        }
        activities.put("main", MainActivity.class);
        activities.put("login", LoginActivity.class);
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
