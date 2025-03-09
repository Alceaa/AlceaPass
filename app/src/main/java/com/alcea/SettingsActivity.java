package com.alcea;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.alcea.firstStart.FirstStartPasswordActivity;
import com.alcea.models.Profile;
import com.alcea.utils.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.TreeMap;

public class SettingsActivity extends AbstractActivity{
    private Bundle extras;
    private TextView profileTitle;
    private Profile profile;
    @SuppressLint("UseSwitchCompatOrMaterialCode") private Switch biometricAuth;
    @SuppressLint("UseSwitchCompatOrMaterialCode") private Switch requestFingerprint;
    @Override
    protected void initialize() {
        setContentView(R.layout.settings);
        extras = getIntent().getExtras();
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        profile = databaseManager.getProfile(extras.getString("profile"));

        profileTitle = findViewById(R.id.profile_title);
        String profileText = profileTitle.getText().toString() + profile.getName();
        profileTitle.setText(profileText);
        profileTitle.setOnClickListener(v ->{
            PopupMenu popupMenu = new PopupMenu(this, profileTitle);
            popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if(menuItem.getItemId() == R.id.exit){
                    Intent i = new Intent(this, LoginActivity.class);
                    transfer(i);
                    return true;
                }
                else{
                    return onOptionsItemSelected(menuItem);
                }
            });
            popupMenu.show();
        });
        Button createProfileButton = findViewById(R.id.button_create);
        Button editProfileButton = findViewById(R.id.button_edit);
        Button deleteProfileButton = findViewById(R.id.button_delete);
        createProfileButton.setOnClickListener(v -> {
            Intent i = new Intent(this, FirstStartPasswordActivity.class);
            transfer(i);
        });
        editProfileButton.setOnClickListener(v -> showEditProfileDialog());
        deleteProfileButton.setOnClickListener(v -> showDeleteProfileDialog());

        Button saveSettingsButton = findViewById(R.id.settings_save_button);
        saveSettingsButton.setOnClickListener(v -> {
            if(checkSettingsDiff()){
                showMasterPasswordDialog();
            }else{
                transferToMain();
            }
        });

        biometricAuth = findViewById(R.id.switch_biometric_auth);
        requestFingerprint = findViewById(R.id.switch_request_fingerprint);
        biometricAuth.setChecked(prefs.getBoolean("biometricEnable", false));
        requestFingerprint.setChecked(prefs.getBoolean("requestFingerprint", false));

    }
    private void showEditProfileDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Информация о " + profile.getName());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_profile, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Button saveButton = dialogView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            EditText profileNameEditText = dialogView.findViewById(R.id.profile_name);
            String profileName = profileNameEditText.getText().toString();
            if(!profileName.equals(profile.getName())){
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
        builder.setTitle("Удалить " + profile.getName() + "?");
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

    private void showMasterPasswordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Подтвердите действие");
        builder.setMessage("Введите мастер-пароль для подтверждения");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_master_password, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Button continueButton = dialogView.findViewById(R.id.continue_button);
        EditText masterPassword = dialogView.findViewById(R.id.master_password);
        TextView masterPasswordError = dialogView.findViewById(R.id.master_password_error);
        continueButton.setOnClickListener(v -> {
            String masterPasswordText = masterPassword.getText().toString();
            if(masterPasswordText.isEmpty()){
                masterPasswordError.setText("Ошибка, заполните поле пароля");
            }
            else{
                try {
                    if(PasswordEncoder.authenticate(masterPasswordText, profile.getMaster(), profile.getSalt())){
                        saveSettings();
                        transferToMain();
                    }
                    else{
                        masterPasswordError.setText("Ошибка, пароль неверный");
                    }
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        dialog.show();
    }

    private void saveSettings(){
        prefs.edit().putBoolean("biometricEnable", biometricAuth.isChecked()).apply();
        prefs.edit().putBoolean("requestFingerprint", requestFingerprint.isChecked()).apply();
    }

    private boolean checkSettingsDiff(){
        return (biometricAuth.isChecked() != prefs.getBoolean("biometricEnable", false)
                || requestFingerprint.isChecked() != prefs.getBoolean("requestFingerprint", false));
    }

    private void transferToMain(){
        Intent i = new Intent(this, MainActivity.class);
        transfer(i);
    }
}
