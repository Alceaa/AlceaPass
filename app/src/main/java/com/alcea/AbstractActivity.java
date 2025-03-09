package com.alcea;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.alcea.database.DatabaseManager;
import com.alcea.interfaces.Transferable;

public abstract class AbstractActivity extends AppCompatActivity implements Transferable {
    protected DatabaseManager databaseManager;
    protected SharedPreferences prefs;

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


    public void transfer(Intent intent){
        startActivity(intent);
        finish();
    }
}
