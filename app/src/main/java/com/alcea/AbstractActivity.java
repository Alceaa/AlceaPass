package com.alcea;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.alcea.database.DatabaseManager;
import com.alcea.fragments.CustomDialogFragment;
import com.alcea.interfaces.DialogBehaviour;
import com.alcea.interfaces.Transferable;

public abstract class AbstractActivity extends AppCompatActivity implements Transferable {
    protected DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager = new DatabaseManager(this);
        databaseManager.open();
        initialize();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        databaseManager.close();
    }

    protected abstract void initialize();

    protected CustomDialogFragment showCustomDialog(String title, String message, String positive, String negative){
        CustomDialogFragment dialog = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("positive", positive);
        args.putString("negative", negative);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "custom");
        return dialog;
    }

    public void transfer(Intent intent){
        startActivity(intent);
        finish();
    }
}
