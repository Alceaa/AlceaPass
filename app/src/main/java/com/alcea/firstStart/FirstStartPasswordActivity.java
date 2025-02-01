package com.alcea.firstStart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.alcea.MainActivity;
import com.alcea.R;
import com.alcea.database.DatabaseManager;
import com.alcea.fragments.CustomDialogFragment;
import com.alcea.interfaces.Transferable;
import com.alcea.utils.CheckPassword;
import com.google.android.material.textfield.TextInputEditText;

public class FirstStartPasswordActivity extends AppCompatActivity implements Transferable {
    TextInputEditText profile;
    TextInputEditText masterPassword;
    Button create;
    private final DatabaseManager databaseManager = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firststart);
        profile = findViewById(R.id.profile);
        masterPassword = findViewById(R.id.masterPassword);
        create = findViewById(R.id.create);
        create.setOnClickListener(v -> create());

        databaseManager.open();
    }

    private void create(){
        String profileText = String.valueOf(profile.getText());
        String masterPasswordText = String.valueOf(profile.getText());
        if(profileText.isEmpty() || masterPasswordText.isEmpty()){
            showCustomDialog("Ошибка", "Заполните все поля", null, "ОК", null);
        }
        else{
            if(databaseManager.getProfile(profileText) == null){
                if(!CheckPassword.checkPasswordValid(masterPasswordText)){
                    showCustomDialog("Ошибка", "Пароль содержит недопустимые символы", null, "ОК", null);
                }
                else if(!CheckPassword.checkPasswordStrong(masterPasswordText)){
                    showCustomDialog("Предупреждение", "Пароль слишком слабый. Для безопасности рекомендуется его усилить",
                            "main", "Все равно продолжить", "Отмена");
                }
                else{
                    databaseManager.createProfile(profileText);
                    transfer(new Intent(this, MainActivity.class));
                }
            }
            else{
                showCustomDialog("Ошибка", "Профиль с таким именем уже существует", null, "ОК", null);
            }
        }
    }

    private void showCustomDialog(String title, String message, String activity, String positive, String negative){
        CustomDialogFragment dialog = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("activity", activity);
        args.putString("positive", positive);
        args.putString("negative", negative);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "custom");
    }

    public void transfer(Intent intent){
        startActivity(intent);
        finish();
    }
}
