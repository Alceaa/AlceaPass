package com.alcea;

import android.content.Intent;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.biometric.BiometricManager;
import androidx.fragment.app.DialogFragment;
import com.alcea.adapters.CustomSpinnerAdapter;
import com.alcea.firstStart.FirstStartPasswordActivity;
import com.alcea.models.Profile;
import com.alcea.utils.Biometric;
import com.alcea.utils.CustomDialog;
import com.alcea.utils.PasswordEncoder;
import com.google.android.material.textfield.TextInputEditText;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class LoginActivity extends AbstractActivity {
    TextInputEditText masterPassword;
    Button login;
    Spinner profileSpinner;


    private Profile profile;


    @Override
    protected void initialize() {
        setContentView(R.layout.login);
        masterPassword = findViewById(R.id.masterPassword);
        login = findViewById(R.id.login);
        profileSpinner = findViewById(R.id.profileSpinner);

        List<Profile> profiles = databaseManager.getProfiles();
        if(profiles.isEmpty()){
            transfer(new Intent(this, FirstStartPasswordActivity.class));
            finish();
        }
        String[] profileNames = new String[profiles.size()];
        for(int i = 0; i < profiles.size(); i++){
            profileNames[i] = profiles.get(i).getName();
        }
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, profileNames);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        profileSpinner.setAdapter(adapter);
        BiometricManager biometricManager = BiometricManager.from(this);
        Biometric biometric = new Biometric(this, new Biometric.AuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess() {
                transferToMain();
            }

            @Override
            public void onAuthenticationError(String error) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
        if(Biometric.checkBiometricAvailable(biometricManager, this)){
            biometric.showBiometricDialog();
        }
        login.setOnClickListener(v -> {
            try {
                login();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void login() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String masterPasswordText = String.valueOf(masterPassword.getText());
        CustomDialog customDialog = new CustomDialog(this, new CustomDialog.CustomDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                dialog.dismiss();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                dialog.dismiss();
            }
        });
        if(masterPasswordText.isEmpty()){
            customDialog.showCustomDialog("Ошибка", "Заполните поле пароля", "ОК", null);
        }
        else{
            profile = databaseManager.getProfile(profileSpinner.getSelectedItem().toString());
            if(PasswordEncoder.authenticate(masterPasswordText, profile.getMaster(), profile.getSalt())){
                transferToMain();
            }
            else{
                customDialog.showCustomDialog("Ошибка", "Пароль неверный",  "ОК", null);
            }
        }
    }


    private void transferToMain(){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("master", profile.getMaster());
        transfer(i);
    }

}
