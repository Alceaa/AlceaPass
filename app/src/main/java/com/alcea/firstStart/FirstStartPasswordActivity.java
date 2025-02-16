package com.alcea.firstStart;

import android.content.Intent;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;
import com.alcea.AbstractActivity;
import com.alcea.MainActivity;
import com.alcea.R;
import com.alcea.interfaces.DialogBehaviour;
import com.alcea.models.Password;
import com.alcea.models.Profile;
import com.alcea.utils.CheckPassword;
import com.alcea.utils.PasswordEncoder;
import com.google.android.material.textfield.TextInputEditText;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class FirstStartPasswordActivity extends AbstractActivity implements DialogBehaviour {
    TextInputEditText profile;
    TextInputEditText masterPassword;
    Button create;
    private String profileText;
    private String masterPasswordText;

    @Override
    protected void initialize() {
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
        if(profileText.isEmpty() || masterPasswordText.isEmpty()){
            showCustomDialog("Ошибка", "Заполните все поля", "ОК", null);
        }
        else{
            if(databaseManager.getProfile(profileText) == null){
                if(!CheckPassword.checkPasswordValid(masterPasswordText)){
                    showCustomDialog("Ошибка", "Пароль содержит недопустимые символы", "ОК", null);
                }
                else if(!CheckPassword.checkPasswordStrong(masterPasswordText)){
                    showCustomDialog("Предупреждение", "Пароль слишком слабый. Для безопасности рекомендуется его усилить", "Изменить пароль", "Всё равно продолжить");
                }
                else{
                    success();
                }
            }
            else{
                showCustomDialog("Ошибка", "Профиль с таким именем уже существует", "ОК", null);
            }
        }
    }

    private void success() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Password encoded = PasswordEncoder.hash(masterPasswordText);
        String master = encoded.hash;
        String salt = encoded.salt;
        Profile profile = new Profile();
        profile.setName(profileText);
        profile.setMaster(master);
        profile.setSalt(salt);
        databaseManager.createProfile(profile);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("master", master);
        transfer(i);
    }
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
}
