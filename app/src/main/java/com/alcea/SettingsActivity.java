package com.alcea;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alcea.firstStart.FirstStartPasswordActivity;
import com.alcea.models.Profile;
import com.alcea.models.Service;

public class SettingsActivity extends AbstractActivity{
    private Bundle extras;
    private TextView profileTitle;
    @Override
    protected void initialize() {
        setContentView(R.layout.settings);
        extras = getIntent().getExtras();

        profileTitle = findViewById(R.id.profile_title);
        String profileText = profileTitle.getText().toString() + extras.getString("profile");
        profileTitle.setText(profileText);

        Button editProfileButton = findViewById(R.id.button_edit);
        Button deleteProfileButton = findViewById(R.id.button_delete);
        editProfileButton.setOnClickListener(v -> showEditProfileDialog());
        deleteProfileButton.setOnClickListener(v -> showDeleteProfileDialog());

    }

    private void showEditProfileDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Информация о " + extras.getString("profile"));
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_profile, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Button saveButton = dialogView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            EditText profileNameEditText = dialogView.findViewById(R.id.profile_name);
            String profileName = profileNameEditText.getText().toString();
            if(!profileName.equals(extras.getString("profile"))){
                Profile profile = databaseManager.getProfile(extras.getString("profile"));
                profile.setName(profileName);
                databaseManager.updateProfile(profile);
                String profileText = "Профиль: " + profileName;
                profileTitle.setText(profileText);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showDeleteProfileDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Удалить " + extras.getString("profile") + "?");
        builder.setMessage("Вы действительно хотите удалить этот профиль? Восстановить профиль после удаления невозможно");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_delete, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button deleteButton = dialogView.findViewById(R.id.delete_button);
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
        deleteButton.setOnClickListener(v -> {
            Profile profile = databaseManager.getProfile(extras.getString("profile"));
            databaseManager.deleteProfile(profile);
            Intent intent;
            if(databaseManager.getProfiles().isEmpty()){
                intent = new Intent(this, FirstStartPasswordActivity.class);
            }
            else{
                intent = new Intent(this, LoginActivity.class);
            }
            transfer(intent);
            dialog.dismiss();
        });
        dialog.show();
    }
}
