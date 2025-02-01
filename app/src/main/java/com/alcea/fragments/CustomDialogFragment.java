package com.alcea.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.alcea.LoginActivity;
import com.alcea.MainActivity;
import com.alcea.interfaces.Transferable;

import java.util.HashMap;
import java.util.Map;

public class CustomDialogFragment extends DialogFragment {
    private Transferable transferable;
    Map<String, Class> activities = new HashMap<>();

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        transferable = (Transferable) context;
        activities.put("main", MainActivity.class);
        activities.put("login", LoginActivity.class);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        String activity = getArguments().getString("activity");
        String positive = getArguments().getString("positive");
        String negative = getArguments().getString("negative");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       builder
            .setTitle(title)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setNegativeButton(negative, null)
            .setMessage(message);
       if(activity != null){
           builder.setPositiveButton(positive, (dialog, which) ->
                   transferable.transfer(new Intent((Context) transferable, activities.get(activity))));
       }
       else{
           builder.setPositiveButton(positive, null);
       }
       return builder.create();
    }
}
