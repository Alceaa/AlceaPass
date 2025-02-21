package com.alcea.firstStart;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import com.alcea.AbstractActivity;
import com.alcea.MainActivity;
import com.alcea.R;
import com.alcea.models.Password;
import com.alcea.models.Profile;
import com.alcea.utils.Biometric;
import com.alcea.utils.CheckPassword;
import com.alcea.utils.CustomDialog;
import com.alcea.utils.PasswordEncoder;
import com.google.android.material.textfield.TextInputEditText;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class FirstStartPasswordActivity extends AbstractActivity {
    TextInputEditText profile;
    TextInputEditText masterPassword;
    Button create;
    private String profileText;
    private String masterPasswordText;

    private String master;
    private Context context;

    @Override
    protected void initialize() {
        context = this;
        setContentView(R.layout.firststart);
        profile = findViewById(R.id.profile);
        masterPassword = findViewById(R.id.masterPassword);
        create = findViewById(R.id.create);
        create.setOnClickListener(v -> {
            try {
                create();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void create() throws NoSuchAlgorithmException, InvalidKeySpecException {
        profileText = String.valueOf(profile.getText());
        masterPasswordText = String.valueOf(masterPassword.getText());
        CustomDialog customDialog = new CustomDialog(this, new CustomDialog.CustomDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                dialog.dismiss();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                try {
                    success();
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        if(profileText.isEmpty() || masterPasswordText.isEmpty()){
            customDialog.showCustomDialog("Ошибка", "Заполните все поля", "ОК", null);
        }
        else{
            if(databaseManager.getProfile(profileText) == null){
                if(!CheckPassword.checkPasswordValid(masterPasswordText)){
                    customDialog.showCustomDialog("Ошибка", "Пароль содержит недопустимые символы", "ОК", null);
                }
                else if(!CheckPassword.checkPasswordStrong(masterPasswordText)){
                    customDialog.showCustomDialog("Предупреждение", "Пароль слишком слабый. Для безопасности рекомендуется его усилить", "Изменить пароль", "Всё равно продолжить");
                }
                else{
                    success();
                }
            }
            else{
                customDialog.showCustomDialog("Ошибка", "Профиль с таким именем уже существует", "ОК", null);
            }
        }
    }

    private void success() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Password encoded = PasswordEncoder.hash(masterPasswordText);
        master = encoded.hash;
        String salt = encoded.salt;
        Profile profile = new Profile();
        profile.setName(profileText);
        profile.setMaster(master);
        profile.setSalt(salt);
        databaseManager.createProfile(profile);

        showBiometricDialog();

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("master", master);
        transfer(i);
    }

    public void showBiometricDialog(){
        CustomDialog customDialog = new CustomDialog(this, new CustomDialog.CustomDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                Biometric biometric = new Biometric(context, new Biometric.AuthenticationCallback() {
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
                biometric.showBiometricDialog();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                dialog.dismiss();
            }
        });
        customDialog.showCustomDialog("Включить аутентификацию по отпечатку пальца", "Хотите включить аутентификацию по отпечатку пальца для повышения безопасности?", "Да", "Нет");

    }

    private void transferToMain(){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("master", master);
        transfer(i);
    }
}
