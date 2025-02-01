package com.alcea;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alcea.database.DatabaseManager;

public class LoginActivity extends AppCompatActivity {
    private final DatabaseManager databaseManager = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        databaseManager.open();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        databaseManager.close();
    }
}
