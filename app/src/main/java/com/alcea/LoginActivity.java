package com.alcea;

import android.content.Intent;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import com.alcea.adapters.CustomSpinnerAdapter;
import com.alcea.firstStart.FirstStartPasswordActivity;
import com.alcea.interfaces.DialogBehaviour;
import com.alcea.models.Profile;
import com.alcea.utils.PasswordEncoder;
import com.google.android.material.textfield.TextInputEditText;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class LoginActivity extends AbstractActivity implements DialogBehaviour {
    TextInputEditText masterPassword;
    Button login;
    Spinner profileSpinner;


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
        if(masterPasswordText.isEmpty()){
            showCustomDialog("Ошибка", "Заполните поле пароля", "ОК", null);
        }
        else{
            Profile profile = databaseManager.getProfile(profileSpinner.getSelectedItem().toString());
            if(PasswordEncoder.authenticate(masterPasswordText, profile.getMaster(), profile.getSalt())){
                transfer(new Intent(this, MainActivity.class));
            }
            else{
                showCustomDialog("Ошибка", "Пароль неверный",  "ОК", null);
            }
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
