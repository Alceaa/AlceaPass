package com.alcea;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alcea.database.DatabaseManager;

public class MainActivity extends AppCompatActivity {
    private final DatabaseManager databaseManager = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseManager.open();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        databaseManager.close();
    }
}
