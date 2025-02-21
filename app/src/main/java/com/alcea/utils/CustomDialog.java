package com.alcea.utils;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.alcea.fragments.CustomDialogFragment;

public class CustomDialog {
    private FragmentActivity context;
    private CustomDialogListener callback;
    public CustomDialog(FragmentActivity context, CustomDialogListener callback){
        this.context = context;
        this.callback = callback;
    }
    public CustomDialogFragment showCustomDialog(String title, String message, String positive, String negative){
        CustomDialogFragment dialog = new CustomDialogFragment(callback);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("positive", positive);
        args.putString("negative", negative);
        dialog.setArguments(args);
        dialog.show(context.getSupportFragmentManager(), "custom");
        return dialog;
    }

    public interface CustomDialogListener{
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }
}
