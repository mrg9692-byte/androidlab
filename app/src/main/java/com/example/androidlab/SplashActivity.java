package com.example.androidlab;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Theme-based splash (no setContentView). Immediately forward to MainActivity.
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
