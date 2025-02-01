package com.alcea.firstStart;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.alcea.LoginActivity;

public class FirstStartHelperActivity extends Activity {
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("FirstStart", MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).apply();
            startActivity(new Intent(FirstStartHelperActivity.this , FirstStartPasswordActivity.class));
            finish();
        }
        else {
            startActivity(new Intent(FirstStartHelperActivity.this , LoginActivity.class));
            finish();
        }
    }
}
