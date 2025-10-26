package com.example.androidlab; // This package name is correct for your project

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CurrencyConverter"; // Tag for logging

    private EditText amountInput;
    private Spinner fromCurrency, toCurrency;
    private Button convertBtn;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountInput = findViewById(R.id.amountInput);
        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);
        convertBtn = findViewById(R.id.convertBtn);
        resultText = findViewById(R.id.resultText);

        List<String> currencies = Arrays.asList("USD", "EUR", "INR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY", "NZD");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromCurrency.setAdapter(adapter);
        toCurrency.setAdapter(adapter);

        fromCurrency.setSelection(adapter.getPosition("USD"));
        toCurrency.setSelection(adapter.getPosition("INR"));

        convertBtn.setOnClickListener(v -> convertCurrency());
    }

    private void convertCurrency() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = amountInput.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        final double amount = Double.parseDouble(amountStr);
        final String from = fromCurrency.getSelectedItem().toString();
        final String to = toCurrency.getSelectedItem().toString();
        final String urlStr = "https://api.exchangerate-api.com/v4/latest/" + from;

        new Thread(() -> {
            try {
                String jsonResponse = fetchConversionData(urlStr);
                JSONObject json = new JSONObject(jsonResponse);
                double rate = json.getJSONObject("rates").getDouble(to);
                double convertedAmount = amount * rate;

                runOnUiThread(() -> resultText.setText(
                        String.format("%.2f %s = %.2f %s", amount, from, convertedAmount, to)
                ));

            } catch (IOException | JSONException e) {
                // Use robust logging instead of printStackTrace()
                Log.e(TAG, "Error during currency conversion", e);
                runOnUiThread(() -> {
                    resultText.setText("");
                    Toast.makeText(MainActivity.this, "Error fetching conversion rate.", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    /**
     * Extracts the network call to its own method for better readability.
     *
     * @param urlStr The URL to fetch data from.
     * @return The JSON response as a String.
     * @throws IOException if there's a network error.
     */
    private String fetchConversionData(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Checks if the device has an active internet connection.
     * This method is updated to use modern APIs for newer Android versions.
     *
     * @return true if connected, false otherwise.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        // For Android 10 (API 29) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            // For older versions
            // The @SuppressWarnings is used because getActiveNetworkInfo is deprecated,
            // but we need it for devices before API 29.
            @SuppressWarnings("deprecation")
            android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
}
