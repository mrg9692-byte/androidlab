package com.example.androidlab;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ResultDialogFragment extends DialogFragment {
    public interface ResultDialogListener {
        void onSaveConversion(double fromAmount, String fromCurrency, double toAmount, String toCurrency);
    }

    static ResultDialogFragment newInstance(String resultText, double fromAmount, String fromCurrency, double toAmount, String toCurrency) {
        ResultDialogFragment f = new ResultDialogFragment();
        Bundle args = new Bundle();
        args.putString("result", resultText);
        args.putDouble("fromAmount", fromAmount);
        args.putString("fromCurrency", fromCurrency);
        args.putDouble("toAmount", toAmount);
        args.putString("toCurrency", toCurrency);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String result = getArguments().getString("result");
        double fromAmount = getArguments().getDouble("fromAmount");
        String fromCurrency = getArguments().getString("fromCurrency");
        double toAmount = getArguments().getDouble("toAmount");
        String toCurrency = getArguments().getString("toCurrency");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Conversion Result")
                .setMessage(result)
                .setPositiveButton("OK", (d, which) -> { /* no-op */ })
                .setNegativeButton("Close", null);
        return builder.create();
    }
}