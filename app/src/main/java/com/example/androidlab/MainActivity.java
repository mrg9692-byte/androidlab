package com.example.androidlab;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ResultDialogFragment.ResultDialogListener {
    private EditText amountInput;
    private Spinner fromCurrency, toCurrency;
    private Button convertBtn, clearBtn;
    private ImageButton themeToggle;
    private ProgressBar progressBar;

    private static final String PREFS = "converter_prefs";
    private static final String KEY_THEME = "theme_mode"; // "night" or "day"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore theme preference quickly
        String themePref = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_THEME, "day");
        if ("night".equals(themePref)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        amountInput = findViewById(R.id.amountInput);
        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);
        convertBtn = findViewById(R.id.convertBtn);
        clearBtn = findViewById(R.id.clearBtn);
        themeToggle = findViewById(R.id.themeToggle);
        progressBar = findViewById(R.id.progressBar);

        // Populate spinners with a small currency list. Replace or extend as needed.
        List<String> currencies = Arrays.asList("USD", "EUR", "INR", "JPY", "GBP", "AUD", "CAD");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrency.setAdapter(adapter);
        toCurrency.setAdapter(adapter);
        fromCurrency.setSelection(adapter.getPosition("USD"));
        toCurrency.setSelection(adapter.getPosition("INR"));

        convertBtn.setOnClickListener(v -> onConvert());
        clearBtn.setOnClickListener(v -> onClear());
        themeToggle.setOnClickListener(v -> toggleTheme());
    }

    private void onConvert() {
        String s = amountInput.getText().toString().trim();
        if (s.isEmpty()) {
            amountInput.setError("Enter amount");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            amountInput.setError("Invalid number");
            return;
        }
        if (amount <= 0) {
            amountInput.setError("Amount must be greater than 0");
            return;
        }

        String from = fromCurrency.getSelectedItem().toString();
        String to = toCurrency.getSelectedItem().toString();

        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        convertBtn.setEnabled(false);

        // Placeholder network simulation. Replace with real API call (Retrofit/OkHttp) in production.
        new Thread(() -> {
            try {
                Thread.sleep(800); // simulate latency
                // simulate a rate (TODO: replace with real API response parsing)
                double rate = 1.2345;
                double converted = amount * rate;
                String result = String.format(Locale.getDefault(), "%.2f %s = %.2f %s", amount, from, converted, to);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    convertBtn.setEnabled(true);
                    ResultDialogFragment dialog = ResultDialogFragment.newInstance(result, amount, from, converted, to);
                    dialog.show(getSupportFragmentManager(), "result_dialog");
                });
            } catch (InterruptedException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    convertBtn.setEnabled(true);
                    Toast.makeText(this, "Error fetching rate", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void onClear() {
        amountInput.setText("");
        fromCurrency.setSelection(0);
        toCurrency.setSelection(0);
    }

    private void toggleTheme() {
        String cur = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_THEME, "day");
        boolean isNight = "night".equals(cur);
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(KEY_THEME, "day").apply();
            themeToggle.setImageResource(R.drawable.ic_sun);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(KEY_THEME, "night").apply();
            themeToggle.setImageResource(R.drawable.ic_moon);
        }
    }

    // Callback from dialog — kept for interface but no history persistence (feature removed)
    @Override
    public void onSaveConversion(double fromAmount, String fromCurrency, double toAmount, String toCurrency) {
        // History feature has been removed per request. This method is intentionally empty.
    }
}
